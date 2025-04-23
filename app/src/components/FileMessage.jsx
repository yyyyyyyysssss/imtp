import React from 'react';
import { Box, HStack, Text, VStack } from 'native-base';
import { StyleSheet } from 'react-native';
import AntDesign from 'react-native-vector-icons/AntDesign';


const FileMessage = React.memo(({ filename, fileSize }) => {

    return (
        <Box
            style={{
                position: 'relative',
                justifyContent: 'center',
                alignItems: 'center'
            }}
        >
            <HStack shadow={1} style={styles.rootHStack} >
                <SubFilemessage filename={filename} fileSize={fileSize} />
            </HStack>
        </Box>
    )
})

export const SubFilemessage = ({ filename, fileSize, size = 'large', justifyContent = 'center', alignItems = 'center' }) => {

    return (
        <HStack flex={1} space={2} justifyContent={justifyContent} alignItems={alignItems}>
            <VStack flex={8} style={styles.leftVstack} justifyContent='space-between'>
                <Text numberOfLines={2} style={styles.leftVstackName}>{filename}</Text>
                {fileSize && (
                    <Text style={styles.leftVstackSize}>{fileSize}</Text>
                )}
            </VStack>
            <VStack flex={2} style={styles.rightVstack} justifyContent='center'>
                <AntDesign name="file1" size={size === 'large' ? 40 : 30} />
            </VStack>
        </HStack>
    )
}

const styles = StyleSheet.create({
    rootHStack: {
        width: 200,
        height: 80,
        backgroundColor: 'white',
        padding: 7,
        borderRadius: 6
    },
    leftVstack: {
        height: '80%',
    },
    rightVstack: {
        height: '100%',
    },
    leftVstackName: {
        flexWrap: 'wrap'
    },
    leftVstackSize: {
        fontSize: 12,
        color: 'gray'
    },
    absolute: {
        position: "absolute",
    }
})

export default FileMessage