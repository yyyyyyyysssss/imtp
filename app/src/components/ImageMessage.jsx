import { Box, Image, Pressable, Spinner } from 'native-base';
import React, { useState } from 'react';
import { ImageContext } from '../context';
import { Modal, StyleSheet } from 'react-native';
import ImageViewer from 'react-native-image-zoom-viewer';
import { MessageStatus } from '../enum';


const ImageMessage = ({ content, status, contentMetadata }) => {

    const [showImage, setShowImage] = useState({
        isVisible: false,
        imageUrl: null
    })

    const showOriginalImage = (url) => {
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

    return (
        <>
            <Pressable
                onPress={() => showOriginalImage(content)}
            >
                <Box
                    style={{
                        position: 'relative'
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
                        alt=''
                    />
                    {status && status === MessageStatus.PENDING && (
                        <Spinner
                            style={{
                                position: 'absolute',
                                top: '50%',
                                left: '50%',
                                transform: 'translate(-50%, -50%)'
                            }}
                            size={50}
                            color="gray.500"
                        />
                    )}
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
                    <ImageViewer
                        onClick={closeModal}
                        onSwipeDown={closeModal} // 下滑关闭 Modal
                        enableSwipeDown={true}
                        imageUrls={[
                            {
                                url: showImage.imageUrl
                            }
                        ]}
                    />
                </Modal>
            </Pressable>
        </>
    )
}

export default ImageMessage