import { Button, Center, Container, Flex, Heading, Input, InputGroup, Pressable, Text, VStack } from 'native-base';
import React, { useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
const Chat = () => {
    const navigation = useNavigation();

    const toChatItem = () => {
        navigation.navigate('ChatItem')
    }

    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Center style={styles.searchCenter}>
                    <Input
                        borderWidth="0"
                        backgroundColor='white'
                        size='lg'
                        shadow={1}
                        InputLeftElement={
                            <Pressable style={{ marginLeft: 8 }}>
                                <AntDesignIcon name='search1' size={18} color="gray" />
                            </Pressable>
                        }
                        placeholder='搜索'
                    />
                </Center>
                <Center>
                    <Button onPress={toChatItem}>This is Chat</Button>
                </Center>
            </VStack>
        </>
    )
}


const styles = StyleSheet.create({
    rootVStack: {
        backgroundColor: '#F5F5F5',
        flex: 1
    },
    searchCenter: {
        paddingLeft: 10,
        paddingRight: 10,
    }
})

export default Chat;