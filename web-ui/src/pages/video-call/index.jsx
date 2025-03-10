import React, { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import './index.less'
import { CloseOutlined, MinusOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons'
import Draggable from 'react-draggable';
import { Avatar, Flex } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { addMessage, stopVoiceCall } from '../../redux/slices/chatSlice';
import { HangUpOutlined, VideoOffLined, VideoOnLined, VolumeMutedLined, VolumeUpLined } from '../../components/customIcon';
import { CallStatus } from '../../enum';




const VideoCall = forwardRef(({ sendMessage }, ref) => {

    const nodeRef = useRef(null)

    const voiceCall = useSelector(state => state.chat.voiceCall)

    const { callOperation, sessionId } = voiceCall
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId]) || {}

    const { name, avatar } = session

    const dispatch = useDispatch()

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

    const remoteVideoRef = useRef(null)
    const localVideoRef = useRef(null)
    

    const switchScreen = () => {
        setIsVideoALarge(!isVideoALarge)
    }

    const microphoneMute = async () => {
        setMicrophoneMuteFlag((prev) => !prev)
    }

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

    const hangUp = () => {
        dispatch(stopVoiceCall())
    }

    return (
        <Draggable nodeRef={nodeRef} offsetParent={document.body} handle='.drag-handle'>
            <Flex
                flex={1}
                ref={nodeRef}
                style={{
                    backgroundColor: 'black',
                    width: 350,
                    height: 600,
                    position: 'fixed',
                    zIndex: 1000,
                    borderRadius: 10,
                    top: '20%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    overflow: 'hidden'
                }}
                vertical
            >
                <video
                    ref={remoteVideoRef}
                    src='http://localhost:9000/y-chat-bucket/09aa1760e1e0427eba4da332f6d80bf5.mp4'
                    className={`video ${isVideoALarge ? 'large' : 'small'}`}
                    onClick={switchScreen}
                    style={{
                        zIndex: isVideoALarge ? 1 : 1000
                    }}
                />
                <video
                    ref={localVideoRef}
                    src='http://localhost:9000/y-chat-bucket/a04c0cbe7ec4472586d577786c7e93d6.mp4'
                    className={`video ${isVideoALarge ? 'small' : 'large'}`}
                    onClick={switchScreen}
                    style={{
                        zIndex: isVideoALarge ? 1000 : 1
                    }}
                />
                <Flex
                    style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        opacity: isControlShow ? 1 : 0,
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
                            justifyContent: 'flex-end',
                            padding: 8
                        }}
                    >
                        <Flex style={{ cursor: 'pointer' }} onClick={hangUp}>
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
                                fontSize: 16,
                                userSelect: 'none'
                            }}
                        >
                            {name}
                        </div>
                        <div
                            style={{
                                color: 'white',
                                fontSize: 12,
                                userSelect: 'none'
                            }}
                        >
                            等待对方接受邀请
                        </div>
                    </Flex>
                    <Flex
                        flex={2}
                        gap={40}
                        justify='center'
                        align='flex-end'
                    >
                        <div
                            className='control-btn-icon-white'
                            onClick={microphoneMute}
                        >
                            {microphoneMuteFlag ? <AudioMutedOutlined style={{ fontSize: 30, color: 'black' }} /> : <AudioOutlined style={{ fontSize: 30, color: 'black' }} />}
                        </div>
                        <div
                            className='control-btn-icon-white'
                            onClick={speakerMute}
                        >
                            {speakerMuteFlag ? <VolumeMutedLined size={30} /> : <VolumeUpLined size={30} />}
                        </div>
                        <div
                            className='control-btn-icon-white'
                            onClick={cameraOff}
                        >
                            {cameraOffFlag ? <VideoOffLined size={30} /> : <VideoOnLined size={30} />}
                        </div>
                    </Flex>
                    <Flex
                        flex={2}
                        justify='center'
                        align='center'
                    >
                        <div
                            className='control-btn-icon-red'
                            onClick={hangUp}
                        >
                            <HangUpOutlined size={30} />
                        </div>
                    </Flex>
                </Flex>
            </Flex>
        </Draggable>
    )

})

export default React.memo(VideoCall)