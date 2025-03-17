import React, { useEffect, useRef, useState } from 'react';
import { Box, HStack, Text, VStack, Input, Pressable, Flex, View } from 'native-base';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import SimpleLineIcons from 'react-native-vector-icons/SimpleLineIcons';
import { PermissionsAndroid, StyleSheet,Platform } from 'react-native';
import { launchCamera, launchImageLibrary } from 'react-native-image-picker';
import { MessageType } from '../enum';
import DocumentPicker, { types } from 'react-native-document-picker'
import RecordVoice from './RecordVoice';
import { NativeModules } from 'react-native';
import { useNavigation, } from '@react-navigation/native';
import { check, request, PERMISSIONS, RESULTS } from 'react-native-permissions';
import { requestCameraPermission } from '../utils/PermissionRequest';

const { CallModule } = NativeModules

const ChatItemFooter = React.memo(({ sendMessage,sessionId }) => {

    const navigation = useNavigation();

    const [isOpen, setIsOpen] = useState(false)

    const [isVoice, setIsVoice] = useState(null)

    const [inputMemo, setInputMemo] = useState("")

    const [overlayVisible, setOverlayVisible] = useState(false)

    const inputRef = useRef()

    const handleSubmit = (event) => {
        const text = event.nativeEvent.text
        if (text) {
            inputRef.current.clear()
            messageProvider({ type: 'text', content: text })
        }
    }

    const messageProvider = (media) => {
        const { content, uri, type, fileName, fileSize, width, height, duration } = media
        let message = null
        if (type.startsWith('image')) {
            message = {
                type: MessageType.IMAGE_MESSAGE,
                fileName: fileName,
                filePath: uri,
                fileType: type,
                fileSize: fileSize,
                width: width,
                height: height
            }
        } else if (type.startsWith('video')) {
            message = {
                type: MessageType.VIDEO_MESSAGE,
                fileName: fileName,
                filePath: uri,
                fileType: type,
                fileSize: fileSize,
                duration: duration,
                width: width,
                height: height
            }
        } else if (type.startsWith('text')) {
            message = {
                type: MessageType.TEXT_MESSAGE,
                content: content,
            }
        } else if (type.startsWith('audio')) {
            message = {
                type: MessageType.VOICE_MESSAGE,
                fileName: fileName,
                filePath: uri,
                fileType: type,
                fileSize: fileSize,
                duration: duration,
            }
        }else {
            message = {
                type: MessageType.FILE_MESSAGE,
                fileName: fileName,
                fileSize: fileSize,
                filePath: uri,
                fileType: type
            }
        }
        sendMessage(message)
    }

    const handleInputFocus = () => {
        setIsOpen(false)
    }

    const selectEmoji = () => {

    }

    useEffect(() => {
        if (isVoice === null) {
            return
        }
        if (isVoice) {
            inputRef.current.blur()
        } else {
            inputRef.current.focus()
        }
    }, [isVoice])

    const handleOnPressVoice = () => {
        if (isVoice) {
            setIsVoice(false)
        } else {
            setIsVoice(true)
        }

    }

    const checkAndRequestRecorderPermission = async () => {
        if (Platform.OS !== 'android') {
            return Promise.resolve(true);
        }
        let result;
        try {
            result = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.RECORD_AUDIO, { title: 'Microphone Permission', message: 'Enter the Gunbook needs access to your microphone so you can search with voice.' });
        } catch (error) {
            console.error('failed getting permission, result:', result);
        }
        return (result === true || result === PermissionsAndroid.RESULTS.GRANTED);
    }

    const handleViocePressIn = async () => {
        const permissionChecked = await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.RECORD_AUDIO)
        if(permissionChecked){
            setOverlayVisible(true)
        }else {
            await checkAndRequestRecorderPermission()
        }
        
    }

    const handleViocePressOut = () => {
        setOverlayVisible(false)
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
    const takePicture = async () => {
        const p = await requestCameraPermission()
        if(!p){
            return
        }
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
    const cameraShoot = async () => {
        const p = await requestCameraPermission()
        if(!p){
            return
        }
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

    const voiceCall = () => {
        CallModule.call('VOICE')
    }

    const videoCall = () => {
        navigation.navigate('Call', {
            sessionId: sessionId
        })
    }

    //文件选择
    const filePicker = () => {
        DocumentPicker.pick({
            mode: 'open',
            type: types.allFiles
        })
            .then(
                (res) => {
                    res.forEach(file => {
                        const { name, uri, type, size } = file
                        const media = {
                            uri: uri,
                            type: type,
                            fileName: name,
                            fileSize: size
                        }
                        messageProvider(media)
                    })

                }
            )
            .catch(err => {
                console.log('error', err)
            })
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
                        <Pressable
                            onPress={() => handleOnPressVoice()}
                        >
                            <MaterialIcon name={isVoice === true ? 'keyboard' : 'keyboard-voice'} size={30} />
                        </Pressable>
                    </HStack>
                    <HStack flex={5.5} justifyContent='center' alignItems='center'>
                        {isVoice === true ?
                            (
                                <Pressable
                                    flex={1}
                                    onPressIn={handleViocePressIn}
                                    onTouchEnd={handleViocePressOut}
                                >
                                    <Input
                                        ref={inputRef}
                                        isReadOnly
                                        style={styles.holdToSpeak}
                                        placeholder='按住 说话'
                                        placeholderTextColor='black'
                                        size='md'
                                        focusOutlineColor='none'
                                        borderWidth={0}
                                        backgroundColor='white'
                                        w={{
                                            base: "100%",
                                        }}
                                    />
                                </Pressable>
                            ) : (
                                <Input
                                    ref={inputRef}
                                    defaultValue={isVoice === false ? inputMemo : ''}
                                    size='md'
                                    focusOutlineColor='none'
                                    borderWidth={0}
                                    backgroundColor='white'
                                    blurOnSubmit={false}
                                    onSubmitEditing={handleSubmit}
                                    onFocus={handleInputFocus}
                                    onChangeText={(text) => setInputMemo(text)}
                                    w={{
                                        base: "100%",
                                    }}
                                />
                            )
                        }

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
                                <Pressable onPress={voiceCall}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="phone" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>语音通话</Text>
                            </VStack>

                            <VStack alignItems='center' justifyContent='flex-start' space={2}>
                                <Pressable onPress={videoCall}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="videocam" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>视频通话</Text>
                            </VStack>

                            <VStack alignItems='center' justifyContent='flex-start' space={2}>
                                <Pressable onPress={filePicker}>
                                    <Box style={styles.chatOpsIcon}>
                                        <MaterialIcon name="folder-open" size={40} />
                                    </Box>
                                </Pressable>
                                <Text>文件</Text>
                            </VStack>

                        </Flex>
                    </Flex>

                )}
            </VStack>
            <RecordVoice
                overlayVisible={overlayVisible}
                setOverlayVisible={setOverlayVisible}
                messageProvider={messageProvider}
            />
        </>
    )
})

const styles = StyleSheet.create({
    chatOpsIcon: {
        padding: 12,
        backgroundColor: 'white',
        borderRadius: 10
    },
    holdToSpeak: {
        fontSize: 20,
        textAlign: 'center',
        fontWeight: 'bold'
    }
})

export default ChatItemFooter