import { RTCPeerConnection, RTCSessionDescription, RTCIceCandidate, mediaDevices, MediaStream } from 'react-native-webrtc';
import { NativeModules, NativeEventEmitter } from 'react-native';
import { showToast } from '../components/Utils';
import { CallType, MessageType } from '../enum';
import IdGen from '../utils/IdGen';

const { MessageModule } = NativeModules

const MessageModuleNativeEventEmitter = new NativeEventEmitter(MessageModule);

class WebRTCWrapper {
    constructor(callType, session, onReceiveClose, facingMode = 'environment') {
        this.callType = callType
        this.session = session
        this.facingMode = facingMode
        this.isRemoteDescriptionSet = false
        this.inited = false
        this.voiceEnabled = true
        this.cachedCandidates = []
        this.onReceiveClose = onReceiveClose
        //接收消息监听
        this.receiveMessageEventEmitter = MessageModuleNativeEventEmitter.addListener('RECEIVE_MESSAGE', (message) => {
            const msg = JSON.parse(message)
            const { header } = msg
            const { cmd } = header
            switch (cmd) {
                case MessageType.SIGNALING_OFFER:
                    const offer = msg.content
                    this.receiveOffer(offer)
                    return
                case MessageType.SIGNALING_ANSWER:
                    const answer = msg.content
                    this.receiveAnswer(answer)
                    return
                case MessageType.SIGNALING_CANDIDATE:
                    const candidate = msg.content
                    this.receiveCandidate(candidate)
                    return
                case MessageType.SIGNALING_BUSY:
                    this.receiveBusy()
                    return
                case MessageType.SIGNALING_CLOSE:
                    this.receiveClose()
                    return
            }
        })
    }

    async init() {
        this.rtc = new RTCPeerConnection({
            iceServers: [
                {
                    urls: 'turn:116.237.179.131:23478',
                    username: 'ys',
                    credential: 'Yan@136156'
                }
            ]
        })
        //创建数据通道 后续可能会使用
        this.channel = this.rtc.createDataChannel('channel')
        //本地媒体流添加到rtc轨道中
        this.localStream = await this.getMediaStream(this.facingMode)
        const tracks = this.localStream.getTracks()
        for (const track of tracks) {
            if (track.kind === 'audio') {
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

    async getMediaStream(facingMode) {
        try {
            return await mediaDevices.getUserMedia({
                audio: true,
                video: this.callType === CallType.VIDEO ?
                    {
                        facingMode: {
                            exact: facingMode
                        }
                    }
                    :
                    false
            })
        } catch (e) {
            showToast('获取本地音视频流失败')
        }
    }

    async switchCamera(){
        this.localStream.getVideoTracks().forEach((track) => {
            track._switchCamera()
        })
    }

    //发送预请求
    async sendPreOffer() {
        //发送preOffer
        this.#sendMessage(MessageType.SIGNALING_PRE_OFFER, this.callType)
    }

    //发送offer
    async sendOffer() {
        await this.init()
        //创建offer
        const offer = await this.rtc.createOffer()
        await this.#setLocalDescription(offer)
        //发送offer
        this.#sendMessage(MessageType.SIGNALING_OFFER, offer.sdp)
        return offer
    }

    //接收到offer
    async receiveOffer(offerSdp) {
        await this.init()
        await this.#setRemoteDescription('offer', offerSdp)
        //创建answer
        const answer = await this.rtc.createAnswer()
        await this.#setLocalDescription(answer)
        //发送answer
        this.#sendMessage(MessageType.SIGNALING_ANSWER, answer.sdp)
    }

    //接收到answer
    async receiveAnswer(sdp) {
        await this.#setRemoteDescription('answer', sdp)
    }

    //接收candidate
    async receiveCandidate(candidate) {
        this.#addIceCandidate(candidate)
    }

    //忙线
    static sendBusy(session) {
        const id = IdGen.nextId()
        const msg = {
            id: id,
            ackId: id,
            type: MessageType.SIGNALING_BUSY,
            sender: session.userId,
            receiver: session.receiverUserId,
            deliveryMethod: session.deliveryMethod,
        }
        MessageModule.sendMessage(JSON.stringify(msg))
    }

    //接收忙线
    async receiveBusy() {
        this.destroy()
    }

    //静音
    async mute() {
        if (this.inited) {
            this.#muted(false)
        } else {
            this.voiceEnabled = false
        }

    }

    //关闭静音
    async unmute() {
        if (this.inited) {
            this.#muted(true)
        } else {
            this.voiceEnabled = true
        }
    }

    async #muted(flag) {
        const audioTracks = this.localStream.getAudioTracks()
        audioTracks.forEach(track => {
            track.enabled = flag
        })
    }

    //接收关闭
    async receiveClose() {
        this.onReceiveClose()
        this.destroy()
    }

    //关闭rtc
    async close() {
        this.#sendMessage(MessageType.SIGNALING_CLOSE, null)
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
        this.receiveMessageEventEmitter.remove()
    }

    //设置本地description
    async #setLocalDescription(description) {
        await this.rtc.setLocalDescription(description)
        //监听服务器返回的新的候选地址
        this.rtc.onicecandidate = async (e) => {
            if (e.candidate) {
                this.#sendMessage(MessageType.SIGNALING_CANDIDATE, JSON.stringify(e.candidate))
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

    #sendMessage(signalingMessageType, content) {
        const msg = this.#signalingMessage(signalingMessageType, content)
        MessageModule.sendMessage(JSON.stringify(msg))
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

export default WebRTCWrapper