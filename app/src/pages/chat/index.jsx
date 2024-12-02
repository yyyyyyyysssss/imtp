import { Button, Text } from 'native-base';
import React, { useState } from 'react';
import {useNavigation,} from '@react-navigation/native';
const Chat = () => {
    const navigation = useNavigation();

    const toChatItem = () => {
        navigation.navigate('ChatItem')
    }

    return (
        <>
            <Button onPress={toChatItem}>This is Chat</Button>
        </>
    )
}

export default Chat;