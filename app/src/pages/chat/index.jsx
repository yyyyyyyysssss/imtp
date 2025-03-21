import { Pressable, VStack, HStack, Text, Divider } from 'native-base';
import React, { useCallback, useContext, useEffect, useRef, useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { InteractionManager, StyleSheet, NativeModules, NativeEventEmitter } from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { loadSession, removeSession, updateMessageStatus, addMessage, selectSession, addSession, callBegin } from '../../redux/slices/chatSlice';
import { SwipeListView } from 'react-native-swipe-list-view';
import { showToast } from '../../components/Utils';
import Search from '../../components/Search';
import UserSessionItem from '../../components/UserSessionItem';
import { normalize, schema } from 'normalizr';
import { CallOperation, DeliveryMethod, MessageType } from '../../enum';
import { fetchUserSessions, deleteUserSessionById, createUserSession } from '../../api/ApiService';
import { getBitAtPosition } from '../../utils/BitUtil';
import IdGen from '../../utils/IdGen';
import WebRTCWrapper from '../../rtc/WebRTCWrapper';

const { MessageModule } = NativeModules
const MessageModuleNativeEventEmitter = new NativeEventEmitter(MessageModule);


const Chat = (props) => {

    const navigation = useNavigation()
    const dispatch = useDispatch()

    const callFlag = useSelector(state => state.chat.call.flag)
    const callFlageRef = useRef()

    useEffect(() => {
        callFlageRef.current = callFlag
    },[callFlag])

    const result = useSelector(state => state.chat.result)

    const sessions = useSelector(state => state.chat.entities.sessions)

    const userInfo = useSelector(state => state.auth.userInfo)

    const sessionMapRef = useRef(new Map())

    const { findFriendByFriendId, findGroupByGroupId, findFriendByGroupIdAndFriendId } = props

    //初始查询用户会话
    useEffect(() => {
        const fetchData = async () => {
            const userSessionList = await fetchUserSessions() || []
            if (userSessionList) {
                const message = new schema.Entity('messages')
                const session = new schema.Entity('sessions', {
                    messages: [message]
                })
                const normalizedData = normalize(userSessionList, [session]);
                //初始化加载会话
                dispatch(loadSession(normalizedData))
            }
        }
        if (!result || !result.length) {
            fetchData()
        }

        //接收消息监听
        const receiveMessageEventEmitter = MessageModuleNativeEventEmitter.addListener('RECEIVE_MESSAGE', (message) => {
            receiveMessageHandler(message)
        })

        return () => {
            receiveMessageEventEmitter.remove()
        }
    }, [])

    useEffect(() => {
        sessionMapRef.current = new Map()
        if (!sessions) {
            return
        }
        for (const session of Object.values(sessions)) {
            sessionMapRef.current.set(session.receiverUserId, session)
        }
    }, [result])

    const receiveMessageHandler = async (message) => {
        const msg = JSON.parse(message)
        const { header } = msg
        const { cmd, sender, receiver, reserved } = header
        //消息响应
        if (cmd === MessageType.COMMON_RESPONSE) {
            const { ackId, state } = msg
            dispatch(updateMessageStatus({ id: ackId, status: state }))
            return
        }
        const { contentMetadata, timestamp } = msg
        const groupBit = getBitAtPosition(reserved, 0)
        const isGroup = groupBit === 1
        const deliveryMethod = isGroup ? DeliveryMethod.GROUP : DeliveryMethod.SINGLE
        const realSender = isGroup ? receiver : sender;
        // 会话是否存在 不存在则先新增会话
        let session = sessionMapRef.current.get(realSender)
        const friendInfo = findFriendInfo(sender, realSender, deliveryMethod)
        if (session) {
            session = {
                ...session,
                lastMsgType: cmd,
                lastMsgTime: timestamp,
                lastUserName: friendInfo.note
            }
        } else {
            const info = deliveryMethod === DeliveryMethod.GROUP ? findGroupByGroupId(realSender) : friendInfo
            const sessionId = await createUserSession(realSender, deliveryMethod)
            session = {
                id: sessionId,
                userId: userInfo.id,
                name: info.note,
                avatar: info.avatar,
                receiverUserId: realSender,
                deliveryMethod: deliveryMethod,
                lastMsgType: cmd,
                lastMsgTime: timestamp,
                lastUserName: friendInfo.note
            }
            dispatch(addSession({ session: session }))
        }
        let content;
        switch (cmd) {
            case MessageType.TEXT_MESSAGE:
                content = msg.text;
                break;
            case MessageType.IMAGE_MESSAGE:
                content = msg.url;
                break;
            case MessageType.VIDEO_MESSAGE:
                content = msg.url;
                break;
            case MessageType.FILE_MESSAGE:
                content = msg.url;
                break;
            case MessageType.VOICE_CALL_MESSAGE:
                content = null;
                break;
            case MessageType.VIDEO_CALL_MESSAGE:
                content = null;
                break;
            case MessageType.SIGNALING_PRE_OFFER:
                if (callFlageRef.current === false) {
                    navigation.navigate('Call', {
                        sessionId: session.id,
                        callType: msg.content,
                        callOperation: CallOperation.ACCEPT
                    })
                } else {
                    //发送忙线
                    WebRTCWrapper.sendBusy(session)
                }
                return
            default:
                return
        }
        //添加消息
        const receiveMessage = messageBase(content, contentMetadata, cmd, timestamp, friendInfo, session)
        dispatch(addMessage({ sessionId: session.id, message: receiveMessage }))
    }

    const findFriendInfo = (friendId, groupId, deliveryMethod) => {

        return deliveryMethod === DeliveryMethod.SINGLE ? findFriendByFriendId(friendId) : findFriendByGroupIdAndFriendId(groupId, friendId)
    }

    const messageBase = (content, contentMetadata, type, timestamp, friendInfo, session) => {

        return {
            id: IdGen.nextId(),
            content: content,
            type: type,
            timestamp: timestamp,
            contentMetadata: contentMetadata,
            self: false,
            sessionId: session.id,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            name: friendInfo.note,
            avatar: friendInfo.avatar
        }
    }

    const toChatItem = (sessionId) => {
        navigation.navigate('ChatItem', {
            sessionId: sessionId,
        })
    }

    const itemSeparator = useCallback(() => {
        return (
            <HStack space={5} alignItems='flex-end' justifyContent='center' style={styles.userSessionListItemSeparator} >
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
                            <UserSessionItem sessionId={item} />
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
                    onPress={() => removeUserSession(rowMap, data.item)}
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

    const removeUserSession = (rowMap, id) => {
        dispatch(removeSession({ sessionId: id }))
        deleteUserSessionById(id)
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
                    ListFooterComponent={(
                        <Divider style={styles.userSessionListItemSeparatorDivider} />
                    )}
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
        height: '100%'
    },
    userSessionListItemSeparator: {
        paddingLeft: 10
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