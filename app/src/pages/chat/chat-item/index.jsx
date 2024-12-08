import { Avatar, FlatList, HStack, Pressable, Text, View, VStack } from 'native-base';
import React, { useCallback, useState } from 'react';
import { Modal, StyleSheet } from 'react-native';
import ItemHeader from '../../../components/ItemHeader';
import { useDispatch, useSelector } from 'react-redux';
import { showToast } from '../../../components/Utils';
import Message from '../../../components/Message';
import ImageViewer from 'react-native-image-zoom-viewer';
import { ImageContext } from '../../../context';


const initData = [
    {
        id: '1',
        type: 1,
        avatar: 'http://localhost:9000/y-chat-bucket/a21f5d18bb074b16b0e5288264f7ec82.jpg',
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
        id: '3',
        type: 4,
        avatar: 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg',
        name: '比克大魔王',
        content: 'http://localhost:9000/y-chat-bucket/d4b0fb7c889d466183188f286ca03446.jpg',
        deliveryMethod: 'GROUP',
        status: 'PENDING',
        self: true,
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
    }
]

const ChatItem = () => {

    const selectedUserSession  = useSelector(state => state.chat.selectedUserSession)
    const dispatch = useDispatch()

    const [isVisible, setIsVisible] = useState(false)
    const [imageUrl, setImageUrl] = useState(null)


    const moreOps = () => {
        showToast('更多操作')
    }

    const itemSeparator = useCallback(() => {

        return (
            <View style={{ height: 30 }} />
        )
    }, [])

    const showOriginalImage = (url) => {
        setImageUrl(url)
        setIsVisible(true)
    }

    const closeModal = () => {
        setIsVisible(false)
        setImageUrl(null)
    }

    const renderItem = useCallback(({ item, index }) => {

        return (
            <ImageContext.Provider value={{showOriginalImage}}>
                <Message message={item} />
            </ImageContext.Provider>
        )
    }, [initData])

    return (
        <>
            <VStack flex={1} justifyContent="space-between">
                <ItemHeader flex={0.7} title={selectedUserSession?.name} moreOps={moreOps} />
                <HStack flex={8.5} style={styles.contentHstack}>
                    <FlatList
                        style={styles.messageList}
                        data={initData}
                        ItemSeparatorComponent={itemSeparator}
                        renderItem={renderItem}
                    />
                </HStack>
                <HStack flex={0.8} style={styles.footerHstack}>

                </HStack>
            </VStack>
            <Pressable
                onPress={closeModal}
            >
                <Modal
                    visible={isVisible}
                    transparent={true}
                    onRequestClose={closeModal}
                >
                    <ImageViewer
                        onClick={closeModal}
                        onSwipeDown={closeModal} // 下滑关闭 Modal
                        enableSwipeDown={true}
                        imageUrls={[
                            {
                                url: imageUrl
                            }
                        ]}
                    />
                </Modal>
            </Pressable>
        </>
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
        backgroundColor: 'yellow'
    }
})

export default ChatItem;