import { Box, HStack } from "native-base"
import { useEffect, useRef, useState } from "react"
import { Animated, Modal, PermissionsAndroid, StyleSheet } from "react-native"
import { Recorder } from '@react-native-community/audio-toolkit';
import RNFS from 'react-native-fs';


const NUM_BARS = 20;  // 音浪条形数量

const initValue = 10

const bars = []
for (let i = 0; i < NUM_BARS; i++) {
    bars.push(new Animated.Value(initValue));
}

const RecordVoice = ({ overlayVisible }) => {

    const animationLoopRef = useRef([])

    const recorderRef = useRef()

    const recorderFilePathRef = useRef()

    useEffect(() => {

        return () => {
            recorderRef.current?.destroy((err) => {
                if(err){
                    console.log('destroy recorder error', err)
                }
            })
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
        if (overlayVisible) {
            start()
        } else {
            stopRecorder()
            stopAnimations()
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
        const recorder = reloadRecorder('adrec.aac')
        recorderRef.current = recorder
        recorder.record((err) => {
            if (err) {
                console.log('start recorder error', err)
            } else {
                console.log('start recorder')
            }

        })
    }

    const stopRecorder = () => {
        recorderRef.current?.stop((err) => {
            if (err) {
                console.log('stop recorder error', err)
            } else {
                const filePath = recorderFilePathRef.current
                RNFS.readFile(filePath, 'base64')
                    .then(
                        (content) => {
                            console.log('content',content)
                        }
                    )
                console.log('recording completed', filePath)
            }
        })
    }

    const reloadRecorder = (path) => {

        return new Recorder(path, {
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
    }

    const startAnimation = () => {
        animationLoopRef.current.forEach(loop => loop.stop())
        animationLoopRef.current = []
        const firstHalf = bars.slice(0, 10)
        const secondHalf = bars.slice(10)
        firstHalf.reverse().forEach((bar, index) => {
            const animation = animationInstance(bar, index, 15, 300)
            const animatedLoop = Animated.loop(animation)
            animationLoopRef.current.push(animatedLoop)
            animatedLoop.start()
        })
        secondHalf.forEach((bar, index) => {
            const animation = animationInstance(bar, index, 15, 300)
            const animatedLoop = Animated.loop(animation)
            animationLoopRef.current.push(animatedLoop)
            animatedLoop.start()
        })
    }

    const animationInstance = (bar, index, value = 15, duration = 200) => {
        const originalValue = new Animated.Value(initValue)
        const toAnimatedValue = new Animated.Value(value)
        return Animated.sequence([
            Animated.delay(index * 100),
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

    const stopAnimations = () => {
        animationLoopRef.current.forEach(loop => loop.stop())
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