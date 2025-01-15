import { Avatar, HStack, Pressable, VStack, Text, Box, Spinner, Flex } from 'native-base';
import React, { useCallback, useEffect, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import TextMessage from './TextMessage';
import ImageMessage from './ImageMessage';
import FileMessage from './FileMessage';
import VideoMessage from './VideoMessage';
import { MessageType, MessageStatus } from '../enum';
import { useSelector } from 'react-redux';
import { NativeModules, NativeEventEmitter } from 'react-native';
import ProgressOverlayBox from './ProgressOverlayBox';
import VoiceMessage from './VoiceMessage';

const { UploadModule } = NativeModules
const UploadModuleNativeEventEmitter = new NativeEventEmitter(UploadModule);

const Message = React.memo(({ style, messageId }) => {

    const message = useSelector(state => state.chat.entities.messages[messageId])

    const [progress, setProgress] = useState(0.01)

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata, progressId } = message || {}

    useEffect(() => {
        let progressEventEmitter;
        const progressEvent = async () => {
            if (progressId && status === MessageStatus.PENDING) {
                const totalSize = contentMetadata.size
                //上传进度
                progressEventEmitter = UploadModuleNativeEventEmitter.addListener(progressId, (uploadedSize) => {
                    const progress = uploadedSize / totalSize
                    setProgress(progress)
                    console.log(`已上传: ${uploadedSize} 进度: ${(progress * 100).toFixed(2)}%`);
                    if (progress == 100) {
                        progressEventEmitter.remove()
                    }
                })
            }
        }
        progressEvent()

        return () => {
            if (progressEventEmitter) {
                progressEventEmitter.remove()
            }
        }
    }, [progressId, status])

    let messageStatusIcon;
    switch (status) {
        case MessageStatus.PENDING:
            messageStatusIcon = <Spinner color='#70BFFF' />
            break
        case MessageStatus.SENT:
            messageStatusIcon = <Spinner color='#70BFFF' />
            break
        case MessageStatus.DELIVERED:
            messageStatusIcon = <></>
            break
        case MessageStatus.FAILED:
            messageStatusIcon = <AntDesignIcon name="exclamationcircle" color="red" size={20} />
            break
        default:
            messageStatusIcon = <></>
            break
    }
    const renderItem = useCallback((type, self, status, content, contentMetadata, progress) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return <TextMessage content={content} direction={self ? 'RIGHT' : 'LEFT'} />
            case MessageType.IMAGE_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progress}
                    >
                        <ImageMessage content={content} status={status} />
                    </ProgressOverlayBox>
                )

            case MessageType.VIDEO_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progress}
                    >
                        <VideoMessage content={content} status={status} contentMetadata={contentMetadata} />
                    </ProgressOverlayBox>
                )
            case MessageType.VOICE_MESSAGE:
                return <VoiceMessage content={content} status={status} duration={contentMetadata.duration} direction={self ? 'RIGHT' : 'LEFT'} />
            case MessageType.FILE_MESSAGE:
                return (
                    <ProgressOverlayBox
                        enabled={status && status === MessageStatus.PENDING}
                        progress={progress}
                    >
                        <FileMessage filename={contentMetadata.name} fileSize={contentMetadata.sizeDesc} />
                    </ProgressOverlayBox>
                )
        }
    }, [])

    return (
        <HStack space={3} reversed={self ? true : false} style={[style]}>
            <Avatar
                size='50px'
                _image={{
                    borderRadius: 8
                }}
                source={{ uri: avatar }}
            />
            <VStack flex={1} justifyContent='center' alignItems={self ? 'flex-end' : 'flex-start'}  >
                {!self && deliveryMethod === 'GROUP' && (
                    <HStack>
                        <Text style={styles.chatItemUserName}>{name}</Text>
                    </HStack>
                )}
                <HStack space={2} reversed={self ? true : false} alignItems='center'>
                    {renderItem(type, self, status, content, contentMetadata, progress)}
                    {messageStatusIcon}
                </HStack>
            </VStack>
        </HStack>
    )
},(prevProps,nextProps) => prevProps.messageId === nextProps.messageId)

const styles = StyleSheet.create({
    chatItemUserName: {
        color: 'grey',
        fontSize: 12,
    }
})

export default Message
