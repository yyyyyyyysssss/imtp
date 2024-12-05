import { Button, Center, FlatList, Image, Input, Pressable, VStack } from 'native-base';
import React, { useEffect, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import { useDispatch, useSelector } from 'react-redux';
import { initSession,addSession } from '../../redux/slices/chatSlice';


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
                    <Button onPress={toChatItem}>This is Chat</Button>
                    {/* <FlatList

                    /> */}
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
    }
})

export default Chat;