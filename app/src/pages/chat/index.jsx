import { Button, Center, FlatList, Avatar, Input, Pressable, VStack, HStack, Box, Text, Flex, Divider, View } from 'native-base';
import React, { useEffect, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import { useDispatch, useSelector } from 'react-redux';
import { initSession, addSession } from '../../redux/slices/chatSlice';
import { formatChatDate } from '../../utils';


const Chat = () => {
    const navigation = useNavigation();

    const { userSessions } = useSelector(state => state.chat)
    const dispatch = useDispatch()
    //查询用户会话
    useEffect(() => {
        api.get('/social/userSession/{userId}')
            .then(
                (res) => {
                    const userSessions = res.data
                    //初始化会话
                    dispatch(initSession(userSessions))
                }
            )
    }, [])

    const toChatItem = () => {
        navigation.navigate('ChatItem')
    }

    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Center style={styles.searchCenter}>
                    <Input
                        borderWidth="0"
                        backgroundColor='white'
                        size='lg'
                        shadow={1}
                        InputLeftElement={
                            <Pressable style={{ marginLeft: 8 }}>
                                <AntDesignIcon name='search1' size={18} color="gray" />
                            </Pressable>
                        }
                        placeholder='搜索'
                    />
                </Center>
                <Center>
                    <FlatList
                        style={styles.userSessionList}
                        contentContainerStyle={{

                        }}
                        ItemSeparatorComponent={() =>
                            <HStack space={4} alignItems='flex-end' justifyContent='center' style={styles.userSessionListItemSeparator} >
                                <HStack flex={1} />
                                <HStack flex={6}>
                                    <Divider style={styles.userSessionListItemSeparatorDivider} />
                                </HStack>
                            </HStack>
                        }
                        data={userSessions}
                        renderItem={({ item }) => {
                            return (
                                <Pressable
                                    onPress={toChatItem}
                                >
                                    {({ isHovered, isFocused, isPressed }) => {
                                        return (
                                            <VStack style={{ backgroundColor: isPressed ? '#C8C6C5' : 'transparent',padding: 10 }}>
                                                <HStack space={4}>
                                                    <HStack flex={1}>
                                                        <Avatar size="60px" _image={{
                                                            borderRadius: 8
                                                        }} source={{ uri: item.avatar }} />
                                                    </HStack>
                                                    <HStack flex={6}>
                                                        <VStack flex={1} justifyContent='space-between'>
                                                            <Text style={styles.userSessionName}>{item.name}</Text>
                                                            <Text style={styles.userSessionLastMsg}>{item.lastMsgContent}</Text>
                                                        </VStack>
                                                        <VStack flex={1} alignItems='flex-end'>
                                                            <Text style={styles.userSessionLastTime}>{formatChatDate(item.lastMsgTime)}</Text>
                                                        </VStack>
                                                    </HStack>
                                                </HStack>
                                            </VStack>

                                        )
                                    }}

                                </Pressable>
                            )
                        }}
                        keyExtractor={item => item.id}
                    />
                </Center>
            </VStack>
        </>
    )
}


const styles = StyleSheet.create({
    rootVStack: {
        backgroundColor: '#F5F5F5',
        flex: 1
    },
    searchCenter: {
        paddingLeft: 10,
        paddingRight: 10,
    },
    userSessionList: {
        width: '100%',
        height: '100%',
    },
    userSessionListItemSeparator: {

    },
    userSessionListItemSeparatorDivider: {
        height: 1,
        backgroundColor: '#D3D3D3',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
    },
    userSessionName: {
        color: '#000000',
        fontWeight: '300',
        fontSize: 16
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

export default Chat;