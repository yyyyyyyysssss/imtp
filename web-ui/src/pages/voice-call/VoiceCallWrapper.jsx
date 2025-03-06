import { useDispatch, useSelector } from 'react-redux';
import VoiceCall from '.';
import { forwardRef, useCallback, useImperativeHandle, useRef } from 'react';
import { startVoiceCall } from '../../redux/slices/chatSlice';
import { CallType } from '../../enum';


const VoiceCallWrapper = forwardRef(({ sendMessage }, ref) => {

    const voiceCallRef = useRef()

    const dispatch = useDispatch()

    useImperativeHandle(ref, () => ({
        receiveSignalingOffer: receiveSignalingOffer,
        receiveSignalingAnswer: receiveSignalingAnswer,
        receiveSignalingCandidate: receiveSignalingCandidate,
        receiveSignalingBusy: receiveSignalingBusy,
        receiveSignalingClose: receiveSignalingClose
    }))


    const voiceCallVisible = useSelector(state => state.chat.voiceCall.visible)

    //接收offer
    const receiveSignalingOffer = useCallback((session, sdp) => {
        if (voiceCallVisible) {
            //发送忙线
            voiceCallRef.current.busy(session)
        } else {
            dispatch(startVoiceCall({
                sessionId: session.id,
                type: CallType.ACCEPT,
                offerSdp: sdp
            }))
        }
    },[voiceCallVisible])

    //接收answer
    const receiveSignalingAnswer = async (sdp) => {
        voiceCallRef.current?.receiveSignalingAnswer(sdp)
    }

    //接收candidate
    const receiveSignalingCandidate = (candidate) => {
        voiceCallRef.current?.receiveSignalingCandidate(candidate)
    }

    //接收busy
    const receiveSignalingBusy = async () => {
        voiceCallRef.current?.receiveSignalingBusy()
    }

    //接收close
    const receiveSignalingClose = async () => {
        voiceCallRef.current?.receiveSignalingClose()
    }

    return (
        <>
            {voiceCallVisible && (
                <VoiceCall ref={voiceCallRef} sendMessage={sendMessage} />
            )}
        </>
    )

})

export default VoiceCallWrapper