import { Avatar, FlatList, HStack, Input, Pressable, ScrollView, Text, View, VStack, KeyboardAvoidingView, Box } from 'native-base';
import React, { useCallback, useContext, useEffect, useRef, useState } from 'react';
import { InteractionManager, Modal, StyleSheet, TouchableWithoutFeedback } from 'react-native';
import ChatItemHeader from '../../../components/ChatItemHeader';
import { useDispatch, useSelector } from 'react-redux';
import { showToast } from '../../../components/Utils';
import Message from '../../../components/Message';
import ChatItemFooter from '../../../components/ChatItemFooter';
import { loadMessage, addMessage, updateMessage, selectSession } from '../../../redux/slices/chatSlice';
import Uplaod from '../../../components/Upload';
import { MessageStatus, MessageType } from '../../../enum';
import IdGen from '../../../utils/IdGen';
import { formatFileSize } from '../../../utils/FormatUtil';
import { createThumbnail } from "react-native-create-thumbnail";
import { NativeModules } from 'react-native';
import { UserInfoContext } from '../../../context';
import { fetchMessageByUserSessionId } from '../../../api/ApiService';
import { useNavigation, } from '@react-navigation/native';

const { MessageModule } = NativeModules

const ChatItem = ({ route }) => {

    const navigation = useNavigation();

    const { sessionId } = route.params;

    const userInfo = useContext(UserInfoContext)

    const [messageIds, setMessageIds] = useState([])

    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    const { messages } = session

    const dispatch = useDispatch()
    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchMessageByUserSessionId(sessionId)
            const messageList = data.list
            const newMessageList = messageList.map(item => {
                item.self = userInfo.id === item.senderUserId
                return item
            })
            dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList }))
        }
        //未初始化的数据进行初始化
        InteractionManager.runAfterInteractions(() => {
            setTimeout(() => {
                // 当前选择会话
                dispatch(selectSession({ sessionId: sessionId }))
                if (session.messageInit === undefined || session.messageInit === false) {
                    fetchData()
                }
            }, 100);
        })
        return () => {

        }
    }, [])

    //加载消息
    useEffect(() => {
        InteractionManager.runAfterInteractions(() => {
            setTimeout(() => {
                setMessageIds(messages)
            }, 100)
        })
    }, [messages])

    // 监听路由退出前事件
    useEffect(() => {
        navigation.addListener('beforeRemove', (e) => {
            dispatch(selectSession({ sessionId: null }))
        })
    }, [navigation])

    const renderItem = ({ item, index }) => {
        return (
            <Message style={{ marginTop: 30 }} messageId={item} />
        )
    }

    const sendMessage = useCallback((message) => {
        const { content, type, width, height, duration, filePath, fileName, fileType, fileSize } = message
        let msg;
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                msg = messageBase(content, type)
                //添加消息
                dispatch(addMessage({ sessionId: sessionId, message: msg }))
                //向服务器发送消息
                realSendMessage(msg)
                break
            case MessageType.IMAGE_MESSAGE:
                msg = messageBase(filePath, type)
                msg.progressId = IdGen.nextId()
                msg.contentMetadata = {
                    name: fileName,
                    width: width,
                    height: height,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize)
                }
                //添加消息
                dispatch(addMessage({ sessionId: sessionId, message: msg }))
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize, msg.progressId)
                    .then(
                        (res) => {
                            const newMsg = { ...msg, status: MessageStatus.SENT, content: res }
                            dispatch(updateMessage({ message: newMsg }))
                            //向服务器发送消息
                            realSendMessage(newMsg)
                        },
                        (error) => {
                            const newMsg = { ...msg, status: MessageStatus.FAILED }
                            dispatch(updateMessage({ message: newMsg }))
                        }
                    )
                break
            case MessageType.VIDEO_MESSAGE:
                msg = messageBase('', type)
                msg.progressId = IdGen.nextId()
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
                //添加消息
                dispatch(addMessage({ sessionId: sessionId, message: msg }))
                //获取视频封面
                createThumbnail({
                    url: filePath,
                    timeStamp: 1000
                })
                    .then(
                        response => {
                            const path = response.path
                            const mime = response.mime
                            const thumbnaiFileName = path.substring(path.lastIndexOf('/') + 1) + '.' + mime.substring(mime.lastIndexOf('/') + 1)
                            const size = response.size
                            //上传海报
                            Uplaod.uploadChunks(path, thumbnaiFileName, mime, size)
                                .then(
                                    (res) => {
                                        const newMsg = { ...msg, contentMetadata: { ...msg.contentMetadata, thumbnailUrl: res } }
                                        dispatch(updateMessage({ message: newMsg }))
                                        //上传视频
                                        Uplaod.uploadChunks(filePath, fileName, fileType, fileSize, msg.progressId)
                                            .then(
                                                (res) => {
                                                    const newMsg2 = { ...newMsg, status: MessageStatus.SENT, content: res }
                                                    dispatch(updateMessage({ message: newMsg2 }))
                                                    //向服务器发送消息
                                                    realSendMessage(newMsg2)
                                                },
                                                (error) => {
                                                    const newMsg2 = { ...newMsg, status: MessageStatus.FAILED }
                                                    dispatch(updateMessage({ message: newMsg2 }))
                                                }
                                            )
                                    },
                                    (error) => {
                                        const newMsg = { ...msg, status: MessageStatus.FAILED }
                                        dispatch(updateMessage({ message: newMsg }))
                                    }
                                )
                        }
                    )
                    .catch(err => {
                        const newMsg = { ...msg, status: MessageStatus.FAILED }
                        dispatch(updateMessage({ message: newMsg }))
                    })
                break
            case MessageType.VOICE_MESSAGE:
                msg = messageBase(filePath, type)
                msg.contentMetadata = {
                    name: fileName,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize),
                    duration: duration
                }
                //添加消息
                dispatch(addMessage({ sessionId: sessionId, message: msg }))
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize)
                    .then(
                        (res) => {
                            const newMsg = { ...msg, status: MessageStatus.SENT, content: res }
                            dispatch(updateMessage({ message: newMsg }))
                            //向服务器发送消息
                            realSendMessage(newMsg)
                        },
                        (error) => {
                            console.log('error',error)
                            const newMsg = { ...msg, status: MessageStatus.FAILED }
                            dispatch(updateMessage({ message: newMsg }))
                        }
                    )
                break
            case MessageType.FILE_MESSAGE:
                msg = messageBase(filePath, type)
                msg.progressId = IdGen.nextId()
                msg.contentMetadata = {
                    name: fileName,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize)
                }
                //添加消息
                dispatch(addMessage({ sessionId: sessionId, message: msg }))
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize, msg.progressId)
                    .then(
                        (res) => {
                            const newMsg = { ...msg, status: MessageStatus.SENT, content: res }
                            dispatch(updateMessage({ message: newMsg }))
                            //向服务器发送消息
                            realSendMessage(newMsg)
                        },
                        (error) => {
                            const newMsg = { ...msg, status: MessageStatus.FAILED }
                            dispatch(updateMessage({ message: newMsg }))
                        }
                    )
                break
            default:
                showToast("Unsupported message type")
        }
    },[])

    const realSendMessage = (msg) => {
        MessageModule.sendMessage(JSON.stringify(msg))
            .then(
                (res) => {
                    console.log('send succeed')
                },
                (error) => {
                    console.log('send failed', error)
                }
            )
    }

    const messageBase = (content, type) => {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: type,
            content: content,
            status: MessageStatus.PENDING,
            sessionId: sessionId,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfo.nickname,
            avatar: userInfo.avatar
        }
    }

    return (

        <VStack flex={1} justifyContent="space-between">
            <ChatItemHeader title={session.name} />
            <KeyboardAvoidingView
                flex={1}
                behavior={Platform.OS == "ios" ? "padding" : null}
                keyboardVerticalOffset={Platform.OS === "ios" ? 64 : 0}
            >
                <HStack flex={9} style={styles.contentHstack}>
                    <FlatList
                        style={styles.messageList}
                        data={messageIds ? [...messageIds].reverse() : []}
                        renderItem={renderItem}
                        scrollEnabled={true}
                        inverted={true}
                        contentContainerStyle={{
                            flexGrow: 1,
                            justifyContent: 'flex-end'
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
        padding: 10
    },
    footerHstack: {

    },
    footerHstackInput: {

    }
})

export default ChatItem;