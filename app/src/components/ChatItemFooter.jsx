import React, { useEffect, useRef, useState } from 'react';
import { Box, HStack, Text, VStack, Input, Pressable, KeyboardAvoidingView, Divider, Flex, ScrollView } from 'native-base';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import Feather from 'react-native-vector-icons/Feather';
import SimpleLineIcons from 'react-native-vector-icons/SimpleLineIcons';
import FontAwesome from 'react-native-vector-icons/FontAwesome';
import { showToast } from './Utils';
import { StyleSheet, Modal, View } from 'react-native';
import EmojiPicker, { tr } from 'rn-emoji-keyboard'
import { launchCamera, launchImageLibrary } from 'react-native-image-picker';
import ImagePicker from 'react-native-image-crop-picker';
import { MessageType } from '../enum';

const ChatItemFooter = ({ sendMessage }) => {

    const [isOpen, setIsOpen] = useState(false)

    const inputRef = useRef()

    const handleSubmit = (event) => {
        const text = event.nativeEvent.text
        messageProvider({
            type: 'text',
            content: text
        })
        inputRef.current.clear()
    }

    const messageProvider = (media) => {
        const { content, uri, type, fileName, fileSize, width, height, duration } = media
        let message = null
        if(type.startsWith('image')){
            message = {
                type: MessageType.IMAGE_MESSAGE,
                fileName: fileName,
                filePath: uri,
                fileType: type,
                fileSize: fileSize,
                width: width,
                height: height
            }
        }else if(type.startsWith('video')){
            message = {
                type: MessageType.VIDEO_MESSAGE,
                fileName: fileName,
                filePath: uri,
                fileType: type,
                fileSize: fileSize,
                fileDuration: duration,
                width: width,
                height: height
            }
        }else if(type.startsWith('text')){
            message = {
                type: MessageType.TEXT_MESSAGE,
                content: content,
            }
        }else {
            return
        }
        sendMessage(message)
    }

    const handleInputFocus = () => {
        setIsOpen(false)
    }

    const selectEmoji = () => {

    }

    const handleMoreOps = () => {
        if (isOpen) {
            setIsOpen(false)
        } else {
            inputRef.current?.blur()
            setIsOpen(true)
        }

    }

    //选择照片
    const selectPhoto = () => {
        launchImageLibrary(
            {
                mediaType: 'mixed',
                selectionLimit: 10
            },
            res => {
                if (res.didCancel) {
                    console.log('用户取消选择')
                } else if (res.errorCode) {
                    console.log('选择时发生错误: ', res.errorMessage)
                } else {
                    const assets = res.assets
                    assets.forEach(media => {
                        messageProvider(media)
                    })
                }
            }
        )
    }

    //拍照
    const takePicture = () => {
        launchCamera(
            {
                mediaType: 'photo',
            },
            res => {
                if (res.didCancel) {
                    console.log('用户取消拍摄')
                } else if (res.errorCode) {
                    console.log('拍摄时发生错误: ', res.errorMessage)
                } else {
                    const assets = res.assets
                    assets.forEach(media => {
                        messageProvider(media)
                    })
                }
            }
        )
    }

    //摄像
    const cameraShoot = () => {
        launchCamera(
            {
                mediaType: 'video',
                videoQuality: 'high',
                cameraType: "back",
                durationLimit: 30
            },
            res => {
                if (res.didCancel) {
                    console.log('用户取消摄像')
                } else if (res.errorCode) {
                    console.log('摄像时发生错误: ', res.errorMessage)
                } else {
                    const assets = res.assets
                    assets.forEach(media => {
                        messageProvider(media)
                    })
                }
            }
        )
    }

    return (
        <>
            <VStack style={{
                overflow: 'hidden',
                paddingTop: 5,
                height: isOpen ? 300 : 65,
                backgroundColor: '#F5F5F5',
                shadowColor: '#000',
                shadowOffset: { width: 1, height: 1 },
                shadowOpacity: 0.4,
                shadowRadius: 3,
                elevation: 5,
                borderTopColor: 'blank',
                borderTopWidth: 0.1
            }}>

                <HStack
                    flex={1}
                    justifyContent='center'
                    alignItems='center'
                    style={{

                    }}
                >
                    <HStack flex={1} justifyContent='center'>
                        <MaterialIcon name="keyboard-voice" size={30} />
                    </HStack>
                    <HStack flex={5.5} justifyContent='center' alignItems='center'>
                        <Input
                            ref={inputRef}
                            size='md'
                            focusOutlineColor='none'
                            borderWidth={0}
                            backgroundColor='white'
                            onSubmitEditing={handleSubmit}
                            onFocus={handleInputFocus}
                            w={{
                                base: "100%",
                            }}
                        />
                    </HStack>
                    <HStack flex={2.5} justifyContent='center' alignContent='center' space={3}>
                        <Pressable onPress={selectEmoji}>
                            <SimpleLineIcons name="emotsmile" size={25} />
                        </Pressable>
                        <Pressable onPress={handleMoreOps}>
                            <SimpleLineIcons name="plus" size={25} />
                        </Pressable>

                    </HStack>
                </HStack>

                {/* <EmojiPicker onEmojiSelected={handleEmojiSelected} open={isOpen} onClose={() => setIsOpen(false)} /> */}
                {isOpen && (
                    <Flex
                        direction="column"
                        justifyContent='center'
                        alignItems='center'
                        style={{
                            width: '100%',
                            height: 235,
                            borderTopWidth: 1,
                            borderTopColor: '#D3D3D3',
                        }}
                    >
                        <Flex
                            direction="row"
                            wrap="wrap"
                            style={{
                                width: '85%',
                                height: '100%',
                                padding: 10
                            }}
                            gap={6}
                        >

                            <VStack alignItems='center' space={2}>
                                <Pressable onPress={selectPhoto}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="photo" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>照片</Text>
                            </VStack>

                            <VStack alignItems='center' space={2}>
                                <Pressable onPress={takePicture}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="camera-alt" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>拍照</Text>
                            </VStack>

                            <VStack alignItems='center' space={2}>
                                <Pressable onPress={cameraShoot}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="video-camera-back" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>拍摄</Text>
                            </VStack>

                            <VStack alignItems='center' space={2}>
                                <Pressable>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="phone" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>语音通话</Text>
                            </VStack>

                            <VStack alignItems='center' justifyContent='flex-start' space={2}>
                                <Pressable>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="videocam" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>视频通话</Text>
                            </VStack>

                        </Flex>
                    </Flex>

                )}
            </VStack>
        </>
    )
}

const styles = StyleSheet.create({
    chatOpsIcon: {
        padding: 12,
        backgroundColor: 'white',
        borderRadius: 10
    }
})

export default ChatItemFooter