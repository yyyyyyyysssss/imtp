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

    const renderItem = useCallback((type, self, status, content, contentMetadata) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return <TextMessage content={content} direction={self ? 'RIGHT' : 'LEFT'} />
            case MessageType.IMAGE_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progressInfo?.percentage}
                    >
                        <ImageMessage content={content} contentMetadata={contentMetadata} status={status} />
                    </ProgressOverlayBox>
                )
            case MessageType.VIDEO_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progressInfo?.percentage}
                    >
                        <VideoMessage content={content} contentMetadata={contentMetadata} status={status} />
                    </ProgressOverlayBox>

                )
            case MessageType.VOICE_MESSAGE:
                return (
                    <VoiceMessage content={content} status={status} duration={contentMetadata.duration} direction={self ? 'RIGHT' : 'LEFT'} />
                )
            case MessageType.FILE_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progressInfo?.percentage}
                    >
                        <FileMessage content={content} status={status} filename={contentMetadata.name} fileSize={contentMetadata.sizeDesc} direction={self ? 'RIGHT' : 'LEFT'} />
                    </ProgressOverlayBox>
                )
            case MessageType.VOICE_CALL_MESSAGE:
                return (
                    <VoiceCallMessage callStatus={contentMetadata.callStatus} duration={contentMetadata.duration} durationDesc={contentMetadata.durationDesc} self={self} />
                )
            case MessageType.VIDEO_CALL_MESSAGE:
                return (
                    <VideoCallMessage callStatus={contentMetadata.callStatus} duration={contentMetadata.duration} durationDesc={contentMetadata.durationDesc} self={self} />
                )
            default:
        }
    }, [progressInfo])

    return (
        <Flex gap="small" style={{ flexDirection: self ? 'row-reverse' : '' }}>
            <Avatar size={45} shape="square" src={avatar} />
            <Flex flex={1} style={{ width: '100%',overflow: 'hidden' }} gap="small" justify='center' align={self ? 'end' : 'start'} vertical>
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