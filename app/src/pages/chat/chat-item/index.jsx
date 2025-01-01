import { Avatar, FlatList, HStack, Input, Pressable, ScrollView, Text, View, VStack, KeyboardAvoidingView, Box } from 'native-base';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { InteractionManager, Modal, StyleSheet, TouchableWithoutFeedback } from 'react-native';
import ChatItemHeader from '../../../components/ChatItemHeader';
import { useDispatch, useSelector } from 'react-redux';
import { showToast } from '../../../components/Utils';
import Message from '../../../components/Message';
import ChatItemFooter from '../../../components/ChatItemFooter';
import { loadMessage, addMessage, updateMessage, selectSession } from '../../../redux/slices/chatSlice';
import api from '../../../api/api';
import Storage from '../../../storage/storage';
import Uplaod from '../../../components/Upload';
import { MessageStatus, MessageType } from '../../../enum';
import IdGen from '../../../utils/IdGen';
import { formatFileSize } from '../../../utils/FormatUtil';
import { createThumbnail } from "react-native-create-thumbnail";


const ChatItem = ({ route }) => {

    const { sessionId } = route.params;

    const flatListRef = useRef()

    const userInfoRef = useRef()

    const [messageIds, setMessageIds] = useState([])

    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    const { messages } = session

    const dispatch = useDispatch()

    useEffect(() => {
        const fetchData = async () => {
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
        const init = async () => {
            const fetchUserInfo = async () => {
                const userInfo = await Storage.get('userInfo')
                userInfoRef.current = userInfo
            }

            fetchUserInfo()
            //选择会话
            dispatch(selectSession({ sessionId: sessionId }))
        }
        //未初始化的数据进行初始化
        InteractionManager.runAfterInteractions(() => {
            setTimeout(() => {
                if (session.messageInit === undefined || session.messageInit === false) {
                    fetchData()
                }
                init()
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

    const moreOps = () => {
        showToast('更多操作')
    }

    const renderItem = ({ item, index }) => {
        return (
            <Message style={{ marginTop: 30 }} messageId={item} />
        )
    }

    const sendMessage = (message) => {
        const { content, type, width, height, duration, filePath, fileName, fileType, fileSize } = message
        let msg;
        switch (type) {
            case MessageType.TEXT_MESSAGE:
                msg = messageBase(content, type)
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
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize, msg.progressId)
                    .then(
                        (res) => {
                            const newMsg = { ...msg, status: MessageStatus.SENT, content: res }
                            dispatch(updateMessage({ message: newMsg }))
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
            case MessageType.FILE_MESSAGE:
                msg = messageBase(filePath, type)
                msg.progressId = IdGen.nextId()
                msg.contentMetadata = {
                    name: fileName,
                    mediaType: fileType,
                    size: fileSize,
                    sizeDesc: formatFileSize(fileSize)
                }
                Uplaod.uploadChunks(filePath, fileName, fileType, fileSize, msg.progressId)
                    .then(
                        (res) => {
                            const newMsg = { ...msg, status: MessageStatus.SENT, content: res }
                            dispatch(updateMessage({ message: newMsg }))
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
        if (msg) {
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
            <ChatItemHeader title={session.name} moreOps={moreOps} />
            <KeyboardAvoidingView
                flex={1}
                behavior={Platform.OS == "ios" ? "padding" : null}
                keyboardVerticalOffset={Platform.OS === "ios" ? 64 : 0}
            >
                <HStack flex={9} style={styles.contentHstack}>
                    <FlatList
                        ref={flatListRef}
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