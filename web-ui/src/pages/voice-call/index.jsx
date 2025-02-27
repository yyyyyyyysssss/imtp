import React, { useEffect, useRef, useState } from 'react';
import './index.less'
import { CloseOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons'
import { HangUpOutlined } from '../../components/customIcon';
import { Avatar, Flex } from 'antd';
import { VoiceCallType } from '../../enum';
import useTimer from '../../hooks/useTimer';
import Draggable from 'react-draggable';
import { useDispatch, useSelector } from 'react-redux';
import { stopVoiceCall } from '../../redux/slices/chatSlice';


const VoiceCall = React.memo(() => {

    console.log('VoiceCall')

    const dispatch = useDispatch()

    const voiceCall = useSelector(state => state.chat.voiceCall)

    const { visible, type, sessionId } = voiceCall
    //会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId]) || {}

    const { name, avatar } = session

    useEffect(() => {

    }, [visible])

    const nodeRef = useRef(null)
    //是否静音
    const [isMute, setIsMute] = useState(false)
    const [inCall, setInCall] = useState(false)
    //计时器
    const { timer, toggleTimer, resetTimer } = useTimer()
    //开始通话
    const startCall = () => {
        toggleTimer()
        setInCall(true)
    }

    const stopCall = () => {
        dispatch(stopVoiceCall())
        resetTimer()
        setInCall(false)
    }

    const mute = () => {
        if (isMute) {
            //开启声音
        } else {
            //关闭声音
        }
        setIsMute((prev) => !prev)
    }

    const accept = () => {
        startCall()
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
                            <Flex style={{ cursor: 'pointer' }} onClick={stopCall}>
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
                                        {inCall ? timer : type === VoiceCallType.INVITE ? '正在呼叫...' : '邀请你语音通话'}
                                    </div>
                                </Flex>
                            </Flex>
                            <Flex justify='space-between' gap={20} style={{ paddingLeft: 10, paddingRight: 10, flexDirection: inCall ? 'row-reverse' : type === VoiceCallType.ACCEPT ? '' : 'row-reverse' }}>
                                <div
                                    style={{
                                        padding: 10,
                                        backgroundColor: 'red',
                                        borderRadius: 100,
                                        display: 'flex'
                                    }}
                                    onClick={stopCall}
                                >
                                    <HangUpOutlined />
                                </div>
                                {
                                    inCall || type === VoiceCallType.INVITE
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
                                            <div
                                                style={{
                                                    padding: 10,
                                                    backgroundColor: '#00B853',
                                                    borderRadius: 100,
                                                    display: 'flex'
                                                }}
                                                onClick={accept}
                                            >
                                                <PhoneOutlined style={{ fontSize: 25, color: 'white' }} />
                                            </div>
                                        )
                                }

                            </Flex>
                        </Flex>
                    </Flex>
                </Draggable>
            )
            }
        </>


    )
})

export default VoiceCall