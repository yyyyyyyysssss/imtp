import { Avatar, HStack, Pressable, VStack, Text, Box, Spinner } from 'native-base';
import React, { useCallback } from 'react';
import { StyleSheet } from 'react-native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import TextMessage from './TextMessage';
import ImageMessage from './ImageMessage';


const Message = React.memo(({ message }) => {

    const { type, name, avatar, deliveryMethod, self, status, content, contentMetadata } = message

    let messageStatusIcon;
    switch (status) {
        case 'PENDING':
            messageStatusIcon = <Spinner color='#70BFFF' />
            break
        case 'SENT':
            messageStatusIcon = <></>
            break
        case 'DELIVERED':
            messageStatusIcon = <></>
            break
        case 'FAILED':
            messageStatusIcon = <AntDesignIcon name="exclamationcircle" color="red" size={18} />
            break
        default:
            messageStatusIcon = <></>
            break
    }

    const renderItem = useCallback((type, self, content, contentMetadata) => {
        switch (type) {
            case 1:
                return <TextMessage content={content} contentMetadata={contentMetadata} direction={self ? 'LEFT' : 'RIGHT'} />
            case 4:
                return <ImageMessage content={content} contentMetadata={contentMetadata} />
            case 5:
                return <></>
            case 6:
                return <></>
        }
    }, [])

    return (
        <HStack space={3} reversed={self ? true : false}>
            <Avatar
                size='50px'
                _image={{
                    borderRadius: 8
                }}
                source={{ uri: avatar }}
            />
            <VStack flex={1} alignItems={self ? 'flex-end' : 'flex-start'}>
                {!self && deliveryMethod === 'GROUP' && (
                    <HStack>
                        <Text style={styles.chatItemUserName}>{name}</Text>
                    </HStack>
                )}
                <HStack space={2} reversed={self ? true : false} alignItems='center'>
                    {renderItem(type, self, content, contentMetadata)}
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
