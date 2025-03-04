import React, { useEffect, useRef } from 'react';
import './index.less'
import { Avatar, Badge, Flex } from "antd";
import { useSelector, useDispatch } from 'react-redux';
import { DeliveryMethod, MessageType } from '../../enum';
import { formatChatDate } from '../../utils';
import { useSwipeable } from 'react-swipeable';
import { removeSession } from '../../redux/slices/chatSlice';
import { deleteUserSessionById } from '../../api/ApiService';

const UserSessionItem = React.memo(({ sessionId }) => {

    const dispatch = useDispatch()

    const session = useSelector(state => state.chat.entities.sessions[sessionId])

    const { name, avatar, lastMsgType, lastUserName, lastMsgContent, lastMsgTime, deliveryMethod, unreadMessageCount } = session || {}

    const [showDelete, setShowDelete] = React.useState(false);
    const deleteRef = useRef()
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (deleteRef.current && !deleteRef.current.contains(event.target)) {
                setShowDelete(false)
            }
        }
        // 绑定事件监听器
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            // 组件卸载时移除事件监听器
            document.removeEventListener('mousedown', handleClickOutside);
        }
    }, [])


    const swipeableHandlers = useSwipeable({
        onSwipedLeft: () => {
            setShowDelete(true)
        },
        onSwipedRight: () => {
            setShowDelete(false)
        },
        trackMouse: true, //允许鼠标操作

    })

    let messageContent;
    switch (lastMsgType) {
        case MessageType.TEXT_MESSAGE:
            messageContent = lastMsgContent
            break
        case MessageType.IMAGE_MESSAGE:
            messageContent = '[图片]'
            break
        case MessageType.VOICE_MESSAGE:
            messageContent = '[语音]'
            break
        case MessageType.VIDEO_MESSAGE:
            messageContent = '[视频]'
            break
        case MessageType.FILE_MESSAGE:
            messageContent = '[文件]'
            break
        case MessageType.VOICE_CALL_MESSAGE:
            messageContent = '[语音通话]'
            break
        case MessageType.VIDEO_CALL_MESSAGE:
            messageContent = '[视频通话]'
            break
    }
    if (messageContent && deliveryMethod === DeliveryMethod.GROUP) {
        messageContent = lastUserName + ': ' + messageContent
    }

    const deleteSession = (e) => {
        //阻止事件传播 避免选中会话
        e.stopPropagation()
        //从全局状态中移除该会话
        dispatch(removeSession({ sessionId: sessionId }))
        //调接口删除会话
        deleteUserSessionById(sessionId)
    }

    return (
        <div
            {...swipeableHandlers}
            style={{
                position: 'relative',
                overflow: 'hidden',
                width: '100%',
                padding: '3px',
            }}
        >
            <Flex flex={1} style={{ transition: 'transform 0.3s', transform: showDelete ? 'translateX(-60px)' : 'translateX(0)' }}>
                <Badge style={{ userSelect: 'none', marginTop: '7px', marginRight: '4px' }} count={unreadMessageCount}><Avatar style={{ userSelect: 'none' }} size={50} shape="square" src={avatar} /></Badge>
                <Flex justify='space-between' style={{ width: '100%', marginLeft: '7px' }} vertical>
                    <Flex justify='space-between'>
                        <span className='user-session-name'>{name}</span>
                        <span className='user-session-time'>{formatChatDate(lastMsgTime)}</span>
                    </Flex>
                    <Flex justify='space-between'>
                        <span className='user-session-lastMsg'>
                            {messageContent}
                        </span>
                    </Flex>
                </Flex>
            </Flex>
            {showDelete && (
                <div
                    ref={deleteRef}
                    onClick={deleteSession}
                    style={{
                        position: 'absolute',
                        right: 6,
                        top: 0,
                        width: '60px',
                        height: '100%',
                        backgroundColor: 'red',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        textAlign: 'center',
                        color: 'white',
                        userSelect: 'none'
                    }}
                >
                    删除
                </div>
            )}
        </div>

    )
})

export default UserSessionItem