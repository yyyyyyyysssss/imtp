import { Avatar, FlatList, HStack, Input, Pressable, ScrollView, Text, View, VStack, KeyboardAvoidingView } from 'native-base';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Modal, StyleSheet, TouchableWithoutFeedback } from 'react-native';
import ItemHeader from '../../../components/ItemHeader';
import { useDispatch, useSelector } from 'react-redux';
import { showToast } from '../../../components/Utils';
import Message from '../../../components/Message';
import ChatItemFooter from '../../../components/ChatItemFooter';
import { loadMessage, addMessage } from '../../../redux/slices/chatSlice';
import api from '../../../api/api';
import Storage from '../../../storage/storage';
import Uplaod from '../../../components/Upload';
import { MessageStatus, MessageType } from '../../../enum';
import IdGen from '../../../utils/IdGen';
import { formatFileSize } from '../../../utils/FormatUtil';


const ChatItem = ({ route }) => {

    const { sessionId } = route.params;

    const flatListRef = useRef()

    const userInfoRef = useRef()

    const entitiesMessages = useSelector(state => state.chat.entities.messages)
    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    const { messages } = session
    const dispatch = useDispatch()

    useEffect(() => {
        console.log('init chatItem')
        //未初始化的数据进行初始化
        if (session.messageInit === undefined || session.messageInit === false) {
            console.log('messageInit')
            api.get('/social/userMessage/{userId}', {
                params: {
                    sessionId: sessionId
                }
            })
                .then(
                    (res) => {
                        const messageList = res.data.list
                        Storage.get('userInfo')
                            .then(
                                (userInfo) => {
                                    const newMessageList = messageList.map(item => {
                                        item.self = userInfo.id === item.senderUserId
                                        return item
                                    })
                                    dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList }))
                                }
                            )
                    }
                )
        }

        const fetchUserInfo = async () => {
            const userInfo = await Storage.get('userInfo')
            userInfoRef.current = userInfo
        }

        fetchUserInfo()
        return () => {
            console.log('unmount chatItem')
        }
    }, [])

    //滚动到底部
    useEffect(() => {
        if (messages && messages.length > 0 && flatListRef.current) {
            // flatListRef.current.scrollToEnd({ animated: true })
        }
    }, [messages])

    const moreOps = () => {
        showToast('更多操作')
    }

    const renderItem = ({ item, index }) => {
        const message = entitiesMessages[item]
        return (
            <Message style={{ marginTop: 30 }} message={message} />
        )
    }

    const sendMessage = (message) => {
        const { content, type, width, height, duration, filePath, fileName, fileType, fileSize } = message
        let msg;
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                console.log('sendMessage')
                msg = messageBase(content, type)
                break
            case MessageType.IMAGE_MESSAGE:
                msg = messageBase(filePath, type)
                msg.contentMetadata = {
                    name: fileName,
                    width: width,
                    height: height,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize)
                }
                console.log('IMAGE_MESSAGE', msg)
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize)
                    .then(
                        (res) => {

                        },
                        (error) => {

                        }
                    )
                break
            case MessageType.VIDEO_MESSAGE:
                msg = messageBase('', type)
                const minutes = Math.floor(duration / 60);
                const seconds = Math.floor(duration % 60);
                msg.contentMetadata = {
                    name: fileName,
                    width: width,
                    height: height,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize),
                    duration: duration,
                    durationDesc: `${minutes}:${seconds.toString().padStart(2, '0')}`
                }
                console.log('VIDEO_MESSAGE', msg)
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize)
                    .then(
                        (res) => {

                        },
                        (error) => {

                        }
                    )
                break
            case MessageType.FILE_MESSAGE:
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize)
                    .then(
                        (res) => {

                        },
                        (error) => {

                        }
                    )
                break
            default:
                showToast("Unsupported message type")
        }
        if(msg){
            dispatch(addMessage({ sessionId: sessionId, message: msg }))
        }
    }

    const messageBase = (content, type) => {
        return {
            id: IdGen.nextId(),
            ackId: IdGen.nextId(),
            type: type,
            content: content,
            status: MessageStatus.PENDING,
            sessionId: sessionId,
            senderUserId: session.senderUserId,
            receiverUserId: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfoRef.current.nickname,
            avatar: userInfoRef.current.avatar
        }
    }

    return (

        <VStack flex={1} justifyContent="space-between">
            <ItemHeader title={session.name} moreOps={moreOps} />
            <KeyboardAvoidingView
                flex={1}
                behavior={Platform.OS == "ios" ? "padding" : null}
                keyboardVerticalOffset={Platform.OS === "ios" ? 64 : 0}
            >
                <HStack flex={9} style={styles.contentHstack}>
                    <FlatList
                        ref={flatListRef}
                        style={styles.messageList}
                        data={messages}
                        renderItem={renderItem}
                        scrollEnabled={true}
                        inverted={true}
                        contentContainerStyle={{
                            flexGrow: 1,
                            flexDirection: 'column-reverse',
                        }}
                    />
                </HStack>
                <ChatItemFooter
                    sendMessage={sendMessage}
                />
            </KeyboardAvoidingView>
        </VStack>
    )
}

const styles = StyleSheet.create({
    headHstack: {

    },
    contentHstack: {

    },
    messageList: {
        padding: 10,
    },
    footerHstack: {

    },
    footerHstackInput: {

    }
})

export default ChatItem;