import React, { useState, useEffect, useLayoutEffect, useRef } from 'react'
import './index.less'
import 'webrtc-adapter'
import { Flex } from 'antd'




const RTC = () => {

    const localRef = useRef()
    const remoteRef = useRef()

    const inputRef = useRef()

    const rtc = new RTCPeerConnection({
        iceServers: [
            {
                urls: 'turn:222.65.207.186:23478',
                username: 'ys',
                credential: 'Yan@136156'
            }
        ]
    })

    useEffect(() => {
        const getLocalStream = async () => {
            const localStream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true,
            })
            localRef.current.srcObject = localStream
            //本地媒体流轨道(视频轨道和音频轨道)
            const tracks = localStream.getTracks()
            for (const track of tracks) {
                //轨道添加到rtc中
                rtc.addTrack(track)
            }
            //监听远程轨道
            rtc.ontrack = (event) => {
                //设置远程视频流
                const ts = event.streams
                console.log('remote',ts)
                // remoteRef.current.srcObject = event.streams[0]
            }
            //创建本地提案
            const offer = await rtc.createOffer()
            console.log('offer',offer)
            //设置本地SDP
            await rtc.setLocalDescription(offer)
            //监听服务器返回的新的候选地址
            rtc.onicecandidate = async (e) => {
                if (e.candidate) {

                }
            }
        }
        //采集本地音视频流
        getLocalStream()
    }, [])

    const setSDP = async () => {
        const r = JSON.parse(inputRef.current.value)
        console.log(r)
        rtc.setRemoteDescription(new RTCSessionDescription(r))
        const answer = await rtc.createAnswer()
        console.log('answer',answer)
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
                <input ref={inputRef} />
                <button onClick={setSDP}>按钮</button>
            </Flex>
        </Flex>
    )
}

export default RTC