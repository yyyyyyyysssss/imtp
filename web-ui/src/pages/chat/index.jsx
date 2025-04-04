import { Flex, message, Tabs } from "antd";
import React, { useCallback, useContext, useEffect, useMemo, useRef } from 'react';
import { HomeContext, useWebSocket } from '../../context';
import { getBit } from '../../utils';
import ChatItem from './chat-item';
import './index.less';
import IdGen from "../../utils/IdGen";
import { normalize, schema } from 'normalizr';
import { createUserSession, fetchUserSessions } from "../../api/ApiService";
import { loadSession, addMessage, addSession, selectSession, updateMessageStatus } from '../../redux/slices/chatSlice';
import { useDispatch, useSelector } from 'react-redux';
import UserSessionItem from "../../components/user-session-item";
import { DeliveryMethod, MessageType } from "../../enum";
import CallWrapper from "../call/CallWrapper";

const Chat = (props) => {
    const { socket } = useWebSocket()
    const { style } = props;
    //语音通话ref
    const voiceCallRef = useRef()
    //好友以及群组信息
    const { findUserInfoByGroup, findGroupByGroupId, findUserInfoByFriendId } = useContext(HomeContext);
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //会话信息map
    const sessionMapRef = useRef(new Map())
    const dispatch = useDispatch()
    //会话数据
    const result = useSelector(state => state.chat.result)
    const sessions = useSelector(state => state.chat.entities.sessions)
    //当前选中的会话id
    const selectedSessionId = useSelector(state => state.chat.selectedSessionId)
    useEffect(() => {
        const fetchData = async () => {
            const userSessionList = await fetchUserSessions() || []
            if (userSessionList.length > 0) {
                const message = new schema.Entity('messages')
                const session = new schema.Entity('sessions', {
                    messages: [message]
                })
                const normalizedData = normalize(userSessionList, [session]);
                //初始化加载会话
                dispatch(loadSession(normalizedData))
            }
        }
        fetchData()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        sessionMapRef.current = new Map()
        for (const session of Object.values(sessions)) {
            sessionMapRef.current.set(session.receiverUserId, session)
        }
    }, [result, sessions])

    //接收消息
    useEffect(() => {
        if (socket) {
            const handleReceiveEvent = async (event) => {
                const msg = JSON.parse(event.data);
                const { header } = msg;
                const { cmd, sender, receiver, reserved } = header
                //心跳包不处理 由websocket hooks处理
                if(cmd === MessageType.HEARTBEAT_PING || cmd === MessageType.HEARTBEAT_PONG){
                    return
                }
                //通用消息响应
                if (cmd === MessageType.COMMON_RESPONSE) {
                    const { ackId, state } = msg
                    dispatch(updateMessageStatus({ id: ackId, status: state }))
                    return
                }
                //授权消息响应
                if (cmd === MessageType.AUTHORIZATION_RES) {
                    return
                }
                const { contentMetadata, timestamp } = msg
                const groupBit = getBit(reserved, 0)
                const isGroup = groupBit === 1
                const deliveryMethod = isGroup ? DeliveryMethod.GROUP : DeliveryMethod.SINGLE
                const realSender = isGroup ? receiver : sender;
                // 会话是否存在 不存在则先新增会话
                let session = sessionMapRef.current.get(realSender)
                const friendInfo = findFriendInfo(sender, realSender, deliveryMethod)
                if (!session) {
                    const info = deliveryMethod === DeliveryMethod.GROUP ? findGroupByGroupId(realSender) : friendInfo
                    const sessionId = await createUserSession(realSender, deliveryMethod)
                    console.log('userInfo.id', userInfo.id)
                    session = {
                        id: sessionId,
                        userId: userInfo.id,
                        name: info.note,
                        avatar: info.avatar,
                        receiverUserId: realSender,
                        deliveryMethod: deliveryMethod
                    }
                    dispatch(addSession({ session: session }))
                }
                let content;
                switch (cmd) {
                    case MessageType.TEXT_MESSAGE:
                        content = msg.text;
                        break
                    case MessageType.IMAGE_MESSAGE:
                        content = msg.url;
                        break
                    case MessageType.VIDEO_MESSAGE:
                        content = msg.url;
                        break
                    case MessageType.FILE_MESSAGE:
                        content = msg.url;
                        break
                    case MessageType.VOICE_MESSAGE:
                        content = msg.url;
                        break
                    case MessageType.VOICE_CALL_MESSAGE:
                        content = null;
                        break
                    case MessageType.VIDEO_CALL_MESSAGE:
                        content = null;
                        break
                    case MessageType.SIGNALING_PRE_OFFER:
                        voiceCallRef.current.receiveSignalingPreOffer(session, msg.content)
                        return
                    case MessageType.SIGNALING_OFFER:
                        voiceCallRef.current.receiveSignalingOffer(msg.content)
                        return
                    case MessageType.SIGNALING_ANSWER:
                        voiceCallRef.current.receiveSignalingAnswer(msg.content)
                        return
                    case MessageType.SIGNALING_CANDIDATE:
                        voiceCallRef.current.receiveSignalingCandidate(msg.content)
                        return
                    case MessageType.SIGNALING_BUSY:
                        voiceCallRef.current.receiveSignalingBusy()
                        return
                    case MessageType.SIGNALING_CLOSE:
                        voiceCallRef.current.receiveSignalingClose()
                        return
                    default:
                        return
                }
                //添加消息
                const receiveMessage = messageBase(content, contentMetadata, cmd, timestamp, friendInfo, session)
                dispatch(addMessage({ sessionId: session.id, message: receiveMessage }))
            }
            socket.addEventListener('message', handleReceiveEvent);
            return () => {
                socket.removeEventListener('message', handleReceiveEvent);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [socket, userInfo]);

    //发送消息
    const sendMessage = useCallback((msg) => {
        socket.send(JSON.stringify(msg))
    }, [socket])

    const findFriendInfo = (friendId, groupId, deliveryMethod) => {

        return deliveryMethod === DeliveryMethod.SINGLE ? findUserInfoByFriendId(friendId) : findUserInfoByGroup(groupId, friendId)
    }

    const messageBase = (content, contentMetadata, type, timestamp, friendInfo, session) => {

        return {
            id: IdGen.nextId(),
            content: content,
            type: type,
            timestamp: timestamp,
            contentMetadata: contentMetadata,
            self: false,
            sessionId: session.id,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            name: friendInfo.note,
            avatar: friendInfo.avatar
        }
    }

    //会话选中
    const state = useRef({ x: 0 });
    const handleMouseDown = e => {
        state.current.x = e.screenX;
    }
    const handleTabClick = (activeKey, e) => {
        const delta = Math.abs(e.screenX - state.current.x);
        if (delta > 10) {
            e.preventDefault();
        } else {
            dispatch(selectSession({ sessionId: activeKey }))
        }
    }

    const items = useMemo(() => {
        return result.map((id) => ({
            key: id,
            forceRender: false,
            label: <UserSessionItem sessionId={id} />,
            children: <ChatItem sessionId={id} />
        }))
    }, [result])

    return (
        <>
            <Flex className='chat-root-flex' justify='center' align='center'>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <div className='chat-panel-tabs' style={style}>
                        <Tabs
                            activeKey={selectedSessionId}
                            destroyInactiveTabPane={true}
                            onMouseDown={handleMouseDown}
                            onTabClick={handleTabClick}
                            key="chat-tabs"
                            tabPosition='left'
                            indicator={{ size: 0 }}
                            centered
                            tabBarGutter={0}
                            items={items}
                        >
                        </Tabs>
                    </div>
                </Flex>
            </Flex>
            <CallWrapper ref={voiceCallRef} sendMessage={sendMessage} />
        </>
    );
}

export default Chat;