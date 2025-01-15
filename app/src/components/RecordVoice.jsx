import { Box, HStack } from "native-base"
import { useEffect, useLayoutEffect, useRef, useState } from "react"
import { Animated, Modal, StyleSheet } from "react-native"
import { Recorder, Player } from '@react-native-community/audio-toolkit';
import RNFS from 'react-native-fs';
import { randomInt } from "../utils/RandomUtil";
import IdGen from "../utils/IdGen";


const NUM_BARS = 20;  // 音浪条形数量

const initValue = 10

const bars = []
for (let i = 0; i < NUM_BARS; i++) {
    bars.push(new Animated.Value(initValue));
}

const RecordVoice = ({ overlayVisible, setOverlayVisible, messageProvider }) => {

    const recorderRef = useRef()

    const recorderFilePathRef = useRef()

    const timeoutRef = useRef()

    const animatedParallelRef = useRef()
    const animatedLoopRef = useRef()

    useEffect(() => {
        const animationInstance = (bar, index, value = 15, delay = 50, duration = 50) => {
            const originalValue = new Animated.Value(initValue)
            const toAnimatedValue = new Animated.Value(value)
            return Animated.sequence([
                Animated.delay(index * delay),
                Animated.timing(bar, {
                    toValue: toAnimatedValue,
                    duration: duration,
                    useNativeDriver: true
                }),
                Animated.timing(bar, {
                    toValue: originalValue,
                    duration: duration,
                    useNativeDriver: true
                }),
            ])
        }
        const animatedParallel = (init = false) => {
            const x1 = randomInt(0, 10)
            const x2 = randomInt(0, 10)
            const x3 = randomInt(0, 10)
            const x4 = randomInt(0, 10)
            const animatedSequence = []
            const firstHalf = bars.slice(0, 10).reverse()
            firstHalf.forEach((bar, index) => {
                let animation;
                if (init) {
                    animation = animationInstance(bar, index, 50)
                } else if (x1 === index || x2 === index) {
                    animation = animationInstance(bar, index, 30)
                } else if (x3 === index || x4 === index) {
                    animation = animationInstance(bar, index, 40)
                } else {
                    animation = animationInstance(bar, index, 20)
                }
                animatedSequence.push(animation)
            })
            const y1 = randomInt(0, 10)
            const y2 = randomInt(0, 10)
            const y3 = randomInt(0, 10)
            const y4 = randomInt(0, 10)
            const secondHalf = bars.slice(10)
            secondHalf.forEach((bar, index) => {
                let animation;
                if (init) {
                    animation = animationInstance(bar, index, 50)
                } else if (y1 === index || y2 === index) {
                    animation = animationInstance(bar, index, 30)
                } else if (y3 === index || y4 === index) {
                    animation = animationInstance(bar, index, 40)
                } else {
                    animation = animationInstance(bar, index, 20)
                }
                animatedSequence.push(animation)
            })

            return Animated.parallel(animatedSequence, { stopTogether: false })
        }
        animatedParallelRef.current = animatedParallel()
        return () => {
            recorderRef.current?.destroy()
            animatedParallelRef.current?.stop()
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current)
            }
        }
    }, [])

    useEffect(() => {
        const start = async () => {
            startRecorder()
            startAnimation()
        }
        const stop = () => {
            stopRecorder()
            stopAnimation()
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

    const readRecorderStream = (fsPath) => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current)
        }
        let lastPosition = 0
        const readNewData = async () => {
            try {
                const result = await RNFS.stat(fsPath)
                const fileSize = result.size
                if (fileSize > lastPosition) {
                    startAnimation()
                    const length = fileSize - lastPosition
                    // RNFS.read(fsPath)
                    console.log(`fileSize: ${fileSize} lastPosition: ${lastPosition} length: ${length}`)
                    //更新读取位置
                    lastPosition = fileSize
                } else {
                    startAnimation()
                }
            } catch (error) {
                console.error('Error reading file:', error);
            }
        }

        intervalRef.current = setInterval(() => {
            readNewData()
        }, 500);
    }

    const startAnimation = () => {
        if(animatedLoopRef.current){
            animatedLoopRef.current.stop()
        }
        animatedLoopRef.current = Animated.loop(animatedParallelRef.current)
        animatedLoopRef.current.start()
    }

    const stopAnimation = () => {
        if (animatedLoopRef.current) {
            animatedLoopRef.current.stop()
        }
        const toValue = new Animated.Value(initValue)
        bars.forEach(bar => {
            Animated.timing(bar, {
                toValue,
                duration: 0,
                useNativeDriver: true
            }).start()
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
                        paddingTop: 40,
                        paddingBottom: 40,
                        paddingLeft: 40,
                        paddingRight: 40,
                        backgroundColor: '#95EC69',
                    }}
                >
                    <HStack
                        space={1}
                        justifyContent='center'
                        alignItems='center'
                    >
                        {
                            bars.map((bar, index) => (
                                <Animated.View
                                    key={index}
                                    style={[
                                        {
                                            height: 1,
                                            width: 3,
                                            backgroundColor: 'black'
                                        },
                                        {
                                            transform: [
                                                {
                                                    scaleY: bar
                                                }
                                            ]
                                        }
                                    ]}
                                />
                            ))
                        }
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