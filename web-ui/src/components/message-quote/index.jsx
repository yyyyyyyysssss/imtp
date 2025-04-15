import { Divider, Flex } from 'antd';
import React from 'react';
import { useSelector } from 'react-redux';
import { CloseCircleOutlined } from '@ant-design/icons';
import './index.less'
import { MessageType } from '../../enum';
import ImageMessage from '../message/image-message';
import VideoMessage from '../message/video-message';
import voicePlayPng from '../../assets/img/voice-play.png'
import Icon, { FileOutlined } from '@ant-design/icons';

const MessageQuote = React.memo(({ messageId, closeContentFooter }) => {

    const message = useSelector(state => state.chat.entities.messages[messageId])

    const { type, name, content, contentMetadata } = message || {}

    const renderItem = (type, content, contentMetadata) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return (
                    <div className='quote-content-div-content'>
                        {content}
                    </div>
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
                    <Flex style={{ width: '20px' }} justify='space-between'>
                        <img
                            style={{
                                transform: 'rotate(90deg)'
                            }}
                            src={voicePlayPng}
                            alt=''
                        />
                        <div style={{ fontSize: 18, paddingLeft: 5 }}>{durationDesc}</div>
                    </Flex >
                )
            case MessageType.FILE_MESSAGE:
                return
            case MessageType.VOICE_CALL_MESSAGE:
                return
            case MessageType.VIDEO_CALL_MESSAGE:
                return
            default:
        }
    }

    return (
        <Flex
            flex={1}
            justify='space-between'
            style={{
                paddingTop: 5,
                paddingLeft: 10,
                paddingRight: 10,
                height: '100%',
            }}
        >
            <Flex gap={5} flex={1} justify='flex-start' align='center' style={{ padding: 10 }}>
                <Divider style={{ height: '100%', borderWidth: '3px', borderColor: 'lightgray' }} type='vertical' />
                <Flex justify='space-between' align='flex-start' vertical>
                    <div className='quote-content-div-name'>
                        {name}：
                    </div>
                    {renderItem(type, content, contentMetadata)}
                </Flex>
            </Flex>
            <Flex
                flex={1}
                justify='flex-end'
                align='flex-start'
            >
                <div className='close-div' onClick={closeContentFooter}>
                    <CloseCircleOutlined
                        style={{
                            fontSize: 16,
                        }}
                    />
                </div>
            </Flex>
        </Flex>
    )
})

export default MessageQuote