import React, { useState, useEffect, useLayoutEffect, useRef } from 'react'
import './index.less'
import 'webrtc-adapter'
import { Flex } from 'antd'
import { useWebSocket } from '../../context';
import { DeliveryMethod, MessageType } from '../../enum';
import IdGen from '../../utils/IdGen';
import { useSelector } from 'react-redux';
import { useSearchParams } from 'react-router-dom';




const RTC = () => {

    const [params] = useSearchParams()

    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //当前会话
    const session = params.get('user') == 1 ? {
        id: 1,
        userId: 1,
        receiverUserId: 2,
        deliveryMethod: DeliveryMethod.SINGLE
    } : {
        id: 2,
        userId: 2,
        receiverUserId: 1,
        deliveryMethod: DeliveryMethod.SINGLE
    }

    const localRef = useRef()
    const remoteRef = useRef()

    const rtcRef = useRef()

    const { socket } = useWebSocket();
    const socketRef = useRef();

    useEffect(() => {
        socketRef.current = socket;
    }, [socket]);

    useEffect(() => {
        if (socket) {
            //信令消息
            const handleSignaling = async (event) => {
                const msg = JSON.parse(event.data);
                const { header } = msg;
                const { cmd } = header
                switch (cmd) {
                    // 接收远端发来的offer
                    case MessageType.SIGNALING_OFFER:
                        console.log('offer')
                        createAnswer(msg.content)
                        break
                    // 收到远端的 answer
                    case MessageType.SIGNALING_ANSWER:
                        console.log('answer')
                        await rtcRef.current.setRemoteDescription(new RTCSessionDescription({
                            type: 'answer',
                            sdp: msg.content
                        }))
                        break
                    // 收到远端的 ICE 候选信息
                    case MessageType.SIGNALING_CANDIDATE:
                        const candidate = new RTCIceCandidate(JSON.parse(msg.content))
                        rtcRef.current.addIceCandidate(candidate)
                        break
                }
            }

            socket.addEventListener('message', handleSignaling);
            return () => {
                socket.removeEventListener('message', handleSignaling);
            }
        }
    }, [socket])

    const createOffer = async () => {
        //创建本地提案
        const offer = await rtcRef.current.createOffer()
        await rtcRef.current.setLocalDescription(offer)
        //监听服务器返回的新的候选地址
        rtcRef.current.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = tempMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                socketRef.current.send(JSON.stringify(msg));
            }
        }
        const msg = tempMessage(MessageType.SIGNALING_OFFER, offer.sdp)
        socketRef.current.send(JSON.stringify(msg));
        return offer
    }

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
                const msg = tempMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                socketRef.current.send(JSON.stringify(msg));
            }
        }
        const msg = tempMessage(MessageType.SIGNALING_ANSWER, answer.sdp)
        socketRef.current.send(JSON.stringify(msg));
    }

    useEffect(() => {
        initRTC()
    },[])

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

        const localStream = await navigator.mediaDevices.getUserMedia({
            video: true,
            audio: true,
        })
        localRef.current.srcObject = localStream
        //本地媒体流轨道(视频轨道和音频轨道)
        const tracks = localStream.getTracks()
        for (const track of tracks) {
            //轨道添加到rtc中
            rtcRef.current.addTrack(track,localStream)
        }
        //监听远程轨道
        rtcRef.current.ontrack = (event) => {
            //设置远程视频流
            const ts = event.streams
            remoteRef.current.srcObject = event.streams[0]
        }
    }

    const tempMessage = (type, content) => {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: type,
            content: content,
            sessionId: session.id,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
            self: true,
            timestamp: new Date().getTime(),
            name: userInfo.nickname,
            avatar: userInfo.avatar
        }
    }


    const handleClick = () => {
        createOffer()
    }

    return (
        <Flex
            style={{

            }}
        >
            <video
                ref={localRef}
                style={{
                    width: 400,
                    height: 400
                }}
                autoPlay
                playsInline
                muted />

            <video
                ref={remoteRef}
                style={{
                    width: 400,
                    height: 400
                }}
                autoPlay
                playsInline
            />
            <Flex vertical>
                <button onClick={handleClick}>创建本地提案</button>
            </Flex>
        </Flex>
    )
}

export default RTC