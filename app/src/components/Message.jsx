import { Avatar, HStack, Pressable, VStack, Text, Box, Spinner } from 'native-base';
import React, { useCallback, useEffect } from 'react';
import { StyleSheet } from 'react-native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import TextMessage from './TextMessage';
import ImageMessage from './ImageMessage';
import FileMessage from './FileMessage';
import VideoMessage from './VideoMessage';
import { MessageType, MessageStatus } from '../enum';
import { useSelector } from 'react-redux';
import { NativeModules, NativeEventEmitter } from 'react-native';

const { UploadModule } = NativeModules
const UploadModuleNativeEventEmitter = new NativeEventEmitter(UploadModule);

const Message = React.memo(({ style, messageId }) => {

    const message = useSelector(state => state.chat.entities.messages[messageId])

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata, progressId } = message

    useEffect(() => {
        let progressEventEmitter;
        const progressEvent = async () => {
            if (progressId && status === MessageStatus.PENDING) {
                const totalSize = contentMetadata.size
                console.log('progressId',progressId)
                //上传进度
                progressEventEmitter = UploadModuleNativeEventEmitter.addListener(progressId, (uploadedSize) => {
                    let progress = (uploadedSize / totalSize) * 100;
                    console.log(`上传进度: ${progress.toFixed(2)}%`);
                    if(progress == 100){
                        progressEventEmitter.remove()
                    }
                })
            }
        }
        progressEvent()
        
        return () => {
            if(progressEventEmitter){
                progressEventEmitter.remove()
            }
        }
    }, [progressId,status])

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
    const renderItem = useCallback((type, self, status, content, contentMetadata) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return <TextMessage content={content} direction={self ? 'RIGHT' : 'LEFT'} />
            case MessageType.IMAGE_MESSAGE:
                return <ImageMessage content={content} status={status} />
            case MessageType.VIDEO_MESSAGE:
                return <VideoMessage content={content} status={status} contentMetadata={contentMetadata} />
            case MessageType.FILE_MESSAGE:
                return <FileMessage content={content} status={status} contentMetadata={contentMetadata} />
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
                    {renderItem(type, self, status, content, contentMetadata)}
                    {messageStatusIcon}
                </HStack>
            </VStack>
        </HStack>
    )
})

const styles = StyleSheet.create({
    chatItemUserName: {
        color: 'grey',
        fontSize: 12,
    }
})

export default Message
