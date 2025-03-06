import { CallType, MessageType } from "../enum"
import IdGen from "../utils/IdGen"
import adapter from 'webrtc-adapter'


class WebRTCConnection {
    constructor(callType, session, sendMessage) {
        this.callType = callType
        this.session = session
        this.sendMessage = sendMessage
    }

    async init() {
        this.rtc = new RTCPeerConnection({
            iceServers: [
                {
                    urls: 'turn:222.65.207.186:23478',
                    username: 'ys',
                    credential: 'Yan@136156'
                }
            ]
        })
        //创建语音通道
        const voiceChannel = this.rtc.createDataChannel('voice_channel')
        //监听通道关闭事件
        voiceChannel.onclose = (event) => {

        }
        //本地音频流通道添加到rtc中
        this.localStream = await this.getUserMedia()
        const tracks = this.localStream.getTracks()
        for (const track of tracks) {
            //轨道添加到rtc中
            this.rtc.addTrack(track, this.localStream)
        }
        //监听远程轨道
        this.rtc.ontrack = (event) => {
            this.ontrack(event)
        }
    }

    async getUserMedia() {
        if (this.localStream) {
            return this.localStream
        }
        return await navigator.mediaDevices.getUserMedia({
            video: this.callType === CallType.VIDEO ? true : false,
            audio: true,
        })
    }

    async createOffer() {
        const offer = await this.rtc.createOffer()
        //设置本地提案
        await this.rtc.setLocalDescription(offer)
        //发送提案
        const msg = this.#signalingMessage(MessageType.SIGNALING_OFFER, offer.sdp)
        this.sendMessage(msg)
        return offer
    }

    async createAnswer(offerSdp) {
        await this.rtc.setRemoteDescription(new RTCSessionDescription({
            type: 'offer',
            sdp: offerSdp
        }))
        const answer = await this.rtc.createAnswer()
        await this.rtc.setLocalDescription(answer)
        //监听服务器返回的新的候选地址
        this.rtc.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = this.#signalingMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                this.sendMessage(msg)
            }
        }
        //发送应答
        const msg = this.#signalingMessage(MessageType.SIGNALING_ANSWER, answer.sdp)
        this.sendMessage(msg)
    }

    //接收answer
    async receiveAnswer(sdp) {
        await this.rtc.setRemoteDescription(new RTCSessionDescription({
            type: 'answer',
            sdp: sdp
        }))
        //监听服务器返回的新的候选地址
        this.rtc.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = this.#signalingMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                this.sendMessage(msg)
            }
        }
    }

    //接收candidate
    async receiveCandidate(candidate) {
        this.rtc.addIceCandidate(new RTCIceCandidate(JSON.parse(candidate)))
    }

    //忙线
    async busy(session) {
        const msg = this.#signalingMessage(MessageType.SIGNALING_BUSY, null)
        const newMsg = {
            ...msg,
            essionId: session.id,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
        }
        this.sendMessage(newMsg)
    }

    async receiveBusy() {
        this.destroy()
    }

    async receiveClose() {
        this.destroy()
    }

    async mute() {
        const audioTracks = this.localStream.getAudioTracks()
        audioTracks.forEach(track => {
            track.enabled = false
        })
    }

    async unmute() {
        const audioTracks = this.localStream.getAudioTracks()
        audioTracks.forEach(track => {
            track.enabled = true
        })
    }

    async close() {
        const msg = this.#signalingMessage(MessageType.SIGNALING_CLOSE, null)
        this.sendMessage(msg)
        this.destroy()
    }

    async destroy() {
        this.localStream?.getTracks().forEach(track => track.stop())
        this.rtc?.close()
    }

    #signalingMessage(signalingMessageType, content) {
        const id = IdGen.nextId()
        return {
            id: id,
            ackId: id,
            type: signalingMessageType,
            content: content,
            sessionId: this.session.id,
            sender: this.session.userId,
            receiver: this.session.receiverUserId,
            deliveryMethod: this.session.deliveryMethod,
        }
    }
}

export default WebRTCConnection