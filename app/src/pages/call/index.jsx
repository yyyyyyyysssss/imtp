import { Avatar, Box, Button, HStack, Pressable, Spinner, Text, VStack } from "native-base"
import { useCallback, useEffect, useRef, useState } from "react";
import { View, PermissionsAndroid, StyleSheet,NativeModules } from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { AudioMuteOutlined, AudioOutlined, HangUpOutlined, PhoneOutlined, VideoOffLined, VideoOnLined, VolumeMuteUpLined, VolumeUpLined } from "../../components/CustomIcon";
import { useNavigation } from '@react-navigation/native';
import { CallOperation, CallStatus, CallType, MessageType } from "../../enum";
import useTimer from "../../hooks/useTimer";
import { RTCView } from 'react-native-webrtc';
import { SafeAreaView } from "react-native-safe-area-context";
import { showToast } from "../../components/Utils";
import WebRTCWrapper from "../../rtc/WebRTCWrapper";
import { addMessage, callBegin, callEnd } from "../../redux/slices/chatSlice";
import IdGen from "../../utils/IdGen";

const BACK_CAMERA = 'environment'
const FRONT_CAMERA = 'user'

const { MessageModule } = NativeModules

const Call = ({ route }) => {

    const navigation = useNavigation()

    const dispatch = useDispatch()

    const { sessionId, callType, callOperation } = route.params;
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    //用户信息
    const userInfo = useSelector(state => state.auth.userInfo)

    const sessionRef = useRef()
    const userInfoRef = useRef()

    const webrtcRef = useRef(null)
    const remoteRef = useRef(null)
    const localRef = useRef(null)

    //计时器
    const { timer, toggleTimer } = useTimer()

    //管理哪个video为大屏展示
    const [isVideoALarge, setIsVideoALarge] = useState(true)
    //通话状态
    const [callStatus, setCallStatus] = useState(CallStatus.PENDING)
    const callStatusRef = useRef()
    //麦克风是否标识
    const [microphoneMuteFlag, setMicrophoneMuteFlag] = useState(false)
    //扬声器静音标识
    const [speakerMuteFlag, setSpeakerMuteFlag] = useState(false)
    //摄像头标识
    const [cameraOffFlag, setCameraOffFlag] = useState(false)
    //本地流
    const [localStream, setLocalStream] = useState(null);
    //远程流
    const [remoteStream, setRemoteStream] = useState(null);

    useEffect(() => {
        const onReceiveClose = () => {
            if (callOperation === CallOperation.INVITE) {
                sendCallMessage(CallOperation.ACCEPT)
            }
            stopCall()
        }
        webrtcRef.current = new WebRTCWrapper(callType, session, onReceiveClose)
        webrtcRef.current.ontrack = (event) => {
            console.log('ontrack')
            //本地流
            setLocalStream(webrtcRef.current.localStream)
            //远程流
            setRemoteStream(event.streams[0])
            if(callStatusRef.current !== CallStatus.PROGRESSING){
                //开始通话
                startCall()
            }
        }

        if (callOperation === CallOperation.INVITE) {
            webrtcRef.current.sendPreOffer()
        }
        dispatch(callBegin({callType: callType}))
        return () => {
            webrtcRef.current.destroy()
        }
    }, [])

    useEffect(() => {
        callStatusRef.current = callStatus
        sessionRef.current = session
        userInfoRef.current = userInfo
    }, [callStatus,session,userInfo])



    //开始通话
    const startCall = async () => {
        toggleTimer()
        setCallStatus(CallStatus.PROGRESSING)
    }

    //关闭通话
    const stopCall = () => {
        dispatch(callEnd())
        navigation.goBack()
    }

    const microphoneMute = () => {
        setMicrophoneMuteFlag((prev) => {
            let m;
            if (prev) {
                //开启声音
                webrtcRef.current.unmute()
                m = false
            } else {
                //关闭声音
                webrtcRef.current.mute()
                m = true
            }
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

    //挂断
    const hangUp = () => {
        if (callOperation === CallOperation.INVITE) {
            sendCallMessage(callOperation)
        }
        stopCall()
        webrtcRef.current.close()
    }

    //接听
    const accept = () => {
        setCallStatus(CallStatus.CONNECTING)
        webrtcRef.current.sendOffer()
    }

    //切换屏幕
    const switchScreen = () => {
        setIsVideoALarge(!isVideoALarge)
    }

    //切换摄像头
    const switchCamera = () => {
        webrtcRef.current.switchCamera()
    }

    //发送通话消息
    const sendCallMessage = (operation) => {
        const type = callType === CallType.VIDEO ? MessageType.VIDEO_CALL_MESSAGE : MessageType.VOICE_CALL_MESSAGE
        //消息
        let msg = messageBase(type, null)
        let callStatus = 'COMPLETED'
        if (operation === CallOperation.INVITE) {
            if (callStatusRef.current === CallStatus.PENDING || callStatusRef.current === CallStatus.CONNECTING) {
                callStatus = 'CANCELLED'
            }

        } else {
            if (callStatusRef.current === CallStatus.PENDING || callStatusRef.current === CallStatus.CONNECTING) {
                callStatus = 'REFUSED'
            }
        }
        if (msg) {
            msg = {
                ...msg,
                contentMetadata: {
                    callStatus: callStatus,
                    duration: timeToSeconds(timer),
                    durationDesc: formatTimeString(timer)
                }
            }
            //添加消息
            dispatch(addMessage({ sessionId: sessionId, message: msg }))
            MessageModule.sendMessage(JSON.stringify(msg))
        }
    }

    const messageBase = (type, content) => {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: type,
            content: content,
            sessionId: sessionRef.current.id,
            sender: userInfoRef.current.id,
            receiver: sessionRef.current.receiverUserId,
            deliveryMethod: sessionRef.current.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfoRef.current.nickname,
            avatar: userInfoRef.current.avatar
        }
    }

    const timeToSeconds = (time) => {
        const [hours, minutes, seconds] = time.split(':').map(Number)
        return hours * 3600 + minutes * 60 + seconds
    }

    const formatTimeString = (time) => {
        const [hours, minutes, seconds] = time.split(':').map(Number)
        if(hours === 0){
            return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
        } else {
            return time
        }
    }

    return (
        <VStack style={styles.callBox}>

            {
                callType === CallType.VOICE
                    ?
                    (
                        <>
                        </>
                    )
                    :
                    (
                        <View>
                            <Pressable onPress={switchScreen}
                                style={[isVideoALarge ? styles.videoLarge : styles.videoSmall,{zIndex: isVideoALarge ? 1 : 1000}]}
                            >
                                <SafeAreaView flex={1}>
                                    <RTCView
                                        ref={remoteRef}
                                        style={{ flex: 1 }}
                                        zOrder={isVideoALarge ? 0 : 1}
                                        streamURL={remoteStream?.toURL()}
                                    />
                                </SafeAreaView>
                            </Pressable>
                            <Pressable onPress={switchScreen}
                                style={[isVideoALarge ? styles.videoSmall : styles.videoLarge,{zIndex: isVideoALarge ? 1000 : 1}]}
                            >
                                <SafeAreaView flex={1}>
                                    <RTCView
                                        ref={localRef}
                                        style={{ flex: 1 }}
                                        zOrder={isVideoALarge ? 1 : 0}
                                        streamURL={localStream?.toURL()}
                                    />
                                </SafeAreaView>
                            </Pressable>
                        </View>
                    )
            }
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
                                onPress={switchCamera}
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
        width: '100%',
        height: '100%',
        backgroundColor: 'black'
    },
    fullView: {
        width: '100%',
        height: '100%',
    },
    callControl: {
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        zIndex: 999
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
    videoLarge: {
        width: '100%',
        height: '100%'
    },
    videoSmall: {
        position: 'absolute',
        top: 0,
        right: 0,
        width: '40%',
        height: 250,
    }
})

export default Call