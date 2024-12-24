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
                        position: 'relative',
                        top: 0, 
                        left: 0, 
                        right: 0, 
                        bottom: 0, 
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
                        alt=''
                    />
                    {status && status === MessageStatus.PENDING && (
                        <Spinner
                            style={{
                                position: 'absolute',
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

const styles = StyleSheet.create({
    
})

export default ImageMessage