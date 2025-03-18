import { Avatar, Box, Button, HStack, Pressable, Spinner, Text, VStack } from "native-base"
import { useCallback, useRef, useState } from "react";
import { UIManager, findNodeHandle, View, requireNativeComponent, PermissionsAndroid, StyleSheet } from 'react-native';
import { requestCameraPermission } from "../../utils/PermissionRequest";
import { useDispatch, useSelector } from 'react-redux';
import { AudioMuteOutlined, AudioOutlined, HangUpOutlined, PhoneOutlined, VideoOffLined, VideoOnLined, VolumeMuteUpLined, VolumeUpLined } from "../../components/CustomIcon";
import { useNavigation } from '@react-navigation/native';
import { CallOperation, CallStatus, CallType } from "../../enum";
import useTimer from "../../hooks/useTimer";

// const CallView = requireNativeComponent('CallView');


const FRONT_CAMERA = '0'
const BACK_CAMERA = '1'

const Call = ({ route }) => {

    const navigation = useNavigation();

    const { sessionId, callType, callOperation } = route.params;

    const session = useSelector(state => state.chat.entities.sessions[sessionId])

    const callRef = useRef(null)

    const [lensFacing, setLensFacing] = useState(BACK_CAMERA)

    //计时器
    const { timer, toggleTimer } = useTimer()

    //通话状态
    const [callStatus, setCallStatus] = useState(CallStatus.PENDING)
    //麦克风是否标识
    const [microphoneMuteFlag, setMicrophoneMuteFlag] = useState(false)
    //扬声器静音标识
    const [speakerMuteFlag, setSpeakerMuteFlag] = useState(false)
    //摄像头标识
    const [cameraOffFlag, setCameraOffFlag] = useState(false)

    const startCamere = async () => {
        const permissionChecked = await requestCameraPermission()
        if (permissionChecked) {
            startCameraCommand(lensFacing)
        }
    }

    const switchCamera = () => {
        setLensFacing((prev) => {
            const newLensFacing = prev === FRONT_CAMERA ? BACK_CAMERA : FRONT_CAMERA;
            switchCameraCommand(newLensFacing)
            return newLensFacing
        })
    }

    const startCameraCommand = (lf) => {
        const viewId = findNodeHandle(callRef.current)
        UIManager.dispatchViewManagerCommand(
            viewId,
            'START_CAMERA',
            [lf]
        )
    }

    const switchCameraCommand = (lf) => {
        const viewId = findNodeHandle(callRef.current)
        UIManager.dispatchViewManagerCommand(
            viewId,
            'SWITCH_CAMERA',
            [lf]
        )
    }

    const microphoneMute = () => {
        setMicrophoneMuteFlag((prev) => {
            const m = prev ? false : true
            return m
        })
    }

    const speakerMute = () => {
        setSpeakerMuteFlag((prev) => {
            const m = prev ? false : true
            return m
        })
    }

    const cameraOff = () => {
        setCameraOffFlag((prev) => {
            const m = prev ? false : true
            return m
        })
    }

    const hangUp = () => {
        navigation.goBack()
    }

    const accept = () => {
        setCallStatus(CallStatus.CONNECTING)
        setTimeout(() => {
            setCallStatus(CallStatus.PROGRESSING)
            toggleTimer()
        }, 3000);
    }

    return (
        <VStack>
            <Box
                width='100%'
                height='100%'
                style={styles.callBox}
            >
                {/* <CallView ref={callRef} style={styles.callView} /> */}
            </Box>
            <VStack
                style={styles.callControl}

            >
                <HStack
                    style={{
                        padding: 8
                    }}
                    justifyContent='space-between'
                >
                    <HStack
                        flex={1}
                    >
                        <View />
                    </HStack>
                    <HStack
                        flex={1}
                        justifyContent='center'
                        style={{
                            opacity: callStatus === CallStatus.PROGRESSING ? 1 : 0
                        }}
                    >
                        <Text style={{ color: 'white', fontSize: 16 }}>{timer}</Text>
                    </HStack>
                    <HStack
                        flex={1}
                    >
                        <View />
                    </HStack>
                </HStack>
                <VStack
                    flex={5}
                    justifyContent='flex-start'
                    alignItems='center'
                    space={5}
                    style={{
                        marginTop: 80
                    }}
                >
                    <Avatar
                        size='80px'
                        _image={{

                        }}
                        source={{ uri: session.avatar }}
                    />
                    <Text style={{
                        color: 'white',
                        fontSize: 18,
                    }}
                    >
                        {session.name}
                    </Text>
                    <Text style={{
                        color: 'white',
                        fontSize: 14,
                    }}
                    >
                        {callStatus === CallStatus.PROGRESSING ? '' : callOperation === CallOperation.INVITE ? '等待对方接受邀请' : callType === CallType.VOICE ? '邀请你进行语音通话' : '邀请你进行视频通话'}
                    </Text>
                </VStack>
                <VStack flex={2} space={10} style={{ marginBottom: 50 }}>
                    <HStack
                        justifyContent='space-between'
                        alignItems='center'
                        style={{
                            paddingLeft: 40,
                            paddingRight: 40,
                        }}
                    >

                        <Pressable
                            onPress={microphoneMute}
                        >
                            <Box
                                style={styles.controlIconWhite}
                            >
                                {microphoneMuteFlag ? <AudioMuteOutlined size={35} color='black' /> : <AudioOutlined size={35} color='black' />}
                            </Box>
                        </Pressable>

                        {callType === CallType.VOICE && (
                            <Pressable
                                onPress={hangUp}
                            >
                                <Box
                                    style={styles.controlIconRed}
                                >
                                    <HangUpOutlined size={35} />
                                </Box>
                            </Pressable>
                        )}

                        <Pressable
                            onPress={speakerMute}
                        >
                            <Box
                                style={styles.controlIconWhite}
                            >
                                {speakerMuteFlag ? <VolumeMuteUpLined size={35} color='black' /> : <VolumeUpLined size={35} color='black' />}
                            </Box>
                        </Pressable>

                        {callType === CallType.VIDEO && (
                            <Pressable
                                onPress={cameraOff}
                            >
                                <Box
                                    style={styles.controlIconWhite}
                                >
                                    {cameraOffFlag ? <VideoOffLined size={35} /> : <VideoOnLined size={35} />}
                                </Box>
                            </Pressable>
                        )}
                    </HStack>
                    <HStack
                        justifyContent={callStatus === CallStatus.PROGRESSING ? 'center' : callOperation === CallOperation.INVITE ? 'center' : callType === CallType.VIDEO ? 'space-between' : 'center'}
                        alignItems='center'
                        style={{
                            paddingLeft: 40,
                            paddingRight: 40
                        }}
                    >
                        {callType === CallType.VIDEO && (

                            <Pressable
                                onPress={hangUp}
                            >
                                <Box
                                    style={styles.controlIconRed}
                                >
                                    <HangUpOutlined size={35} />
                                </Box>
                            </Pressable>
                        )}
                        {
                            callStatus === CallStatus.PROGRESSING
                                ?
                                (
                                    <></>
                                )
                                :
                                callOperation === CallOperation.ACCEPT
                                    ?
                                    (
                                        <View>
                                            {
                                                callStatus === CallStatus.CONNECTING
                                                    ?
                                                    (
                                                        <Spinner size={60} color='white' />

                                                    )
                                                    :
                                                    (
                                                        <Pressable
                                                            onPress={accept}
                                                        >
                                                            <Box
                                                                style={styles.controlIconGreen}
                                                            >
                                                                <PhoneOutlined size={35} />
                                                            </Box>
                                                        </Pressable>
                                                    )
                                            }
                                        </View>


                                    )
                                    :
                                    (
                                        <></>
                                    )
                        }
                    </HStack>
                </VStack>

            </VStack>

        </VStack>
    )
}


const styles = StyleSheet.create({
    callBox: {
        position: 'relative',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'black'
    },
    callView: {
        width: '100%',
        height: '100%'
    },
    callControl: {
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
    },
    controlIconWhite: {
        backgroundColor: 'white',
        borderRadius: 100,
        padding: 20
    },
    controlIconRed: {
        backgroundColor: 'red',
        borderRadius: 100,
        padding: 20
    },
    controlIconGreen: {
        backgroundColor: '#00B853',
        borderRadius: 100,
        padding: 20
    },
})

export default Call