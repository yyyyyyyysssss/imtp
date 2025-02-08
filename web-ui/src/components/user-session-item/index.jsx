import React from 'react';
import './index.less'
import { Avatar, Badge, Flex } from "antd";
import { useSelector } from 'react-redux';
import { DeliveryMethod, MessageType } from '../../enum';
import { formatChatDate } from '../../utils';

const UserSessionItem = React.memo(({ sessionId }) => {

    const session = useSelector(state => state.chat.entities.sessions[sessionId])

    const { name, avatar, lastMsgType, lastUserName, lastMsgContent, lastMsgTime, deliveryMethod, unreadMessageCount } = session || {}

    let messageContent;
    switch (lastMsgType) {
        case MessageType.TEXT_MESSAGE:
            messageContent = lastMsgContent
            break
        case MessageType.IMAGE_MESSAGE:
            messageContent = '[图片]'
            break
        case MessageType.VIDEO_MESSAGE:
            messageContent = '[视频]'
            break
        case MessageType.FILE_MESSAGE:
            messageContent = '[文件]'
            break
        case MessageType.VOICE_MESSAGE:
            messageContent = '[语音]'
            break
    }
    if (messageContent && deliveryMethod === DeliveryMethod.GROUP) {
        messageContent = lastUserName + ': ' + messageContent
    }

    return (
        <Flex style={{ padding: '3px', width: '100%' }}>
            <Badge count={unreadMessageCount}><Avatar size={50} shape="square" src={avatar} /></Badge>
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
    )
})

export default UserSessionItem