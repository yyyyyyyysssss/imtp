import { Avatar, Badge, Flex, Tabs } from "antd";
import React, { forwardRef, useContext, useEffect, useImperativeHandle, useState } from 'react';
import { HomeContext, useWebSocket } from '../../context';
import { getBit } from '../../utils';
import ChatItem from './chat-item';
import './index.less';
import IdGen from "../../utils/IdGen";
import { normalize, schema } from 'normalizr';
import { createUserSession, fetchUserSessions } from "../../api/ApiService";
import { loadSession, removeSession, addMessage, addSession } from '../../redux/slices/chatSlice';
import { useDispatch, useSelector } from 'react-redux';
import UserSessionItem from "../../components/user-session-item";
import { DeliveryMethod, MessageStatus, MessageType } from "../../enum";

const Chat = forwardRef((props, ref) => {
    const {socket} = useWebSocket();
    useImperativeHandle(ref, () => ({
        addUserSession: addUserSession
    }));
    const { style } = props;
    const { setHeadName,userInfo, findUserInfoByGroup,findGroupByGroupId, findUserInfoByFriendId } = useContext(HomeContext);

    const dispatch = useDispatch()
    //当前选中的会话
    const [selectTab, setSelectTab] = useState(null);
    //会话数据
    const result = useSelector(state => state.chat.result)
    const [data, setData] = useState([]);
    useEffect(() => {
        const fetchData = async () => {
            const userSessionList = await fetchUserSessions() || []
            if(userSessionList){
                setData(userSessionList)
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
    }, []);
    //接收消息处理
    const handleReceiveMessage = (msg,userSessionItem) => {
        if (!userSessionItem.chatItemData) {
            userSessionItem.chatItemData = [];
        }
        userSessionItem.chatItemData.push(msg);
        const latestUs = {
            ...userSessionItem,
            lastMsgType: msg.type,
            lastMsgContent: msg.content,
            lastMsgTime: msg.timestamp,
            lastSendMsgUserId: msg.sender,
            lastUserName: msg.senderName
        }
        if (selectTab !== userSessionItem.id && !msg.self) {
            latestUs.count = userSessionItem.count === undefined ? 1 : userSessionItem.count + 1;
        }
        moveToTop(latestUs, data, false);
    }
    //接收消息
    useEffect(() => {
        if (socket) {
            const handleReceiveEvent = async (event) => {
            const msg = JSON.parse(event.data);
            const { header } = msg;
            let content;
            if (header) {
                const { cmd, sender, receiver, reserved } = header;
                const isGroup = getBit(reserved);
                const realSender = (isGroup === 1) ? receiver : sender;
                if (sender == 0) {
                    //消息响应
                    if(cmd === MessageType.COMMON_RESPONSE){
                        const msgResUserSessionItem = data.find(f => f.receiverUserId === receiver);
                        const chatItemData = msgResUserSessionItem.chatItemData;
                        const newArr = chatItemData.map(item => item.ackId === msg.ackId ? {...item,status: MessageStatus.DELIVERED} : item)
                        setData(prevData => prevData.map(m => m.id === msgResUserSessionItem.id ? {...msgResUserSessionItem,chatItemData: newArr} : m));
                    }
                    return;
                }
                let us = data.find(f => f.receiverUserId === realSender);
                if (!us) {
                    let userItem;
                    if(isGroup === 1){
                        userItem = findGroupByGroupId(receiver);
                        userItem.type = DeliveryMethod.GROUP;
                    }else {
                        console.log('findUserInfoByFriendId',sender)
                        userItem = findUserInfoByFriendId(sender);
                        userItem.type = DeliveryMethod.SINGLE;
                    }
                    
                    us = await createSession(userItem);
                }
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
                }
                const m = message(msg, content,us);
                handleReceiveMessage(m,us);
            }
            }
            socket.addEventListener('message', handleReceiveEvent);
            return () => {
                socket.removeEventListener('message', handleReceiveEvent);
            }
        }
    }, [socket,data]);

    const message = (msg, content, userSessionItem) => {
        let avatar;
        let name;
        let userItem;
        if (userSessionItem.deliveryMethod === DeliveryMethod.GROUP) {
            userItem = findUserInfoByGroup(userSessionItem.receiverUserId, msg.header.sender);
            name = userItem.nickname;
        } else {
            userItem = findUserInfoByFriendId(msg.header.sender);
            name = userSessionItem.name;
        }
        avatar = userItem.avatar;
        const message = {
            id: IdGen.nextId(),
            type: msg.header.cmd,
            status: MessageStatus.COMPLETED,
            sender: msg.header.sender,
            receiver: msg.header.receiver,
            deliveryMethod: userSessionItem.deliveryMethod,
            self: false,
            avatar: avatar,
            name: name,
            timestamp: msg.timestamp,
            senderName: userItem.nickname,
            content: content,
            contentMetadata: msg.contentMetadata
        }
        return message;
    }


    //更新会话
    const updateUserSessionData = (item) => {
        setData(prevData => prevData.map(m => m.id === item.id ? { ...item } : m));
    }
    //会话选中
    const handleSelected = (id) => {
        //切换tab
        setSelectTab(id);
        const item = data.find(item => item.id === id);
        const uitem = {
            ...item,
            count: 0
        };
        updateUserSessionData(uitem);
        //设置头部名称
        setHeadName(item.name);
    }
    //会话移到最顶层
    const moveToTop = (item, arr, selected = true) => {
        if (!arr) {
            arr = data;
        }
        setData(prevData => {
            const newData = prevData.filter(f => f.id !== item.id);
            return [item,...newData];
        });
        if (selected) {
            setSelectTab(item.id);
        }
    }
    //添加会话
    const addUserSession = async (userItem) => {
        let item = data.find(item => item.receiverUserId === userItem.id);
        if (!item) {
            item  = await createSession(userItem);
        }
        setSelectTab(item.id);
    }

    const createSession = async (userItem) => {
        const userId = userInfo.id;
        const sessionId = await createUserSession(userItem.id, userItem.type)
        const session = {
            id: sessionId,
            userId: userId,
            name: userItem.nickname === undefined ? userItem.groupName : userItem.nickname,
            senderUserId: userId,
            receiverUserId: userItem.id,
            avatar: userItem.avatar,
            deliveryMethod: userItem.type,
            lastMsgContent: userItem.content,
            lastMsgTime: userItem.timestamp
        }
        dispatch(addSession({ session: session }))
        return session;
    }
    return (
        <>
            <Flex className='chat-root-flex' justify='center' align='center'>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <div className='chat-panel-tabs' style={style}>
                        <Tabs
                            activeKey={selectTab}
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
                                    label: <UserSessionItem sessionId = {id}/>,
                                    children: (
                                        <div style={{ height: '100%' }}>
                                            <ChatItem key={id} sessionId={id} selectTab={selectTab} />
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
})

export default Chat;