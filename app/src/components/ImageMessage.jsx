import { Box, Image, ImageBackground, Pressable, Spinner } from 'native-base';
import React, { useState } from 'react';
import { ImageContext } from '../context';
import { Modal, StyleSheet } from 'react-native';
import ImageViewer from 'react-native-image-zoom-viewer';
import { MessageStatus } from '../enum';


const ImageMessage = React.memo(({ content, status }) => {

    const [showImage, setShowImage] = useState({
        isVisible: false,
        imageUrl: null
    })

    const showOriginalImage = (url) => {
        if(status && status === MessageStatus.PENDING){
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
                    {status && status === MessageStatus.PENDING && (
                        <Spinner
                            style={styles.absolute}
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