import { Button, Center, FlatList, Avatar, Input, Pressable, VStack, HStack, Box, Text, Flex, Divider, Icon, ScrollView } from 'native-base';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import { useDispatch, useSelector, shallowEqual } from 'react-redux';
import { initSession, addSession,loadSession, selectSession, removeSession, incrUnreadCount, decrUnreadCount } from '../../redux/slices/chatSlice';
import { formatChatDate } from '../../utils/FormatUtil';
import { SwipeListView } from 'react-native-swipe-list-view';
import { showToast } from '../../components/Utils';
import Search from '../../components/Search';
import SwipeItemOperation from '../../components/SwipeItemOperation';
import UserSessionItem from '../../components/UserSessionItem';
import { normalize,schema } from 'normalizr';


const Chat = () => {
    const navigation = useNavigation();

    // const userSessions = useSelector(state => state.chat.userSessions, shallowEqual)
    const dispatch = useDispatch()

    const entities = useSelector(state => state.chat.entities)
    const result = useSelector(state => state.chat.result)

    //查询用户会话
    useEffect(() => {
        api.get('/social/userSession/{userId}')
            .then(
                (res) => {
                    const userSessionList = res.data
                    if (userSessionList) {
                        const message = new schema.Entity('messages')
                        const session = new schema.Entity('sessions',{
                            messages: [message]
                        })
                        const normalizedData = normalize(userSessionList, [session]);
                        //初始化会话
                        // dispatch(initSession(userSessionList))
                        //初始化加载会话
                        dispatch(loadSession(normalizedData))
                    }

                }
            )
    }, [])

    const toChatItem = (sessionId) => {
        // dispatch(selectSession(userSession))
        navigation.navigate('ChatItem',{
            sessionId: sessionId,
        })
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
        const session = entities.sessions[item]
        return (
            <Pressable
                onPress={() => toChatItem(item)}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <VStack style={{ backgroundColor: isPressed ? '#C8C6C5' : '#F5F5F5', padding: 10 }}>
                            <UserSessionItem
                                avatar={session.avatar}
                                name={session.name}
                                lastMsgType={session.lastMsgType}
                                lastMsgContent={session.lastMsgContent}
                                lastMsgTime={session.lastMsgTime}
                                unreadMessageCount={session.unreadMessageCount}
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
                    keyExtractor={item => item}
                    data={result}
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