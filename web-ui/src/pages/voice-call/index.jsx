import React, { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import './index.less'
import adapter from 'webrtc-adapter'
import { CloseOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons'
import { HangUpOutlined } from '../../components/customIcon';
import { Avatar, Flex, message, Spin } from 'antd';
import { MessageType, VoiceCallType } from '../../enum';
import useTimer from '../../hooks/useTimer';
import Draggable from 'react-draggable';
import { useDispatch, useSelector } from 'react-redux';
import { addMessage, startVoiceCall, stopVoiceCall } from '../../redux/slices/chatSlice';
import IdGen from '../../utils/IdGen';
import { formatTimeString, timeToSeconds } from '../../utils';

// 通话状态 1 初始状态 2 连接中 3 通话中
const PENDING = 1
const CONNECTING = 2
const CALLING = 3

const VoiceCall = forwardRef(({ sendMessage }, ref) => {

    useImperativeHandle(ref, () => ({
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
    const { timer, toggleTimer, resetTimer } = useTimer()

    const { visible, type, sessionId, offerSdp } = voiceCall
    const visibleRef = useRef()
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId]) || {}

    const { name, avatar } = session

    const rtcRef = useRef()
    const localStreamRef = useRef()
    const localRef = useRef()
    const remoteRef = useRef()

    useEffect(() => {
        visibleRef.current = visible
        const invite = async () => {
            if (type === VoiceCallType.INVITE) {
                await initRTC()
                //创建offer并发送给对方
                createOffer()
            }
        }
        //页面刷新前直接挂断
        const handleRefresh = (e) => {
            if (visibleRef.current) {
                hangUpPhone()
            }
        }
        if (visible) {
            invite()
            window.addEventListener("beforeunload", handleRefresh)
        } else {
            window.removeEventListener("beforeunload", handleRefresh)
        }

        return () => {
            destroy()
        }
    }, [visible])

    const initRTC = async () => {
        rtcRef.current = new RTCPeerConnection({
            iceServers: [
                {
                    urls: 'turn:222.65.207.186:23478',
                    username: 'ys',
                    credential: 'Yan@136156'
                }
            ]
        })
        //创建语音通道
        const voiceChannel = rtcRef.current.createDataChannel('voice_channel')
        //监听通道关闭事件
        voiceChannel.onclose = (event) => {

        }
        //本地音频流通道
        await addLocalVoiceStream()
        //监听远程轨道
        rtcRef.current.ontrack = (event) => {
            //设置远程音频流
            remoteRef.current.srcObject = event.streams[0]
            //开始通话
            startCall()
        }

        //监听对方关闭状态
        rtcRef.current.oniceconnectionstatechange = () => {

        }
    }

    //添加本地音频流
    const addLocalVoiceStream = async () => {
        const localStream = await getLocalVoiceStream()
        //设置本地音频流播放
        localRef.current.srcObject = localStream
        //本地音频流轨道添加到rtc中
        const tracks = localStream.getTracks()
        for (const track of tracks) {
            //轨道添加到rtc中
            rtcRef.current.addTrack(track, localStream)
        }
    }

    //获取本地音频流
    const getLocalVoiceStream = async () => {
        if (localStreamRef.current) {
            return localStreamRef.current
        }
        const localStream = await navigator.mediaDevices.getUserMedia({
            video: false,
            audio: true,
        })
        //本地音频流
        localStreamRef.current = localStream
        return localStream
    }

    //销毁
    const destroy = () => {
        localStreamRef.current?.getTracks().forEach(track => track.stop())
        rtcRef.current?.close()
        //引用设置为null
        rtcRef.current = null
        localStreamRef.current = null
    }

    //提案
    const createOffer = async () => {
        //创建本地提案
        const offer = await rtcRef.current.createOffer()
        //设置本地提案
        await rtcRef.current.setLocalDescription(offer)
        //发送提案
        const msg = signalingMessage(MessageType.SIGNALING_OFFER, offer.sdp)
        sendMessage(msg)
        return offer
    }

    //应答
    const createAnswer = async (offerSdp) => {
        await rtcRef.current.setRemoteDescription(new RTCSessionDescription({
            type: 'offer',
            sdp: offerSdp
        }))
        const answer = await rtcRef.current.createAnswer()
        await rtcRef.current.setLocalDescription(answer)
        //监听服务器返回的新的候选地址
        rtcRef.current.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = signalingMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                sendMessage(msg)
            }
        }
        //发送应答
        const msg = signalingMessage(MessageType.SIGNALING_ANSWER, answer.sdp)
        await sendMessage(msg)

    }

    //接收offer
    const receiveSignalingOffer = async (session, sdp) => {
        if (visible) {
            //发送忙线
            const msg = signalingMessage(MessageType.SIGNALING_BUSY, null)
            const newMsg = {
                ...msg,
                sessionId: session.id,
                receiver: session.receiverUserId,
                deliveryMethod: session.deliveryMethod,
            }
            await sendMessage(newMsg)
        } else {
            dispatch(startVoiceCall({
                sessionId: session.id,
                type: VoiceCallType.ACCEPT,
                offerSdp: sdp
            }))
        }

    }

    //接收answer
    const receiveSignalingAnswer = async (sdp) => {
        await rtcRef.current.setRemoteDescription(new RTCSessionDescription({
            type: 'answer',
            sdp: sdp
        }))
        //监听服务器返回的新的候选地址
        rtcRef.current.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = signalingMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                sendMessage(msg)
            }
        }
    }

    //接收candidate
    const receiveSignalingCandidate = (candidate) => {
        rtcRef.current?.addIceCandidate(new RTCIceCandidate(JSON.parse(candidate)))
    }

    //接收busy
    const receiveSignalingBusy = async () => {
        message.info('对方正在通话中，请稍后再试')
        stopCall()
    }

    //接收close
    const receiveSignalingClose = async () => {
        if(type === VoiceCallType.INVITE){
            sendVoiceCallMessage(VoiceCallType.ACCEPT)
        }
        stopCall()
    }

    //开始通话
    const startCall = async () => {
        toggleTimer()
        setCallStatus(CALLING)
    }

    //关闭通话
    const stopCall = () => {
        dispatch(stopVoiceCall())
        resetTimer()
        setCallStatus(PENDING)
        destroy()
    }

    const hangUpPhone = () => {
        if(type === VoiceCallType.INVITE){
            sendVoiceCallMessage(type)
        }
        stopCall()
        //信令消息
        const signalingMsg = signalingMessage(MessageType.SIGNALING_CLOSE, null)
        sendMessage(signalingMsg)
    }

    const sendVoiceCallMessage = (callType) => {
        //消息
        let msg = signalingMessage(MessageType.VOICE_CALL_MESSAGE, null)
        let callStatus = 'COMPLETED'
        if (callType === VoiceCallType.INVITE) {
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

    const mute = async () => {
        const stream = await getLocalVoiceStream()
        let audioTracks = stream.getAudioTracks()
        if (isMute) {
            //开启声音
            audioTracks.forEach(track => {
                track.enabled = true
            })
        } else {
            //关闭声音
            audioTracks.forEach(track => {
                track.enabled = false
            })
        }
        setIsMute((prev) => !prev)
    }

    const accept = async () => {
        setCallStatus(CONNECTING)
        await initRTC()
        //创建应答
        createAnswer(offerSdp)
    }


    const signalingMessage = (type, content) => {
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
        <>
            {visible && (
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
                                        {callStatus === CALLING ? timer : type === VoiceCallType.INVITE ? '正在呼叫...' : '邀请你语音通话'}
                                    </div>
                                </Flex>
                            </Flex>
                            <Flex justify='space-between' gap={20} style={{ paddingLeft: 10, paddingRight: 10, flexDirection: callStatus === CALLING ? 'row-reverse' : type === VoiceCallType.ACCEPT ? '' : 'row-reverse' }}>
                                <div
                                    style={{
                                        padding: 10,
                                        backgroundColor: 'red',
                                        borderRadius: 100,
                                        display: 'flex',
                                    }}
                                    onClick={hangUpPhone}
                                >
                                    <HangUpOutlined />
                                </div>
                                {
                                    callStatus === CALLING || type === VoiceCallType.INVITE
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
            }
        </>


    )
})

export default React.memo(VoiceCall)