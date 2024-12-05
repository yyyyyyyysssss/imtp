import { Button, Center, FlatList, Avatar, Input, Pressable, VStack, HStack, Box, Text, View } from 'native-base';
import React, { useEffect, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import { useDispatch, useSelector } from 'react-redux';
import { initSession, addSession } from '../../redux/slices/chatSlice';


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
                    {/* <Button onPress={toChatItem}>This is Chat</Button> */}
                    <FlatList
                        style={styles.userSessionList}
                        contentContainerStyle={{

                        }}
                        ItemSeparatorComponent={() => <View style={{ height: 15 }} />}
                        data={userSessions}
                        renderItem={({ item }) => {
                            return (
                                <HStack space={3}>
                                    <Avatar size="56px" _image={{
                                        borderRadius: 8
                                    }} source={{ uri: item.avatar }} />
                                    <HStack flex={1}>
                                        <VStack flex={1} justifyContent='space-between'>
                                            <Text>{item.name}</Text>
                                            <Text>{item.lastMsgContent}</Text>
                                        </VStack>
                                        <VStack flex={1} alignItems='flex-end' style={{paddingRight: 5}}>
                                            <Text>{item.lastMsgTime}</Text>
                                        </VStack>
                                    </HStack>
                                </HStack>
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
        padding: 10
    },
    lastMsgVStack: {}
})

export default Chat;