import React, { useEffect } from 'react';
import { Box, HStack, Text, VStack, Spinner } from 'native-base';
import { StyleSheet } from 'react-native';
import AntDesign from 'react-native-vector-icons/AntDesign';
import { MessageStatus } from '../enum';
import * as Progress from 'react-native-progress';


const FileMessage = React.memo(({ content, status, contentMetadata, progress }) => {

    const { name, sizeDesc } = contentMetadata

    return (
        <Box
            style={{
                position: 'relative',
                justifyContent: 'center',
                alignItems: 'center'
            }}
        >
            <HStack shadow={1} style={styles.rootHStack} space={2} justifyContent='center' alignItems='center'>
                <VStack flex={8} style={styles.leftVstack} justifyContent='space-between'>
                    <Text numberOfLines={2} style={styles.leftVstackName}>{name}</Text>
                    <Text style={styles.leftVstackSize}>{sizeDesc}</Text>
                </VStack>
                <VStack flex={2} style={styles.rightVstack} justifyContent='center'>
                    <AntDesign name="file1" size={40} />
                </VStack>
            </HStack>
        </Box>
    )
})

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