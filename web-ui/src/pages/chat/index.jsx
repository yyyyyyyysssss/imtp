import { Avatar, Badge, Flex, Modal, Tabs } from "antd";
import React, { forwardRef, useContext, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import httpWrapper from '../../api/axiosWrapper';
import VideoPlay from '../../components/VideoPlay';
import { ChatPanelContext, HomeContext, useWebSocket } from '../../context';
import { formatChatDate, getBit } from '../../utils';
import ChatItem from './chat-item';
import './index.less';

const TEXT_MESSAGE = 1;
const IMAGE_MESSAGE = 4;
const VIDEO_MESSAGE = 5;
const FILE_MESSAGE = 6;

const MSG_RES = -1;

const SINGLE = "SINGLE";
const GROUP = "GROUP";

const PENDING = "PENDING";
const SENT = "SENT";
const DELIVERED = "DELIVERED";

const Chat = forwardRef((props, ref) => {
    const {socket} = useWebSocket();
    useImperativeHandle(ref, () => ({
        addUserSession: addUserSession
    }));
    const { style } = props;
    const { setHeadName,userInfo, findUserInfoByGroup,findGroupByGroupId, findUserInfoByFriendId } = useContext(HomeContext);
    //视频弹出框
    const [videoOpen, setVideoOpen] = useState(false);
    //视频播放选项
    const [videoOption, setVideoOption] = useState(null);
    //当前选中的会话
    const [selectTab, setSelectTab] = useState(null);
    //会话数据
    const [data, setData] = useState([]);
    useEffect(() => {
        httpWrapper.get('/social/userSession/{userId}')
            .then(
                (res) => {
                    setData(res?.data);
                }
            )
    }, []);
    const handleSenderMessage = (msg,userSessionId) => {
        setData(prevData => {
            const us = prevData.find(f => f.id === userSessionId);
            if (!us.chatItemData) {
                us.chatItemData = [];
            }
            us.chatItemData.push(msg);
            const latestUs = {
                ...us,
                lastMsgType: msg.type,
                lastMsgContent: msg.content,
                lastMsgTime: msg.timestamp,
                lastSendMsgUserId: msg.sender,
                lastUserName: msg.senderName,
                count: 0
            }
            const newData = prevData.filter(f => f.id !== userSessionId);
            return [latestUs,...newData];
        })
    }
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
                    if(cmd === MSG_RES){
                        const msgResUserSessionItem = data.find(f => f.receiverUserId === receiver);
                        const chatItemData = msgResUserSessionItem.chatItemData;
                        const newArr = chatItemData.map(item => item.ackId === msg.ackId ? {...item,status: DELIVERED} : item)
                        setData(prevData => prevData.map(m => m.id === msgResUserSessionItem.id ? {...msgResUserSessionItem,chatItemData: newArr} : m));
                    }
                    return;
                }
                let us = data.find(f => f.receiverUserId === realSender);
                if (!us) {
                    let userItem;
                    if(isGroup === 1){
                        userItem = findGroupByGroupId(receiver);
                        userItem.type = GROUP;
                    }else {
                        console.log('findUserInfoByFriendId',sender)
                        userItem = findUserInfoByFriendId(sender);
                        userItem.type = SINGLE;
                    }
                    
                    us = await createUserSession(userItem);
                }
                switch (cmd) {
                    case TEXT_MESSAGE:
                        content = msg.text;
                        break;
                    case IMAGE_MESSAGE:
                        content = msg.url;
                        break;
                    case VIDEO_MESSAGE:
                        content = msg.url;
                        break;
                    case FILE_MESSAGE:
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
        if (userSessionItem.deliveryMethod === GROUP) {
            userItem = findUserInfoByGroup(userSessionItem.receiverUserId, msg.header.sender);
            name = userItem.nickname;
        } else {
            userItem = findUserInfoByFriendId(msg.header.sender);
            name = userSessionItem.name;
        }
        avatar = userItem.avatar;
        const message = {
            id: uuidv4(),
            type: msg.header.cmd,
            status: 'COMPLETED',
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

    const messageContentPre = (messageType,messageContent) => {
        if(!messageType){
            return '';
        }
        let c;
        switch(messageType){
            case TEXT_MESSAGE:
                c = messageContent;
                break;
            case IMAGE_MESSAGE:
                c = '[图片]';
                break;
            case VIDEO_MESSAGE:
                c = '[视频]';
                break;
            case FILE_MESSAGE:
                c = '[文件]';
                break;
            default:
                c = '';
        }
        return c;
    }

    //更新会话消息
    const updateChatItem = (userSessionId,chatItemMsg) => {
        setData(prevData => {
            const msgResUserSessionItem = prevData.find(f => f.id === userSessionId);
            const chatItemData = msgResUserSessionItem.chatItemData;
            const newArr = chatItemData.map(item => item.id === chatItemMsg.id ? {...chatItemMsg} : item);
            return prevData.map(m => m.id === msgResUserSessionItem.id ? {...msgResUserSessionItem,chatItemData: newArr} : m);
        });
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
        const item = data.find(item => item.receiverUserId === userItem.id);
        if (item) {
            moveToTop(item);
        } else {
            const userSessionItem  = await createUserSession(userItem);
            moveToTop(userSessionItem);
        }
    }

    const createUserSession = async (userItem) => {
        const userId = userInfo.id;
        const userSessionReq = {
            userId: userId,
            receiverUserId: userItem.id,
            deliveryMethod: userItem.type
        }
        let userSessionItem;
        await httpWrapper.post('/social/userSession/{userId}', userSessionReq)
            .then(
                (res) => {
                    userSessionItem = {
                        id: res?.data,
                        userId: userId,
                        name: userItem.nickname === undefined ? userItem.groupName : userItem.nickname,
                        senderUserId: userId,
                        receiverUserId: userItem.id,
                        avatar: userItem.avatar,
                        deliveryMethod: userItem.type,
                        lastMsgContent: userItem.content,
                        lastMsgTime: userItem.timestamp
                    }
                }
            );
        return userSessionItem;
    }

    //视频播放
    const handleVideoPlay = (url, fileType) => {
        console.log('chat play: ', url, fileType);
        const videoJsOptions = {
            autoplay: true,
            controls: true,
            responsive: true,
            fluid: true,
            sources: [{
                src: url,
                type: fileType
            }]
        };
        setVideoOption(videoJsOptions)
        setVideoOpen(true);
    }
    //视频关闭
    const videoOnCancel = () => {
        setVideoOpen(false);
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
                            items={data.map((item, i) => {
                                return {
                                    key: item.id,
                                    forceRender: true,
                                    label: (
                                        <Flex style={{ padding: '3px', width: '100%' }}>
                                            <Badge count={item.count}><Avatar size={50} shape="square" src={item.avatar} /></Badge>
                                            <Flex justify='space-between' style={{ width: '100%', marginLeft: '7px' }} vertical>
                                                <Flex justify='space-between'>
                                                    <span className='user-session-name'>{item.name}</span>
                                                    <span className='user-session-time'>{formatChatDate(item.lastMsgTime)}</span>
                                                </Flex>
                                                <Flex justify='space-between'>
                                                    <span className='user-session-lastMsg'>
                                                        {messageContentPre(item.lastMsgType,item.lastMsgContent)}
                                                    </span>
                                                </Flex>
                                            </Flex>
                                        </Flex>
                                    ),
                                    children: (
                                        <div style={{ height: '100%' }}>
                                            <ChatPanelContext.Provider value={{ handleVideoPlay,handleSenderMessage,updateChatItem }}>
                                                <ChatItem key={item.id} userSessionItem={item} selectTab={selectTab} />
                                            </ChatPanelContext.Provider>
                                        </div>
                                    )

                                }
                            })}
                        >

                        </Tabs>
                    </div>
                </Flex>
            </Flex>
            <Modal
                centered
                destroyOnClose={true}
                maskClosable={false}
                width={400}
                open={videoOpen}
                onCancel={videoOnCancel}
                footer={null}
            >
                <div>
                    <VideoPlay options={videoOption} />
                </div>
            </Modal>
        </>
    );
})

export default Chat;