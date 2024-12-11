import { Avatar, FlatList, HStack, Input, Pressable, ScrollView, Text, View, VStack, KeyboardAvoidingView } from 'native-base';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Modal, StyleSheet } from 'react-native';
import ItemHeader from '../../../components/ItemHeader';
import { useDispatch, useSelector } from 'react-redux';
import { showToast } from '../../../components/Utils';
import Message from '../../../components/Message';
import ChatItemFooter from '../../../components/ChatItemFooter';
import { addMessage } from '../../../redux/slices/chatSlice';


const initData = [
    {
        id: '1',
        type: 1,
        avatar: 'http://localhost:9000/y-chat-bucket/d4b0fb7c889d466183188f286ca03446.jpg',
        name: '卡卡罗特',
        content: '在嘛？弗利萨要来毁灭地球了，快回来拯救地球',
        deliveryMethod: 'SINGLE',
        status: 'FAILED',
        self: true,
        contentMetadata: {

        }
    },
    {
        id: '2',
        type: 4,
        avatar: 'http://localhost:9000/y-chat-bucket/08ddb70d687f445892a294a684ec0ba3.jpg',
        name: '贝吉塔',
        content: 'http://localhost:9000/y-chat-bucket/08ddb70d687f445892a294a684ec0ba3.jpg',
        deliveryMethod: 'GROUP',
        status: 'PENDING',
        self: false,
        contentMetadata: {
            name: '卡卡罗特.jpg',
            width: 700,
            height: 618,
            mediaType: 'image/jpg',
            thumbnailUrl: null,
            duration: null,
            durationDesc: null,
            size: 55602,
            sizeDesc: '54.3K'
        }
    },
    {
        id: '4',
        type: 1,
        avatar: 'http://localhost:9000/y-chat-bucket/d4b0fb7c889d466183188f286ca03446.jpg',
        name: '孙悟饭',
        content: '好的吧',
        deliveryMethod: 'SINGLE',
        status: 'FAILED',
        self: true,
        contentMetadata: {

        }
    }
]

const ChatItem = ({route }) => {

    const { sessionId } = route.params;

    const flatListRef = useRef()

    const entities = useSelector(state => state.chat.entities)
    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    const {messages} = session
    const dispatch = useDispatch()

    useEffect(() => {
        dispatch(addMessage({sessionId:sessionId,message: initData[0]}))
    },[])

    const moreOps = () => {
        showToast('更多操作')
    }

    const renderItem = ({ item, index }) => {
        console.log('message',item)
        const message = entities.messages[item]
        return (
            <Message style={{ marginTop: 30 }} message={message} />
        )
    }

    return (
        <>
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
                            // scrollEnabled={true}
                            inverted={true}
                            contentContainerStyle={{
                                flexGrow: 1,
                                flexDirection: 'column-reverse',

                            }}
                        />
                    </HStack>
                    <ChatItemFooter />
                </KeyboardAvoidingView>
            </VStack>
        </>
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