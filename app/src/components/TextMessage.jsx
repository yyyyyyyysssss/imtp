import React from 'react';
import { StyleSheet } from 'react-native';
import { Text, Box } from 'native-base';

const TextMessage = ({ content, contentMetadata, direction }) => {

    return (
        <Box style={direction === 'LEFT' ? styles.chatItemMessageBoxRight : styles.chatItemMessageBoxLeft}>
            <Box style={self ? styles.chatItemMessageBoxRightArrow : styles.chatItemMessageBoxLeftArrow} />
            <Text
                style={styles.chatItemMessageText}
                isTruncated={false}
                noOfLines={undefined}
                flexWrap='wrap'
            >
                {content}
            </Text>
        </Box>
    )
}


const styles = StyleSheet.create({
    chatItemMessageBoxLeft: {
        position: 'relative',
        borderRadius: 6,
        padding: 7,
        backgroundColor: 'white',
        maxWidth: '80%',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 5,
        elevation: 5,
    },
    chatItemMessageBoxRight: {
        position: 'relative',
        borderRadius: 6,
        padding: 7,
        backgroundColor: '#95EC69',
        maxWidth: '80%',
        shadowColor: "black",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.8,
        shadowRadius: 5,
        elevation: 5,
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
        fontSize: 16,
    }
})

export default TextMessage;