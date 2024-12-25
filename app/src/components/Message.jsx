import { Avatar, HStack, Pressable, VStack, Text, Box, Spinner } from 'native-base';
import React, { useCallback } from 'react';
import { StyleSheet } from 'react-native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import TextMessage from './TextMessage';
import ImageMessage from './ImageMessage';
import FileMessage from './FileMessage';
import VideoMessage from './VideoMessage';
import { MessageType,MessageStatus } from '../enum';


const Message = React.memo(({ style,message }) => {

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata } = message

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
                return <TextMessage content={content} contentMetadata={contentMetadata} direction={self ? 'RIGHT' : 'LEFT'} />
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
}, (prevProps, nextProps) => {

    return prevProps.message.status === nextProps.message.status &&
        prevProps.message.content === nextProps.message.content && 
        prevProps.message.contentMetadata?.thumbnailUrl === nextProps.message.contentMetadata?.thumbnailUrl
})

const styles = StyleSheet.create({
    chatItemUserName: {
        color: 'grey',
        fontSize: 12,
    }
})

export default Message
