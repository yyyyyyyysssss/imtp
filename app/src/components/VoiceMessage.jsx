import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Box, HStack, Text } from 'native-base';
import { Platform, Pressable, StyleSheet, TouchableOpacity } from 'react-native';
import { VoiceStaticIcon } from './CustomIcon';
import { MessageStatus } from '../enum';
import { Player } from '@react-native-community/audio-toolkit';

const minWidth = 0.25

const VoiceMessage = React.memo(({ content, status, duration, direction }) => {

    const durationDesc = useMemo(() => {

        return Math.floor(duration / 1000) + '\'\''
    }, [duration])

    const calcWidthByDuration = useMemo(() => {
        let s = Math.floor(duration / 1000) / 60
        s = s < minWidth ? minWidth + s : s
        return s > 1 ? '100%' : `${s * 100}%`
    }, [duration])


    const playRef = useRef()

    useEffect(() => {
        return () => {
            playRef.current?.destroy()
        }
    }, [])

    useEffect(() => {
        playRef.current = new Player(content, {
            autoDestroy: false
        })
        if (Platform.OS === 'android') {
            playRef.current.speed = 0.0
        }
    }, [content])

    const [inPlay, setInPlay] = useState(false)



    const playVoice = () => {
        console.log('playVoice', content)
        if(playRef.current.isPlaying){
            playRef.current.stop()
            return
        }
        playRef.current.play((err) => {
            if (err) {
                console.log('play', err)
            }
        })
    }

    return (
        <Box width={calcWidthByDuration} style={direction === 'LEFT' ? styles.chatItemMessageBoxLeft : styles.chatItemMessageBoxRight}>
            <Box style={direction === 'LEFT' ? styles.chatItemMessageBoxLeftArrow : styles.chatItemMessageBoxRightArrow} />
            <Pressable onPress={playVoice} hitSlop={7}>
                <HStack opacity={status && status === MessageStatus.PENDING ? 0 : 1} reversed={direction === 'LEFT' ? true : false} justifyContent={direction === 'LEFT' ? 'flex-start' : 'flex-end'} alignItems='center'>
                    <Text style={styles.durationText}>{durationDesc}</Text>
                    <VoiceStaticIcon style={{ transform: direction === 'LEFT' ? [{ rotate: '90deg' }] : [{ rotate: '-90deg' }] }} size={35} />
                </HStack>
            </Pressable>
        </Box>
    )
})

const styles = StyleSheet.create({
    chatItemMessageBoxLeft: {
        position: 'relative',
        maxWidth: '80%',
        borderRadius: 6,
        padding: 7,
        backgroundColor: 'white',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 1,
        elevation: 1,
    },
    chatItemMessageBoxRight: {
        position: 'relative',
        maxWidth: '80%',
        borderRadius: 6,
        padding: 7,
        backgroundColor: '#95EC69',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 1,
        elevation: 1,
    },
    chatItemMessageBoxLeftArrow: {
        position: 'absolute',
        top: 15,
        left: -7,
        width: 0,
        height: 0,
        borderTopWidth: 8,
        borderBottomWidth: 8,
        borderRightWidth: 10,
        borderTopColor: 'transparent',
        borderBottomColor: 'transparent',
        borderRightColor: 'white'
    },
    chatItemMessageBoxRightArrow: {
        position: 'absolute',
        top: 15,
        right: -7,
        width: 0,
        height: 0,
        borderTopWidth: 8,
        borderBottomWidth: 8,
        borderLeftWidth: 10,
        borderTopColor: 'transparent',
        borderBottomColor: 'transparent',
        borderLeftColor: '#95EC69'
    },
    durationText: {
        fontSize: 18
    }
})

export default VoiceMessage