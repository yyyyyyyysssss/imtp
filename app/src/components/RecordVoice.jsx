import { Box, HStack } from "native-base"
import { useEffect, useRef } from "react"
import { Modal, StyleSheet } from "react-native"
import { Recorder, Player } from '@react-native-community/audio-toolkit';
import RNFS from 'react-native-fs';
import IdGen from "../utils/IdGen";
import FastImage from 'react-native-fast-image'


const RecordVoice = ({ overlayVisible, setOverlayVisible, messageProvider }) => {

    const recorderRef = useRef()

    const recorderFilePathRef = useRef()

    const timeoutRef = useRef()

    useEffect(() => {
        return () => {
            recorderRef.current?.destroy()
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current)
            }
        }
    }, [])

    useEffect(() => {
        const start = async () => {
            startRecorder()
        }
        const stop = () => {
            stopRecorder()
        }
        if (overlayVisible) {
            start()
        } else {
            stop()
        }
    }, [overlayVisible])



    const startRecorder = () => {
        if (recorderRef.current) {
            recorderRef.current.destroy()
        }
        const name = IdGen.nextId() + '.aac'
        recorderRef.current = new Recorder(name, {
            bitrate: 256000, //越高 音频质量越好
            channels: 2, //双声道
            sampleRate: 44100, //采样率
            quality: 'high', //录音质量
            format: 'aac'
        }).prepare((err, fsPath) => {
            if (err) {
                console.log('reload recorder error', err)
            } else {
                recorderFilePathRef.current = fsPath
            }
        })
        //开始录制
        recorderRef.current.record((err) => {
            if (err) {
                console.log('start recorder error', err)
            } else {
                console.log('start recorder')
            }

        })
        timeoutRef.current = setTimeout(() => {
            if (recorderRef.current?.isRecording) {
                setOverlayVisible(false)
            }
        }, 60000);
    }

    const stopRecorder = () => {
        recorderRef.current?.stop((err) => {
            if (err) {
                console.log('stop recorder error', err)
            } else {
                const filePath = recorderFilePathRef.current
                const player = new Player(filePath)
                    .prepare(async (err) => {
                        if (err) {
                            console.log('player prepare error', err);
                            return
                        }
                        const duration = player.duration;
                        player.destroy()
                        const fileName = filePath.substring(filePath.lastIndexOf('/') + 1)
                        const media = {
                            uri: filePath,
                            type: 'audio/x-hx-aac-adts',
                            fileName: fileName,
                            duration: duration
                        }
                        messageProvider(media)
                    })
                console.log('recording completed', filePath)
            }
            recorderRef.current.destroy()
        })
    }

    return (
        <Modal
            transparent={true}
            animationType='fade'
            visible={overlayVisible}
        >
            <Box style={styles.overlay}>
                <Box
                    style={{
                        borderRadius: 20,
                        paddingTop: 20,
                        paddingBottom: 20,
                        paddingLeft: 60,
                        paddingRight: 60,
                        backgroundColor: '#95EC69',
                    }}
                >
                    <HStack
                        space={1}
                        justifyContent='center'
                        alignItems='center'
                    >
                        <FastImage
                            style={{ width: 50, height: 50}}
                            source={require('../assets/gif/audio-50.gif')}
                            resizeMode={FastImage.resizeMode.contain}
                        />
                    </HStack>
                </Box>
            </Box>
        </Modal>
    )
}

const styles = StyleSheet.create({
    overlay: {
        ...StyleSheet.absoluteFillObject, // 覆盖整个屏幕
        backgroundColor: '#000',
        opacity: 0.8,
        justifyContent: 'center',
        alignItems: 'center'
    },
    bar: {
        width: 3,
        backgroundColor: 'black',
        borderRadius: 2
    }
})

export default RecordVoice