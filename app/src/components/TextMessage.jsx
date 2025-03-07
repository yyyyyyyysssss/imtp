import React from 'react';
import { StyleSheet } from 'react-native';
import { Text, Box } from 'native-base';

const TextMessage = React.memo(({ content, direction }) => {

    return (
        <Box style={direction === 'LEFT' ? styles.chatItemMessageBoxLeft : styles.chatItemMessageBoxRight}>
            <Box style={direction === 'LEFT' ? styles.chatItemMessageBoxLeftArrow : styles.chatItemMessageBoxRightArrow} />
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

export default TextMessage;