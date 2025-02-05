import React, { useState } from 'react';
import './index.less'
import { MessageType, MessageStatus } from '../../enum';
import sendFailIcon from '../../assets/img/send_fail.png'
import { LoadingOutlined } from '@ant-design/icons';
import { Flex, Avatar } from "antd"
import TextMessage from './text-message';
import ImageMessage from './image-message';
import FileMessage from './file-message';
import VideoMessage from './video-message';


const Message = React.memo(({ message }) => {
    // const message = useSelector(state => state.chat.entities.messages[messageId])

    const [progress, setProgress] = useState(0.01)

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata, progressId } = message || {}

    let messageStatusIcon;
    switch (status) {
        case MessageStatus.PENDING:
            messageStatusIcon = <LoadingOutlined style={{ fontSize: '15px', display: 'none' }} />;
            break
        case MessageStatus.SENT:
            messageStatusIcon = <LoadingOutlined style={{ fontSize: '15px', display: 'none' }} />;
            break
        case MessageStatus.DELIVERED:
            messageStatusIcon = <></>
            break
        case MessageStatus.FAILED:
            messageStatusIcon = <img src={sendFailIcon} alt='' style={{ width: '20px', height: '20px' }} />;
            break
        default:
            messageStatusIcon = <></>
            break
    }

    const renderItem = (type, self, status, content, contentMetadata, progress) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return <TextMessage content={content} direction={self ? 'RIGHT' : 'LEFT'} />
            case MessageType.IMAGE_MESSAGE:
                return (
                    <ImageMessage content={content} contentMetadata={contentMetadata} status={status} />
                )

            case MessageType.VIDEO_MESSAGE:
                return (
                    <VideoMessage content={content} contentMetadata={contentMetadata} status={status}/>
                )
            case MessageType.VOICE_MESSAGE:
                return (
                    <></>
                )
            case MessageType.FILE_MESSAGE:
                return (
                    <FileMessage content={content} filename={contentMetadata.name} fileSize={contentMetadata.sizeDesc} direction={self ? 'RIGHT' : 'LEFT'}/>
                )
        }
    }

    return (
        <Flex gap="small" style={{ flexDirection: self ? 'row-reverse' : '' }}>
            <Avatar size={45} shape="square" src={avatar} />
            <Flex flex={1} gap="small" justify='center' align={self ? 'end' : 'start'} vertical>
                {!self && deliveryMethod === 'GROUP' && (
                    <Flex>
                        <label className='chat-item-label-name'>{name}</label>
                    </Flex>
                )}
                <Flex gap="small" style={{ flexDirection: self ? 'row-reverse' : '',width: '100%' }} align='center'>
                    {renderItem(type, self, status, content, contentMetadata, progress)}
                    {messageStatusIcon}
                </Flex>
            </Flex>
        </Flex>
    )
})

export default Message