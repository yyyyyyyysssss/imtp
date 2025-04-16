import { Flex, Tooltip } from 'antd';
import React from 'react';
import './index.less'
import { CallStatus, MessageType } from '../../enum';
import ImageMessage from '../message/image-message';
import VideoMessage from '../message/video-message';
import { VoiceCallOutlined, VideoMessageIcon, VoiceOutlined } from '../customIcon'
import Icon, { FileOutlined } from '@ant-design/icons';
import { download } from '../../utils';

const MessageQuote = React.memo(({ style, message, vertical = true }) => {

    const { type, name, content, contentMetadata, self } = message || {}

    const handlerFileMessageClick = (url, fileName) => {
        download(url, fileName)
    }

    const renderItem = (type, content, contentMetadata, self) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return (
                    <Tooltip placement="top" title={content} color='white' overlayInnerStyle={{ color: 'gray' }} >
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
                const durationDesc = Math.floor(contentMetadata.duration / 1000) + '\'\''
                return (
                    <Flex gap={6} justify='center' align='center'>
                        <VoiceOutlined
                            style={{
                                transform: 'rotate(90deg)',
                            }}
                            size={20}
                            color='gray'
                        />
                        <div className='quote-content-div-content'>{durationDesc}</div>
                    </Flex >
                )
            case MessageType.FILE_MESSAGE:
                return (
                    <Flex style={{ cursor: 'pointer' }} onClick={() => handlerFileMessageClick(content, contentMetadata.name)}>
                        <Icon component={FileOutlined} style={{ color: 'gray', fontSize: 20 }} />
                        <div className='quote-content-div-content'>
                            {contentMetadata.name}
                        </div>
                    </Flex>
                )
            case MessageType.VOICE_CALL_MESSAGE:
            case MessageType.VIDEO_CALL_MESSAGE:
                let icon = type === MessageType.VOICE_CALL_MESSAGE ? <VoiceCallOutlined size={23} color='gray' /> : <VideoMessageIcon color='gray' size={25} />
                let text
                switch (contentMetadata.callStatus) {
                    case CallStatus.COMPLETED:
                        text = '通话时长'
                        break
                    case CallStatus.CANCELLED:
                        text = self ? '已取消' : '对方已取消'
                        break
                    case CallStatus.REFUSED:
                        text = self ? '对方已拒接' : '已拒接'
                        break
                    case CallStatus.INTERRUPTED:
                        text = '通话中断'
                        break
                }
                return (
                    <Flex gap={6} justify='center' align='center'>
                        {icon}
                        <div className='quote-content-div-content'>
                            {text}
                        </div>
                        {contentMetadata.callStatus === CallStatus.COMPLETED && (
                            <div className='quote-content-div-content'>
                                {contentMetadata.durationDesc}
                            </div>
                        )}
                    </Flex >
                )
            default:
                return (
                    <></>
                )
        }
    }

    return (
        <Flex justify='center' align='flex-start' vertical={vertical}>
            <div className='quote-content-div-name'>
                {name}：
            </div>
            {renderItem(type, content, contentMetadata, self)}
        </Flex>
    )
})

export default MessageQuote