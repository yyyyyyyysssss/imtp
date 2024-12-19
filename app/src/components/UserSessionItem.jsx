import React from 'react';
import { Avatar, VStack, HStack, Text, Pressable } from 'native-base';
import { StyleSheet } from 'react-native';
import { formatChatDate } from '../utils/FormatUtil';

const UserSessionItem = React.memo(({ name, avatar, lastMsgType, lastMsgContent, lastMsgTime, unreadMessageCount }) => {
    
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
                    <Text style={styles.userSessionLastMsg}>{lastMsgContent}</Text>
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
        fontSize: 13
    },
    userSessionLastTime: {
        color: 'gray',
        fontSize: 13
    }
})

export default UserSessionItem