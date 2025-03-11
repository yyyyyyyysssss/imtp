import React, { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import './index.less'
import { CloseOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons'
import Draggable from 'react-draggable';
import { Avatar, Flex, message, Spin } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { addMessage, stopVoiceCall } from '../../redux/slices/chatSlice';
import { HangUpOutlined, VideoOffLined, VideoOnLined, VolumeMutedLined, VolumeUpLined } from '../../components/customIcon';
import { CallOperation, CallStatus, CallType, MessageType } from '../../enum';
import useTimer from '../../hooks/useTimer';
import WebRTCConnection from '../../webrtc/WebRTCConnection';
import { formatTimeString, timeToSeconds } from '../../utils';
import IdGen from '../../utils/IdGen';




const Call = forwardRef(({ sendMessage }, ref) => {

    useImperativeHandle(ref, () => ({
        sendBusy: sendBusy,
        receiveSignalingOffer: receiveSignalingOffer,
        receiveSignalingAnswer: receiveSignalingAnswer,
        receiveSignalingCandidate: receiveSignalingCandidate,
        receiveSignalingBusy: receiveSignalingBusy,
        receiveSignalingClose: receiveSignalingClose
    }))

    const nodeRef = useRef(null)

    const voiceCall = useSelector(state => state.chat.voiceCall)

    const { callType, callOperation, sessionId } = voiceCall
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId]) || {}

    const { name, avatar } = session

    const dispatch = useDispatch()

    //计时器
    const { timer, toggleTimer } = useTimer()

    const [isControlShow, setIsControlShow] = useState(true)

    //当前通话状态
    const [callStatus, setCallStatus] = useState(CallStatus.PENDING)
    //管理哪个video为大屏展示
    const [isVideoALarge, setIsVideoALarge] = useState(true)
    //麦克风是否标识
    const [microphoneMuteFlag, setMicrophoneMuteFlag] = useState(false)
    //扬声器静音标识
    const [speakerMuteFlag, setSpeakerMuteFlag] = useState(false)
    //摄像头标识
    const [cameraOffFlag, setCameraOffFlag] = useState(false)

    const remoteRef = useRef(null)
    const localRef = useRef(null)
    const webrtcRef = useRef(null)
    const callStatusRef = useRef(null)

    useEffect(() => {
        webrtcRef.current = new WebRTCConnection(callType, session, sendMessage)
        webrtcRef.current.ontrack = (event) => {
            //本地流
            localRef.current.srcObject = webrtcRef.current.localStream
            //远程流
            remoteRef.current.srcObject = event.streams[0]
            //开始通话
            startCall()
        }
        if (callOperation === CallOperation.INVITE) {
            //创建preOffer并发送给对方
            webrtcRef.current.sendPreOffer()
        }
        //页面刷新前直接挂断
        const handleRefresh = (e) => {
            hangUp()
        }
        window.addEventListener("beforeunload", handleRefresh)
        return () => {
            window.removeEventListener("beforeunload", handleRefresh)
            webrtcRef.current?.destroy()
        }
    }, [])


    useEffect(() => {
        callStatusRef.current = callStatus
    }, [callStatus])

    //接收到offer
    const receiveSignalingOffer = async (offerSdp) => {
        webrtcRef.current.receiveOffer(offerSdp)
    }

    //接收answer
    const receiveSignalingAnswer = async (answerSdp) => {
        webrtcRef.current.receiveAnswer(answerSdp)
    }

    //接收candidate
    const receiveSignalingCandidate = (candidate) => {
        webrtcRef.current?.receiveCandidate(candidate)
    }

    //忙线
    const sendBusy = async (session) => {
        webrtcRef.current?.sendBusy(session)
    }

    //接收busy
    const receiveSignalingBusy = async () => {
        message.info('对方正在通话中，请稍后再试')
        webrtcRef.current?.receiveBusy()
        stopCall()
    }

    //接收close
    const receiveSignalingClose = async () => {
        if (callOperation === CallOperation.INVITE) {
            sendCallMessage(CallOperation.ACCEPT)
        }
        webrtcRef.current?.receiveClose()
        stopCall()
    }

    //开始通话
    const startCall = async () => {
        toggleTimer()
        setCallStatus(CallStatus.PROGRESSING)
    }

    //关闭通话
    const stopCall = () => {
        dispatch(stopVoiceCall())
    }

    //接受
    const accept = () => {
        setCallStatus(CallStatus.CONNECTING)
        webrtcRef.current.sendOffer()
    }

    //挂断
    const hangUp = () => {
        if (callOperation === CallOperation.INVITE) {
            sendCallMessage(callOperation)
        }
        stopCall()
        webrtcRef.current?.close()
    }

    const switchScreen = () => {
        setIsVideoALarge(!isVideoALarge)
    }

    const microphoneMute = useCallback(() => {
        if (microphoneMuteFlag) {
            //开启声音
            webrtcRef.current.unmute()
        } else {
            //关闭声音
            webrtcRef.current.mute()
        }
        setMicrophoneMuteFlag((prev) => !prev)
    },[microphoneMuteFlag])

    const speakerMute = async () => {
        setSpeakerMuteFlag((prev) => !prev)
    }

    const cameraOff = async () => {
        setCameraOffFlag((prev) => !prev)
    }

    const controlBtnShow = () => {
        setIsControlShow(true)
    }

    const controlBtnNotShow = useCallback(() => {
        if (callStatus === CallStatus.PROGRESSING) {
            setIsControlShow(false)
        }
    }, [callStatus])

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
            sendMessage(msg)
        }
    }

    const messageBase = (type, content) => {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: type,
            content: content,
            sessionId: session.id,
            sender: userInfo.id,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfo.nickname,
            avatar: userInfo.avatar
        }
    }

    return (
        <Draggable nodeRef={nodeRef} offsetParent={document.body} handle='.drag-handle'>
            <Flex
                flex={1}
                ref={nodeRef}
                style={{
                    backgroundColor: callType === CallType.VOICE ? '#494746' : 'black',
                    width: 350,
                    height: 600,
                    position: 'fixed',
                    zIndex: 1000,
                    borderRadius: 10,
                    top: '20%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    overflow: 'hidden',
                    boxShadow: 'box-shadow: 0px 0px 2px 2px lightgray'
                }}
                vertical
            >
                {
                    callType === CallType.VOICE
                        ?
                        (
                            <>
                                <audio ref={remoteRef} autoPlay />
                                <audio ref={localRef} autoPlay muted />
                            </>
                        )
                        :
                        (
                            <>
                                <video
                                    ref={remoteRef}
                                    className={`video ${isVideoALarge ? 'large' : 'small'}`}
                                    onClick={switchScreen}
                                    style={{
                                        zIndex: isVideoALarge ? 1 : 1000
                                    }}
                                />
                                <video
                                    ref={localRef}
                                    className={`video ${isVideoALarge ? 'small' : 'large'}`}
                                    onClick={switchScreen}
                                    style={{
                                        zIndex: isVideoALarge ? 1000 : 1
                                    }}
                                />
                            </>
                        )
                }
                <Flex
                    style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        opacity: callType === CallType.VIDEO ? isControlShow ? 1 : 0 : 1,
                        transition: 'opacity 0.3s ease',
                        transitionDelay: isControlShow ? '0s' : '2s',
                        zIndex: 999
                    }}
                    onMouseEnter={controlBtnShow}
                    onMouseLeave={controlBtnNotShow}
                    vertical
                >
                    <Flex
                        className='drag-handle'
                        style={{
                            padding: 8
                        }}
                        justify='space-between'
                    >
                        <Flex flex={1} justify='flex-start' style={{ cursor: 'pointer' }}>
                            <div />
                        </Flex>
                        <Flex flex={1} justify='center' style={{
                            cursor: 'pointer',
                            visibility: callStatus === CallStatus.PROGRESSING ? 'visible' : 'hidden'
                        }}>
                            <div style={{ color: 'white', userSelect: 'none' }}>
                                {timer}
                            </div>
                        </Flex>
                        <Flex flex={1} justify='flex-end' style={{ cursor: 'pointer' }} onClick={hangUp}>
                            <CloseOutlined style={{ color: 'white' }} />
                        </Flex>
                    </Flex>
                    <Flex
                        flex={5}
                        gap={5}
                        justify='flex-start'
                        align='center'
                        style={{
                            marginTop: 50
                        }}
                        vertical
                    >
                        <Avatar style={{ userSelect: 'none' }} shape='square' size={60} src={avatar} />
                        <div
                            style={{
                                color: 'white',
                                fontSize: 18,
                                userSelect: 'none'
                            }}
                        >
                            {name}
                        </div>
                        <div
                            style={{
                                color: 'white',
                                fontSize: 14,
                                userSelect: 'none'
                            }}
                        >
                            {callStatus === CallStatus.PROGRESSING ? '' : callOperation === CallOperation.INVITE ? '等待对方接受邀请' : callType === CallType.VOICE ? '邀请你进行语音通话' : '邀请你进行视频通话'}
                        </div>
                    </Flex>
                    <Flex
                        flex={2}
                        justify='space-between'
                        align={callStatus === CallStatus.PROGRESSING ? 'center' : callOperation === CallOperation.ACCEPT || callType === CallType.VIDEO ? 'flex-end' : 'center'}
                        style={{
                            paddingLeft: 40,
                            paddingRight: 40
                        }}
                    >
                        <div
                            className='control-btn-icon-white'
                            onClick={microphoneMute}
                        >
                            {microphoneMuteFlag ? <AudioMutedOutlined style={{ fontSize: 30, color: 'black' }} /> : <AudioOutlined style={{ fontSize: 30, color: 'black' }} />}
                        </div>
                        {callType === CallType.VOICE && (
                            <div
                                className='control-btn-icon-red'
                                onClick={hangUp}
                            >
                                <HangUpOutlined size={30} />
                            </div>
                        )}
                        <div
                            className='control-btn-icon-white'
                            onClick={speakerMute}
                        >
                            {speakerMuteFlag ? <VolumeMutedLined size={30} /> : <VolumeUpLined size={30} />}
                        </div>
                        {callType === CallType.VIDEO && (
                            <div
                                className='control-btn-icon-white'
                                onClick={cameraOff}
                            >
                                {cameraOffFlag ? <VideoOffLined size={30} /> : <VideoOnLined size={30} />}
                            </div>
                        )}
                    </Flex>
                    <Flex
                        flex={2}
                        justify={callOperation === CallOperation.INVITE ? 'center' : callType === CallType.VIDEO ? 'space-between' : 'center'}
                        align='center'
                        style={{
                            paddingLeft: 40,
                            paddingRight: 40
                        }}
                    >
                        {callType === CallType.VIDEO && (

                            <div
                                className='control-btn-icon-red'
                                onClick={hangUp}
                            >
                                <HangUpOutlined size={30} />
                            </div>
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
                                        <Spin spinning={callStatus === CallStatus.CONNECTING}>
                                            <div
                                                className='control-btn-icon-green'
                                                onClick={accept}
                                            >
                                                <PhoneOutlined style={{ fontSize: 30, color: 'white' }} />
                                            </div>
                                        </Spin>
                                    )
                                    :
                                    (
                                        <></>
                                    )
                        }
                    </Flex>
                </Flex>
            </Flex>
        </Draggable>
    )

})

export default React.memo(Call)