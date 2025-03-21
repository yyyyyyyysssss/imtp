import React from 'react';
import { StyleSheet } from 'react-native';
import { Text, Box, HStack } from 'native-base';
import { CallStatus } from '../enum';
import { VoiceCallMessageIcon } from './CustomIcon';

const VoiceCallMessage = React.memo(({ callStatus, self, duration, durationDesc }) => {

    let content
    switch (callStatus) {
        case CallStatus.COMPLETED:
            content = '通话时长'
            break
        case CallStatus.CANCELLED:
            content = self ? '已取消' : '对方已取消'
            break
        case CallStatus.REFUSED:
            content = self ? '对方已拒接' : '已拒接'
            break
        case CallStatus.INTERRUPTED:
            content = '通话中断'
            break
    }

    return (
        <Box style={self ? styles.chatItemMessageBoxRight : styles.chatItemMessageBoxLeft }>
            <Box style={self ? styles.chatItemMessageBoxRightArrow : styles.chatItemMessageBoxLeftArrow} />
            <HStack space={2} justifyContent='center' alignItems='center'>
                <VoiceCallMessageIcon size={7} style={{color: 'black'}} />
                <Text style={styles.chatItemMessageText}>
                    {content}
                </Text>
                {callStatus === CallStatus.COMPLETED && (
                    <Text>
                        {durationDesc}
                    </Text>
                )}
            </HStack>

        </Box>
    )

})

const styles = StyleSheet.create({
    chatItemMessageBoxLeft: {
        position: 'relative',
        borderRadius: 6,
        padding: 10,
        backgroundColor: 'white',
        maxWidth: '80%',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 1,
        elevation: 1,
    },
    chatItemMessageBoxRight: {
        position: 'relative',
        borderRadius: 6,
        padding: 10,
        backgroundColor: '#95EC69',
        maxWidth: '80%',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 1,
        elevation: 1,
    },
    chatItemMessageBoxLeftArrow: {
        position: 'absolute',
        top: 10,
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
    chatItemMessageText: {
        fontSize: 18,
    }
})

export default VoiceCallMessage