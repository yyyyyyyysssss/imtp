import React, { useMemo } from 'react';
import { Box, HStack, Text } from 'native-base';
import { StyleSheet } from 'react-native';
import { VoiceStaticIcon } from './CustomIcon';
import { MessageStatus } from '../enum';

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

    return (
        <Box width={calcWidthByDuration} style={direction === 'LEFT' ? styles.chatItemMessageBoxLeft : styles.chatItemMessageBoxRight}>
            <Box style={direction === 'LEFT' ? styles.chatItemMessageBoxLeftArrow : styles.chatItemMessageBoxRightArrow} />
            <HStack opacity={status && status === MessageStatus.PENDING ? 0 : 1} reversed={direction === 'LEFT' ? true : false} justifyContent={direction === 'LEFT' ? 'flex-start' : 'flex-end'} alignItems='center'>
                <Text style={styles.durationText}>{durationDesc}</Text>
                <VoiceStaticIcon style={{ transform: direction === 'LEFT' ? [{ rotate: '90deg' }] : [{ rotate: '-90deg' }] }} size={35} />
            </HStack>

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
        top: 10,
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