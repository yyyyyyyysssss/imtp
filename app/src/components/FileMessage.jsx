import React from 'react';
import { HStack, Text, VStack } from 'native-base';
import { StyleSheet } from 'react-native';
import AntDesign from 'react-native-vector-icons/AntDesign';


const FileMessage = ({ content, contentMetadata }) => {

    const { name, sizeDesc } = contentMetadata

    return (
        <HStack shadow={1} style={styles.rootHStack} space={2} justifyContent='center' alignItems='center'>
            <VStack flex={8} style={styles.leftVstack} justifyContent='space-between'>
                <Text numberOfLines={2} style={styles.leftVstackName}>{name}</Text>
                <Text style={styles.leftVstackSize}>{sizeDesc}</Text>
            </VStack>
            <VStack flex={2} style={styles.rightVstack} justifyContent='center'>
                <AntDesign name="file1" size={40} />
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
    }
})

export default FileMessage