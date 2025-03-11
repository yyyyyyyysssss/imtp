import { CallType, MessageType } from "../enum"
import IdGen from "../utils/IdGen"
import adapter from 'webrtc-adapter'


class WebRTCConnection {
    constructor(callType, session, sendMessage) {
        this.callType = callType
        this.session = session
        this.sendMessage = sendMessage
        this.isRemoteDescriptionSet = false
        this.inited = false
        this.voiceEnabled = true
        this.cachedCandidates = []
    }

    async #init() {
        this.rtc = new RTCPeerConnection({
            iceServers: [
                {
                    urls: 'turn:116.237.179.131:23478',
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
        const tracks = await this.localStream.getTracks()
        for (const track of tracks) {
            if(track.kind === 'audio'){
                track.enabled = this.voiceEnabled
            }
            //轨道添加到rtc中
            this.rtc.addTrack(track, this.localStream)
        }
        //监听远程轨道
        this.rtc.ontrack = (event) => {
            this.ontrack(event)
        }
        this.inited = true
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

    async sendPreOffer(){
        //发送preOffer
        const msg = this.#signalingMessage(MessageType.SIGNALING_PRE_OFFER,this.callType)
        this.sendMessage(msg)
    }

    //发送offer
    async sendOffer() {
        await this.#init()
        //创建offer
        const offer = await this.rtc.createOffer()
        await this.#setLocalDescription(offer)
        //发送offer
        const msg = this.#signalingMessage(MessageType.SIGNALING_OFFER, offer.sdp)
        this.sendMessage(msg)
        return offer
    }

    //接收到offer
    async receiveOffer(offerSdp) {
        await this.#init()
        await this.#setRemoteDescription('offer', offerSdp)
        //创建answer
        const answer = await this.rtc.createAnswer()
        await this.#setLocalDescription(answer)
        //发送answer
        const msg = this.#signalingMessage(MessageType.SIGNALING_ANSWER, answer.sdp)
        this.sendMessage(msg)
    }

    //接收到answer
    async receiveAnswer(sdp) {
        await this.#setRemoteDescription('answer', sdp)
    }

    //接收candidate
    async receiveCandidate(candidate) {
        this.#addIceCandidate(candidate)
    }

    //设置本地description
    async #setLocalDescription(description) {
        await this.rtc.setLocalDescription(description)
        //监听服务器返回的新的候选地址
        this.rtc.onicecandidate = async (e) => {
            if (e.candidate) {
                const msg = this.#signalingMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
                this.sendMessage(msg)
            }
        }
    }

    //设置远端description
    async #setRemoteDescription(type, sdp) {
        await this.rtc.setRemoteDescription(new RTCSessionDescription({
            type: type,
            sdp: sdp
        }))
        this.isRemoteDescriptionSet = true
        this.cachedCandidates.forEach(c => {
            this.#addIceCandidate(c)
        })
    }

    //添加candidate 远端描述未设置时先把candidate缓存起来
    async #addIceCandidate(candidate) {
        if (this.isRemoteDescriptionSet) {
            this.rtc.addIceCandidate(new RTCIceCandidate(JSON.parse(candidate)))
        } else {
            this.cachedCandidates.push(candidate)
        }
    }

    //忙线
    async sendBusy(session) {
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

    //接收忙线
    async receiveBusy() {
        this.destroy()
    }

    //接收关闭
    async receiveClose() {
        this.destroy()
    }

    //静音
    async mute() {
        if(this.inited){
            this.#muted(false)
        }else {
            this.voiceEnabled = false
        }
        
    }

    //关闭静音
    async unmute() {
        if(this.inited){
            this.#muted(true)
        } else {
            this.voiceEnabled = true
        }
    }

    async #muted(flag){
        const audioTracks = this.localStream.getAudioTracks()
        audioTracks.forEach(track => {
            track.enabled = flag
        })
    }

    //关闭rtc
    async close() {
        const msg = this.#signalingMessage(MessageType.SIGNALING_CLOSE, null)
        this.sendMessage(msg)
        this.destroy()
    }

    //销毁
    async destroy() {
        if (this.localStream) {
            this.localStream.getTracks().forEach(track => track.stop())
            this.localStream = null
        }
        if (this.rtc) {
            this.rtc.close()
            this.rtc = null
        }
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