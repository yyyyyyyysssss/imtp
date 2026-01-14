import React, { useCallback } from 'react';
import './index.less'
import { MessageType, MessageStatus } from '../../enum';
import sendFailIcon from '../../assets/img/send_fail.png'
import { LoadingOutlined } from '@ant-design/icons';
import { Flex, Avatar } from "antd"
import TextMessage from './text-message';
import ImageMessage from './image-message';
import FileMessage from './file-message';
import VideoMessage from './video-message';
import VoiceMessage from './voice-message';
import { useSelector } from 'react-redux';
import ProgressOverlayBox from '../ProgressOverlayBox';
import VoiceCallMessage from './voice-call-message';
import VideoCallMessage from './video-call-message';
import MessageQuote from '../message-quote';


const Message = React.memo(({ messageId, onContextMenu }) => {

    const message = useSelector(state => state.chat.entities.messages[messageId])

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata, progressId } = message || {}

    const progressInfo = useSelector(state => state.chat.uploadProgress[progressId])

    let messageStatusIcon;
    switch (status) {
        case MessageStatus.PENDING:
        case MessageStatus.SENT:
            messageStatusIcon = <LoadingOutlined className="message-status-pending" />;
            break
        case MessageStatus.DELIVERED:
            messageStatusIcon = null
            break
        case MessageStatus.FAILED:
            messageStatusIcon = <img src={sendFailIcon} alt='' className="message-status-failed" />;
            break
        default:
            messageStatusIcon = null
            break
    }


    const renderMessage = (type, content, contentMetadata, status, self) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return <TextMessage content={content} direction={self ? 'RIGHT' : 'LEFT'} />;
            case MessageType.IMAGE_MESSAGE:
                return <ImageMessage content={content} contentMetadata={contentMetadata} status={status} />;
            case MessageType.VIDEO_MESSAGE:
                return <VideoMessage content={content} contentMetadata={contentMetadata} status={status} />;
            case MessageType.VOICE_MESSAGE:
                return <VoiceMessage content={content} status={status} duration={contentMetadata.duration} direction={self ? 'RIGHT' : 'LEFT'} />;
            case MessageType.FILE_MESSAGE:
                return <FileMessage content={content} status={status} filename={contentMetadata.name} fileSize={contentMetadata.sizeDesc} direction={self ? 'RIGHT' : 'LEFT'} />;
            case MessageType.VOICE_CALL_MESSAGE:
                return <VoiceCallMessage callStatus={contentMetadata.callStatus} duration={contentMetadata.duration} durationDesc={contentMetadata.durationDesc} self={self} />;
            case MessageType.VIDEO_CALL_MESSAGE:
                return <VideoCallMessage callStatus={contentMetadata.callStatus} duration={contentMetadata.duration} durationDesc={contentMetadata.durationDesc} self={self} />;
            default:
                return null;
        }
    }

    const renderItem = useCallback((type, self, status, content, contentMetadata) => {
        const isPending = status === MessageStatus.PENDING
        const progress = progressInfo?.percentage
        switch (type) {
            case MessageType.IMAGE_MESSAGE:
            case MessageType.VIDEO_MESSAGE:
            case MessageType.FILE_MESSAGE:
                return (
                    <ProgressOverlayBox enabled={isPending} progress={progress}>
                        {renderMessage(type, content, contentMetadata, status, self)}
                    </ProgressOverlayBox>
                )
            case MessageType.TEXT_MESSAGE:
            case MessageType.VOICE_MESSAGE:
            case MessageType.VOICE_CALL_MESSAGE:
            case MessageType.VIDEO_CALL_MESSAGE:
                return renderMessage(type, content, contentMetadata, status, self)
            default:
        }
    }, [progressInfo])

    return (
        <Flex gap="small" style={{ flexDirection: self ? 'row-reverse' : '' }}>
            <Avatar size={45} shape="square" src={avatar} />
            <Flex flex={1} style={{ width: '100%', overflow: 'hidden' }} gap="small" justify='center' align={self ? 'end' : 'start'} vertical>
                {!self && deliveryMethod === 'GROUP' && (
                    <Flex>
                        <label className='chat-item-label-name'>{name}</label>
                    </Flex>
                )}
                <Flex gap="small" style={{ flexDirection: self ? 'row-reverse' : '', width: '100%' }} align='center'>
                    <Flex onContextMenu={onContextMenu} style={{ maxWidth: '60%' }} vertical>
                        {renderItem(type, self, status, content, contentMetadata)}
                    </Flex>
                    {messageStatusIcon}
                </Flex>
                {contentMetadata?.quoteMessage && (
                    <Flex
                        justify='center'
                        align='center'
                        style={{
                            backgroundColor: '#E8E8E8',
                            padding: 5,
                            maxWidth: '60%',
                        }}
                    >
                        <MessageQuote
                            message={contentMetadata.quoteMessage}
                            vertical={false}
                        />
                    </Flex>

                )}
            </Flex>
        </Flex>
    )
})

export default Message