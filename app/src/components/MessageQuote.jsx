import React, { useMemo } from 'react';
import { Box, Divider, HStack, Image, Text, VStack } from "native-base"
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Pressable } from 'react-native';
import { MessageType, shortMessage } from '../enum';
import ImageMessage from './ImageMessage';
import VideoMessage from './VideoMessage';
import FastImage from 'react-native-fast-image'
import { VoicePlay } from './VoiceMessage';
import { SubFilemessage } from './FileMessage';
import { SubVoiceCallMessage } from './VoiceCallMessage';
import { SubVideoCallMessage } from './VideoCallMessage';


const MessageQuote = React.memo(({ message, close, type = 'quote' }) => {

    const { type: messageType, name, content, contentMetadata, self } = message || {}

    const messageContent = useMemo(() => {
        return shortMessage(messageType, content)
    }, [messageType, content])

    const closeQuote = () => {
        close()
    }

    const renderItem = (type, content, contentMetadata, self) => {
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                return (
                    <Text style={{ maxWidth: '65%' }} numberOfLines={1} ellipsizeMode='tail'>{content}</Text>
                )
            case MessageType.IMAGE_MESSAGE:
                return (
                    <ImageMessage content={content} size={50} />
                )
            case MessageType.VIDEO_MESSAGE:
                return (
                    <VideoMessage content={content} contentMetadata={contentMetadata} maxHeight={50} />
                )
            case MessageType.VOICE_MESSAGE:
                return (
                    <VoicePlay content={content} duration={contentMetadata.duration} direction='LEFT' />
                )
            case MessageType.FILE_MESSAGE:
                return (
                    <Box
                        width={200}
                        height={50}
                    >
                        <SubFilemessage filename={contentMetadata.name} size='small' justifyContent='flex-start' alignItems='flex-start' />
                    </Box>
                )
            case MessageType.VOICE_CALL_MESSAGE:
                return (
                    <SubVoiceCallMessage callStatus={contentMetadata.callStatus} self={self} durationDesc={contentMetadata.durationDesc} />
                )
            case MessageType.VIDEO_CALL_MESSAGE:
                return (
                    <SubVideoCallMessage callStatus={contentMetadata.callStatus} self={self} durationDesc={contentMetadata.durationDesc} />
                )
            default:
                return <></>
        }
    }

    const renderQuoteItem = (content) => {
        return (
            <Text style={{ maxWidth: '65%' }} numberOfLines={1} ellipsizeMode='tail'>{content}</Text>
        )
    }

    return (
        <HStack maxHeight={100} space={5} style={{ backgroundColor: '#EAEAEA', padding: 5 }}>
            <HStack>
                <Text>{name}：</Text>
                {type === 'quote' ? renderQuoteItem(messageContent) : renderItem(messageType, content, contentMetadata, self)}
            </HStack>
            {type === 'quote' && (
                <Pressable onPress={closeQuote}>
                    <Ionicons name='close-circle-outline' size={20} />
                </Pressable>
            )}
        </HStack>
    )
})

export default MessageQuote