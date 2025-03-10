import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import './index.less'
import { CloseOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons'
import { HangUpOutlined } from '../../components/customIcon';
import { Avatar, Flex, message, Spin } from 'antd';
import { MessageType, CallOperation, CallType } from '../../enum';
import useTimer from '../../hooks/useTimer';
import Draggable from 'react-draggable';
import { useDispatch, useSelector } from 'react-redux';
import { addMessage, stopVoiceCall } from '../../redux/slices/chatSlice';
import IdGen from '../../utils/IdGen';
import { formatTimeString, timeToSeconds } from '../../utils';
import WebRTCConnection from '../../webrtc/WebRTCConnection';

// 通话状态 1 初始状态 2 连接中 3 通话中
const PENDING = 1
const CONNECTING = 2
const CALLING = 3

const VoiceCall = forwardRef(({ sendMessage }, ref) => {

    useImperativeHandle(ref, () => ({
        sendBusy: sendBusy,
        receiveSignalingOffer: receiveSignalingOffer,
        receiveSignalingAnswer: receiveSignalingAnswer,
        receiveSignalingCandidate: receiveSignalingCandidate,
        receiveSignalingBusy: receiveSignalingBusy,
        receiveSignalingClose: receiveSignalingClose
    }))

    const dispatch = useDispatch()

    const voiceCall = useSelector(state => state.chat.voiceCall)

    const nodeRef = useRef(null)

    //是否静音
    const [isMute, setIsMute] = useState(false)
    const [callStatus, setCallStatus] = useState(PENDING)

    const callStatusRef = useRef()
    useEffect(() => {
        callStatusRef.current = callStatus
    }, [callStatus])

    //计时器
    const { timer, toggleTimer } = useTimer()

    const { callOperation, sessionId } = voiceCall
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId]) || {}

    const { name, avatar } = session

    const localRef = useRef()
    const remoteRef = useRef()
    const webrtcRef = useRef()

    useEffect(() => {
        const invite = async () => {
            if (callOperation === CallOperation.INVITE) {
                //创建preOffer并发送给对方
                webrtcRef.current.sendPreOffer()
            }
        }
        webrtcRef.current = new WebRTCConnection(CallType.VOICE, session, sendMessage)
        webrtcRef.current.ontrack = (event) => {
            //设置远程音频流
            remoteRef.current.srcObject = event.streams[0]
            //开始通话
            startCall()
        }

        invite()

        //页面刷新前直接挂断
        const handleRefresh = (e) => {
            hangUpPhone()
        }
        window.addEventListener("beforeunload", handleRefresh)
        return () => {
            window.removeEventListener("beforeunload", handleRefresh)
            webrtcRef.current?.destroy()
        }
    }, [])

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
        stopCall()
        webrtcRef.current?.receiveBusy()
    }

    //接收close
    const receiveSignalingClose = async () => {
        if (callOperation === CallOperation.INVITE) {
            sendVoiceCallMessage(CallOperation.ACCEPT)
        }
        stopCall()
        webrtcRef.current?.receiveClose()
    }

    //开始通话
    const startCall = async () => {
        toggleTimer()
        setCallStatus(CALLING)
    }

    //关闭通话
    const stopCall = () => {
        dispatch(stopVoiceCall())
    }

    //挂断
    const hangUpPhone = () => {
        if (callOperation === CallOperation.INVITE) {
            sendVoiceCallMessage(callOperation)
        }
        stopCall()
        webrtcRef.current?.close()
    }

    //发送语音通话消息
    const sendVoiceCallMessage = (operation) => {
        //消息
        let msg = messageBase(MessageType.VOICE_CALL_MESSAGE, null)
        let callStatus = 'COMPLETED'
        if (operation === CallOperation.INVITE) {
            if (callStatusRef.current === PENDING || callStatusRef.current === CONNECTING) {
                callStatus = 'CANCELLED'
            }

        } else {
            if (callStatusRef.current === PENDING || callStatusRef.current === CONNECTING) {
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

    //静音
    const mute = async () => {
        if (isMute) {
            //开启声音
            webrtcRef.current.unmute()
        } else {
            //关闭声音
            webrtcRef.current.mute()
        }
        setIsMute((prev) => !prev)
    }

    //接受通话
    const accept = async () => {
        setCallStatus(CONNECTING)
        webrtcRef.current.sendOffer()
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
            <Flex flex={1} ref={nodeRef}
                style={{
                    width: 300,
                    position: 'fixed',
                    zIndex: 1000,
                    borderRadius: 10,
                    right: '20px',
                    bottom: '20px',
                    overflow: 'hidden'
                }}
                vertical>
                {/* header */}
                <Flex
                    className='drag-handle'
                    style={{
                        justifyContent: 'center',
                        backgroundColor: '#2B3745',
                        padding: 5
                    }}
                >
                    <Flex flex={1} style={{ justifyContent: 'center', cursor: 'move' }}>
                        <div style={{ color: '#A0A7B1', userSelect: 'none' }}>语音通话</div>
                    </Flex>
                    <Flex style={{ cursor: 'pointer' }} onClick={hangUpPhone}>
                        <CloseOutlined style={{ color: '#A0A7B1' }} />
                    </Flex>
                </Flex>
                {/* body */}
                <Flex gap={20} justify='center' style={{ backgroundColor: '#474F5B', padding: 20 }} vertical>
                    <Flex gap={10}>
                        <Avatar style={{ userSelect: 'none' }} size={50} shape="circle" src={avatar} />
                        <Flex gap={5} vertical>
                            <div style={{ color: 'white', userSelect: 'none' }}>
                                {name}
                            </div>
                            <div style={{ color: 'white', userSelect: 'none' }}>
                                {callStatus === CALLING ? timer : callOperation === CallOperation.INVITE ? '正在呼叫...' : '邀请你语音通话'}
                            </div>
                        </Flex>
                    </Flex>
                    <Flex justify='space-between' gap={20} style={{ paddingLeft: 10, paddingRight: 10, flexDirection: callStatus === CALLING ? 'row-reverse' : callOperation === CallOperation.ACCEPT ? '' : 'row-reverse' }}>
                        <div
                            style={{
                                padding: 10,
                                backgroundColor: 'red',
                                borderRadius: 100,
                                display: 'flex',
                            }}
                            onClick={hangUpPhone}
                        >
                            <HangUpOutlined size={25}/>
                        </div>
                        {
                            callStatus === CALLING || callOperation === CallOperation.INVITE
                                ?
                                (
                                    <div
                                        style={{
                                            padding: 10,
                                            backgroundColor: '#555D68',
                                            borderRadius: 100,
                                            display: 'flex'
                                        }}
                                        onClick={mute}
                                    >
                                        {isMute ? <AudioMutedOutlined style={{ fontSize: 25, color: 'white' }} /> : <AudioOutlined style={{ fontSize: 25, color: 'white' }} />}
                                    </div>

                                )
                                :
                                (

                                    <Spin spinning={callStatus === CONNECTING}>
                                        <div
                                            style={{
                                                padding: 10,
                                                backgroundColor: '#00B853',
                                                borderRadius: 100,
                                                display: 'flex',
                                            }}
                                            onClick={accept}
                                        >
                                            <PhoneOutlined style={{ fontSize: 25, color: 'white' }} />
                                        </div>
                                    </Spin>
                                )
                        }

                    </Flex>
                </Flex>
                <audio ref={localRef} autoPlay muted />
                <audio ref={remoteRef} autoPlay />
            </Flex>
        </Draggable>
    )
})

export default React.memo(VoiceCall)