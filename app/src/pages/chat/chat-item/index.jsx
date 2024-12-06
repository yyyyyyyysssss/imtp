import { HStack, Text, VStack } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Feather from 'react-native-vector-icons/Feather';
import ItemHeader from '../../../components/ItemHeader';

const ChatItem = () => {

    return (
        <>
            <VStack flex={1} justifyContent="space-between">
                <HStack flex={0.8} style={styles.headHstack} alignItems='center'>
                    <ItemHeader title='张三'/>
                </HStack>
                <HStack flex={8.4} style={styles.contentHstack}>

                </HStack>
                <HStack flex={0.8} style={styles.footerHstack}>

                </HStack>
            </VStack>
        </>
    )
}

const styles = StyleSheet.create({
    headHstack: {
        padding: 5
    },
    contentHstack: {
        backgroundColor: 'red'
    },
    footerHstack: {
        backgroundColor: 'yellow'
    }
})

export default ChatItem;