import { Box, Image, NativeBaseProvider, Pressable } from 'native-base';
import React, { useState } from 'react';
import { Modal, StyleSheet } from 'react-native';
import ImageViewer from 'react-native-image-zoom-viewer';
import { MessageStatus } from '../enum';
import { savePicture } from '../utils/CameraRollUtil';


const ImageMessage = React.memo(({ content, status }) => {

    const [showImage, setShowImage] = useState({
        isVisible: false,
        imageUrl: null
    })

    const showOriginalImage = (url) => {
        if (status && status === MessageStatus.PENDING) {
            return
        }
        setShowImage({
            isVisible: true,
            imageUrl: url
        })
    }

    const closeModal = () => {
        setShowImage({
            isVisible: false,
            imageUrl: null
        })
    }

    const saveToCamera = (url) => {
        savePicture(url, 'photo')
    }

    return (
        <>
            <Pressable
                onPress={() => showOriginalImage(content)}
            >
                <Box
                    style={{
                        position: 'relative',
                        justifyContent: 'center',
                        alignItems: 'center'
                    }}
                >
                    <Image
                        rounded={8}
                        size={200}
                        shadow={3}
                        resizeMode='cover'
                        source={{
                            uri: content
                        }}
                        blurRadius={status && status === MessageStatus.PENDING ? 10 : 0}
                        alt=''
                    />
                </Box>
            </Pressable>
            <Pressable
                onPress={closeModal}
            >
                <Modal
                    visible={showImage.isVisible}
                    transparent={true}
                    onRequestClose={closeModal}
                >
                    <NativeBaseProvider>
                        <ImageViewer
                            onClick={closeModal}
                            onSwipeDown={closeModal} // 下滑关闭 Modal
                            enableSwipeDown={true}
                            onSave={saveToCamera}
                            imageUrls={[
                                {
                                    url: showImage.imageUrl
                                }
                            ]}
                            menuContext={{
                                saveToLocal: '保存到相册',
                                cancel: "取消"
                            }}
                        />
                    </NativeBaseProvider>
                </Modal>
            </Pressable>
        </>
    )
})

const styles = StyleSheet.create({
    absolute: {
        position: "absolute",
        top: 0,
        left: 0,
        bottom: 0,
        right: 0
    }
})

export default ImageMessage