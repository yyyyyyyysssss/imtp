import { Flex, Tabs } from "antd";
import React, { useContext, useEffect, useRef } from 'react';
import { HomeContext, useWebSocket } from '../../context';
import { getBit } from '../../utils';
import ChatItem from './chat-item';
import './index.less';
import IdGen from "../../utils/IdGen";
import { normalize, schema } from 'normalizr';
import { createUserSession, fetchUserSessions } from "../../api/ApiService";
import { loadSession, removeSession, addMessage, addSession, selectSession, updateMessageStatus } from '../../redux/slices/chatSlice';
import { useDispatch, useSelector } from 'react-redux';
import UserSessionItem from "../../components/user-session-item";
import { DeliveryMethod, MessageType } from "../../enum";

const Chat = (props) => {
    const { socket } = useWebSocket();
    const { style } = props;
    const { findUserInfoByGroup, findGroupByGroupId, findUserInfoByFriendId } = useContext(HomeContext);
    const userInfo = useSelector(state => state.chat.userInfo) || {}
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
            if (userSessionList) {
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
    }, [result,sessions])

    //接收消息
    useEffect(() => {
        if (socket) {
            const handleReceiveEvent = async (event) => {
                const msg = JSON.parse(event.data);
                const { header } = msg;
                const { cmd, sender, receiver, reserved } = header
                //通用消息响应
                if (cmd === MessageType.COMMON_RESPONSE) {
                    const { ackId, state } = msg
                    dispatch(updateMessageStatus({ id: ackId, status: state }))
                    return
                }
                const { contentMetadata, timestamp } = msg
                const groupBit = getBit(reserved, 0)
                const isGroup = groupBit === 1
                const deliveryMethod = isGroup ? DeliveryMethod.GROUP : DeliveryMethod.SINGLE
                const realSender = isGroup ? receiver : sender;
                let content;
                switch (cmd) {
                    case MessageType.TEXT_MESSAGE:
                        content = msg.text;
                        break;
                    case MessageType.IMAGE_MESSAGE:
                        content = msg.url;
                        break;
                    case MessageType.VIDEO_MESSAGE:
                        content = msg.url;
                        break;
                    case MessageType.FILE_MESSAGE:
                        content = msg.url;
                        break;
                    default:
                        return
                }
                // 会话是否存在 不存在则先新增会话
                let session = sessionMapRef.current.get(realSender)
                const friendInfo = findFriendInfo(sender, realSender, deliveryMethod)
                if (session) {
                    session = {
                        ...session,
                        lastMsgType: cmd,
                        lastMsgContent: content,
                        lastMsgTime: timestamp,
                        lastUserName: friendInfo.note
                    }
                } else {
                    const info = deliveryMethod === DeliveryMethod.GROUP ? findGroupByGroupId(realSender) : friendInfo
                    const sessionId = await createUserSession(realSender, deliveryMethod)
                    session = {
                        id: sessionId,
                        userId: userInfo.id,
                        name: info.note,
                        avatar: info.avatar,
                        receiverUserId: realSender,
                        deliveryMethod: deliveryMethod,
                        lastMsgType: cmd,
                        lastMsgContent: content,
                        lastMsgTime: timestamp,
                        lastUserName: friendInfo.note
                    }
                    dispatch(addSession({ session: session }))
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
    }, [socket]);

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
    const handleSelected = (id) => {
        dispatch(selectSession({ sessionId: id }))
    }

    return (
        <>
            <Flex className='chat-root-flex' justify='center' align='center'>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <div className='chat-panel-tabs' style={style}>
                        <Tabs
                            activeKey={selectedSessionId}
                            onChange={(key) => handleSelected(key)}
                            key="chat-tabs"
                            className='chat-tabs'
                            tabPosition='left'
                            indicator={{ size: 0 }}
                            centered
                            tabBarGutter={0}
                            items={result.map((id, i) => {
                                return {
                                    key: id,
                                    forceRender: true,
                                    label: <UserSessionItem sessionId={id} />,
                                    children: (
                                        <div style={{ height: '100%' }}>
                                            <ChatItem key={id} sessionId={id}/>
                                        </div>
                                    )

                                }
                            })}
                        >
                        </Tabs>
                    </div>
                </Flex>
            </Flex>
        </>
    );
}

export default Chat;