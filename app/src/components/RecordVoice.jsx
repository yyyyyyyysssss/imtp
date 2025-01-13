import { Box, HStack } from "native-base"
import { useEffect, useRef, useState } from "react"
import { Animated, Modal, PermissionsAndroid, StyleSheet } from "react-native"
import { Recorder, Player } from '@react-native-community/audio-toolkit';
import RNFS from 'react-native-fs';
import { randomInt } from "../utils/RandomUtil";


const NUM_BARS = 20;  // 音浪条形数量

const initValue = 10

const bars = []
for (let i = 0; i < NUM_BARS; i++) {
    bars.push(new Animated.Value(initValue));
}

const RecordVoice = ({ overlayVisible, setOverlayVisible }) => {

    const recorderRef = useRef()

    const recorderFilePathRef = useRef()

    const intervalRef = useRef()

    const timeoutRef = useRef()

    useEffect(() => {

        return () => {
            recorderRef.current?.destroy()
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current)
            }
            if (intervalRef.current) {
                clearInterval(intervalRef.current)
            }
        }
    })

    useEffect(() => {
        const start = async () => {
            const checked = await checkRecorderPermission()
            if (checked) {
                startRecorder()
                startAnimation()
            }
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

    const checkRecorderPermission = async () => {
        if (Platform.OS !== 'android') {
            return Promise.resolve(true);
        }
        let result;
        try {
            result = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.RECORD_AUDIO, { title: 'Microphone Permission', message: 'Enter the Gunbook needs access to your microphone so you can search with voice.' });
        } catch (error) {
            console.error('failed getting permission, result:', result);
        }
        return (result === true || result === PermissionsAndroid.RESULTS.GRANTED);
    }

    const startRecorder = () => {
        if (recorderRef.current) {
            recorderRef.current.destroy()
        }
        recorderRef.current = new Recorder('adrec.mp4', {
            bitrate: 256000, //越高 音频质量越好
            channels: 2, //双声道
            sampleRate: 44100, //采样率
            quality: 'high', //录音质量
            // format: 'aac'
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
        if (timeoutRef.current) {
            clearTimeout(timeoutRef.current)
        }
        recorderRef.current?.stop((err) => {
            if (err) {
                console.log('stop recorder error', err)
                if (intervalRef.current) {
                    clearInterval(intervalRef.current)
                }
            } else {
                const filePath = recorderFilePathRef.current
                const player = new Player(filePath)
                    .prepare(async (err) => {
                        if (err) {
                            console.log('音频加载失败：', err);
                            return
                        }
                        const result = await RNFS.stat(filePath)
                        const duration = player.duration;
                        console.log(`filePath: ${filePath} fileSize: ${result.size} duration: ${(duration / 1000).toFixed(2)}s`);
                        RNFS.readFile(filePath, 'base64')
                            .then(
                                (content) => {
                                    console.log('content', content)
                                }
                            )
                        player.destroy()
                    })
                // RNFS.stat(filePath)
                //     .then(
                //         (result) => {
                //             console.log(`filePath: ${filePath} totalSize: ${result.size}`)
                //         }
                //     )
                // RNFS.readFile(filePath, 'base64')
                //     .then(
                //         (content) => {
                //             console.log('content',content)
                //         }
                //     )
                if (intervalRef.current) {
                    clearInterval(intervalRef.current)
                }
                console.log('recording completed', filePath)
            }
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
        const firstHalf = bars.slice(0, 10).reverse()
        const secondHalf = bars.slice(10)
        const run = (init = false) => {
            const x1 = randomInt(0, 10)
            const x2 = randomInt(0, 10)
            const x3 = randomInt(0, 10)
            const x4 = randomInt(0, 10)
            firstHalf.forEach((bar, index) => {
                let animation;
                if (init) {
                    animation = animationInstance(bar, index, 15, 100, 200)
                } else if (x1 === index || x2 === index) {
                    animation = animationInstance(bar, index, 30, 50, 100)
                } else if (x3 === index || x4 === index) {
                    animation = animationInstance(bar, index, 40, 50, 100)
                } else {
                    animation = animationInstance(bar, index, 15, 100, 200)
                }
                animation.start()
            })
            const y1 = randomInt(0, 10)
            const y2 = randomInt(0, 10)
            const y3 = randomInt(0, 10)
            const y4 = randomInt(0, 10)
            secondHalf.forEach((bar, index) => {
                let animation;
                if (init) {
                    animation = animationInstance(bar, index, 15, 100, 200)
                } else if (y1 === index || y2 === index) {
                    animation = animationInstance(bar, index, 30, 50, 100)
                } else if (y3 === index || y4 === index) {
                    animation = animationInstance(bar, index, 40, 50, 100)
                } else {
                    animation = animationInstance(bar, index, 15, 100, 200)
                }
                animation.start()
            })
        }
        run(true)
        intervalRef.current = setInterval(() => {
            run(false)
        }, 500)
    }

    const animationInstance = (bar, index, value = 15, delay = 100, duration = 200) => {
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

    const stopAnimation = () => {
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