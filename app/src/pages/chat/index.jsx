import { Button, Center, FlatList, Avatar, Input, Pressable, VStack, HStack, Box, Text, Flex, Divider, Icon, ScrollView } from 'native-base';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import { useDispatch, useSelector, shallowEqual } from 'react-redux';
import { initSession, addSession, selectSession, removeSession, incrUnreadCount, decrUnreadCount } from '../../redux/slices/chatSlice';
import { formatChatDate } from '../../utils';
import { SwipeListView } from 'react-native-swipe-list-view';
import { showToast } from '../../components/Utils';
import Search from '../../components/Search';
import SwipeItemOperation from '../../components/SwipeItemOperation';
import UserSessionItem from '../../components/UserSessionItem';


const initData = [
    {
        id: '1',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '2',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '3',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '4',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '5',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '6',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '7',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: ''
    },
    {
        id: '8',
        name: '张三',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: 1725499854000
    },
    {
        id: '9',
        name: '李四',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: 1725499854000
    },
    {
        id: '10',
        name: '王二',
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        lastMsgContent: '',
        lastMsgTime: 1725499854000
    }
]

const Chat = () => {
    const navigation = useNavigation();

    const userSessions = useSelector(state => state.chat.userSessions, shallowEqual)
    const dispatch = useDispatch()

    console.log('Chat')

    //查询用户会话
    useEffect(() => {
        api.get('/social/userSession/{userId}')
            .then(
                (res) => {
                    const userSessionList = res.data
                    if (userSessionList) {
                        //初始化会话
                        dispatch(initSession(userSessionList))
                    }

                }
            )
    }, [])

    const toChatItem = (userSession) => {
        dispatch(selectSession(userSession))
        navigation.navigate('ChatItem')
    }

    const itemSeparator = useCallback(() => {
        return (
            <HStack space={7} alignItems='flex-end' justifyContent='center' style={styles.userSessionListItemSeparator} >
                <HStack flex={1}>

                </HStack>
                <HStack flex={6}>
                    <Divider style={styles.userSessionListItemSeparatorDivider} />
                </HStack>
            </HStack>
        )
    }, [])

    const renderItem = ({ item, index }) => {
        return (
            <Pressable
                onPress={() => toChatItem(item)}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <VStack style={{ backgroundColor: isPressed ? '#C8C6C5' : '#F5F5F5', padding: 10 }}>
                            <UserSessionItem
                                avatar={item.avatar}
                                name={item.name}
                                lastMsgType={item.lastMsgType}
                                lastMsgContent={item.lastMsgContent}
                                lastMsgTime={item.lastMsgTime}
                                unreadMessageCount={item.unreadMessageCount}
                            />
                        </VStack>
                    )
                }}

            </Pressable>
        )
    }

    const renderHiddenItem = (data, rowMap) => {
        return (
            <HStack flex="1" pl="2" justifyContent='flex-end'>
                <Pressable
                    style={{ width: 90 }}
                    cursor="pointer"
                    bg="red.500"
                    justifyContent="center"
                    onPress={() => deleteUserSession(rowMap, data.item.id)}
                    _pressed={{
                        opacity: 0.5
                    }}>
                    <VStack alignItems="center">
                        <Text color="white" fontSize="17" fontWeight="medium">
                            删除
                        </Text>
                    </VStack>
                </Pressable>
            </HStack>
        )
    }

    const onRowDidOpen = rowKey => {
        // console.log('onRowDidOpen',rowKey)
    }

    const onRowClose = rowKey => {
        // console.log('onRowClose',rowKey)
    }

    const deleteUserSession = (rowMap, id) => {
        dispatch(removeSession(id))
    };

    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Search />
                <SwipeListView
                    style={styles.userSessionList}
                    keyExtractor={item => item.id}
                    data={userSessions}
                    renderItem={renderItem}
                    ItemSeparatorComponent={itemSeparator}
                    renderHiddenItem={renderHiddenItem}
                    rightOpenValue={-120}
                    disableRightSwipe={true}
                    onRowDidOpen={onRowDidOpen}
                    onRowClose={onRowClose}
                />
            </VStack>
        </>
    )
}


const styles = StyleSheet.create({
    rootVStack: {
        backgroundColor: '#F5F5F5',
        flex: 1,
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

export default Chat;