import { Flex, Tooltip } from 'antd';
import React from 'react';
import './index.less'
import { CallStatus, MessageType } from '../../enum';
import ImageMessage from '../message/image-message';
import VideoMessage from '../message/video-message';
import { VoiceCallOutlined, VideoMessageIcon, VoiceOutlined } from '../customIcon'
import { SubFileMessage } from '../message/file-message';
import { SubVoiceMessage } from '../message/voice-message';
import { SubVoiceCallMessage } from '../message/voice-call-message';
import { SubVideoCallMessage } from '../message/video-call-message';

const MessageQuote = React.memo(({ style, message, vertical = true }) => {

    const { type, name, content, contentMetadata, self } = message || {}

    const renderItem = (type, content, contentMetadata, self) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return (
                    <Tooltip style={{width: '100%'}} placement="top" title={content} color='white' styles={{body: {color: 'gray'}}} >
                        <div className='quote-content-div-content'>
                            {content}
                        </div>
                    </Tooltip>
                )
            case MessageType.IMAGE_MESSAGE:
                return (
                    <ImageMessage content={content} contentMetadata={contentMetadata} maxHeight={30} />
                )
            case MessageType.VIDEO_MESSAGE:
                return (
                    <VideoMessage content={content} contentMetadata={contentMetadata} maxHeight={30} />
                )
            case MessageType.VOICE_MESSAGE:
                return (
                    <SubVoiceMessage content={content} duration={contentMetadata.duration} direction='LEFT' />
                )
            case MessageType.FILE_MESSAGE:
                return (
                    <SubFileMessage content={content} filename={contentMetadata.name} iconFontSize={20} color='gray' maxLine={1}  />
                )
            case MessageType.VOICE_CALL_MESSAGE:
                return (
                    <SubVoiceCallMessage callStatus={contentMetadata.callStatus} durationDesc={contentMetadata.durationDesc} self={self} color='gray'/>
                )
            case MessageType.VIDEO_CALL_MESSAGE:
                return (
                    <SubVideoCallMessage callStatus={contentMetadata.callStatus} durationDesc={contentMetadata.durationDesc} self={self} color='gray'/>
                )
            default:
                return (
                    <></>
                )
        }
    }

    return (
        <Flex style={{width: '100%'}} justify='center' align='flex-start' vertical={vertical}>
            <div className='quote-content-div-name'>
                {name}：
            </div>
            {renderItem(type, content, contentMetadata, self)}
        </Flex>
    )
})

export default MessageQuote