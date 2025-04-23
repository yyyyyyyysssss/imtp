import React, { useMemo } from 'react';
import { Avatar, VStack, HStack, Text, Pressable } from 'native-base';
import { StyleSheet } from 'react-native';
import { formatChatDate } from '../utils/FormatUtil';
import { useSelector } from 'react-redux';
import { DeliveryMethod, shortMessage } from '../enum';

const UserSessionItem = React.memo(({ sessionId }) => {

    const session = useSelector(state => state.chat.entities.sessions[sessionId])

    const { name, avatar, lastMsgType, lastUserName, lastMsgContent, lastMsgTime, deliveryMethod, unreadMessageCount } = session || {}

    const messageContent = useMemo(() => {
        const m = shortMessage(lastMsgType, lastMsgContent)
        if (m && deliveryMethod === DeliveryMethod.GROUP) {
            m = lastUserName + ': ' + m
        }
        return m
    }, [lastMsgType, lastMsgContent, deliveryMethod, lastUserName])

    return (
        <HStack space={5}>
            <HStack flex={1}>
                <Avatar size="60px" _image={{
                    borderRadius: 8
                }} source={{ uri: avatar }} >
                    {
                        unreadMessageCount > 0 && (
                            <Avatar.Badge mb={10} mr={-3} size={7} zIndex={1} bg="red.500" justifyContent='center' alignItems='center'>
                                <Text fontSize={12} fontWeight='bold' color='white'>{unreadMessageCount > 99 ? '99+' : unreadMessageCount}</Text>
                            </Avatar.Badge>
                        )
                    }

                </Avatar>
            </HStack>
            <HStack flex={6}>
                <VStack flex={1} justifyContent='space-between'>
                    <Text style={styles.userSessionName}>{name}</Text>
                    <Text
                        style={styles.userSessionLastMsg}
                        numberOfLines={1}
                        ellipsizeMode='tail'
                    >
                        {messageContent}
                    </Text>
                </VStack>
                <VStack flex={1} alignItems='flex-end'>
                    <Text style={styles.userSessionLastTime}>{formatChatDate(lastMsgTime)}</Text>
                </VStack>
            </HStack>
        </HStack>
    )
})


const styles = StyleSheet.create({
    userSessionName: {
        color: '#000000',
        fontWeight: '300',
        fontSize: 17
    },
    userSessionLastMsg: {
        color: 'gray',
        fontSize: 15
    },
    userSessionLastTime: {
        color: 'gray',
        fontSize: 14
    }
})

export default UserSessionItem