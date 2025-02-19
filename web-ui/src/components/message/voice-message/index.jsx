import React, { useRef, useState, useMemo } from 'react'
import './index.less'
import { Flex } from 'antd'
import voicePlayGif from '../../../assets/gif/voice-play.gif'
import voicePlayPng from '../../../assets/img/voice-play.png'

const minPercentage = 0.25
const maxWidth = 300

const VoiceMessage = React.memo(({ content, status, duration, direction }) => {

    const [playing, setPlaying] = useState(false)

    const audioRef = useRef()

    const durationDesc = useMemo(() => {

        return Math.floor(duration / 1000) + '\'\''
    }, [duration])

    const calcWidthByDuration = useMemo(() => {
        let s = Math.floor(duration / 1000) / 60
        s = s < minPercentage ? minPercentage + s : s
        const d = s > 1 ? 1 : s
        return d * maxWidth
    }, [duration])

    const playVoice = () => {
        if (playing) {
            audioRef.current.pause()
            audioRef.current.currentTime = 0
        } else {
            audioRef.current.play()
        }
        setPlaying(!playing)
    }

    const playCompleted = () => {
        setPlaying(false)
    }

    return (
        <div onClick={playVoice} className={`voice-message ${direction === 'RIGHT' ? 'voice-message-right' : 'voice-message-left'}`}>
            <Flex style={{ width: calcWidthByDuration, flexDirection: direction === 'LEFT' ? 'row-reverse' : '' }} justify='space-between'>
                <audio autoPlay={false} onEnded={playCompleted} ref={audioRef} src={content} preload='metadata' />
                <div style={{ fontSize: 18 }}>{durationDesc}</div>
                <img
                    style={{
                        transform: direction === 'LEFT' ? 'rotate(90deg)' : 'rotate(-90deg)'
                    }}
                    src={playing ? voicePlayGif : voicePlayPng}
                    alt=''
                />
            </Flex>
        </div>
    )
})

export default VoiceMessage