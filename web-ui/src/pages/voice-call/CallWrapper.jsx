import { useDispatch, useSelector } from 'react-redux';
import VoiceCall from '.';
import { forwardRef, useCallback, useImperativeHandle, useRef } from 'react';
import { startVoiceCall } from '../../redux/slices/chatSlice';
import { CallType } from '../../enum';
import VideoCall from '../video-call';


const CallWrapper = forwardRef(({ sendMessage }, ref) => {

    const callRef = useRef()

    const dispatch = useDispatch()

    useImperativeHandle(ref, () => ({
        receiveSignalingPreOffer: receiveSignalingPreOffer,
        receiveSignalingOffer: receiveSignalingOffer,
        receiveSignalingAnswer: receiveSignalingAnswer,
        receiveSignalingCandidate: receiveSignalingCandidate,
        receiveSignalingBusy: receiveSignalingBusy,
        receiveSignalingClose: receiveSignalingClose
    }))


    const voiceCall = useSelector(state => state.chat.voiceCall)

    const { visible, callType } = voiceCall

    const receiveSignalingPreOffer = useCallback((session, callType) => {
        if (visible) {
            //发送忙线
            callRef.current.sendBusy(session)
        } else {
            dispatch(startVoiceCall({
                sessionId: session.id,
                callOperation: CallType.ACCEPT,
                callType: callType
            }))
        }
    }, [visible])

    //接收offer
    const receiveSignalingOffer = (sdp) => {
        callRef.current?.receiveSignalingOffer(sdp)
    }

    //接收answer
    const receiveSignalingAnswer = async (sdp) => {
        callRef.current?.receiveSignalingAnswer(sdp)
    }

    //接收candidate
    const receiveSignalingCandidate = (candidate) => {
        callRef.current?.receiveSignalingCandidate(candidate)
    }

    //接收busy
    const receiveSignalingBusy = async () => {
        callRef.current?.receiveSignalingBusy()
    }

    //接收close
    const receiveSignalingClose = async () => {
        callRef.current?.receiveSignalingClose()
    }


    const readerItem = useCallback((callType) => {
        switch(callType){
            case CallType.VOICE:
                return <VoiceCall ref={callRef} sendMessage={sendMessage} />
            case CallType.VIDEO:
                return <VideoCall ref={callRef} sendMessage={sendMessage} />
        }
    },[callType])

    return (
        <>
            {readerItem(callType)}
        </>
    )

})

export default CallWrapper