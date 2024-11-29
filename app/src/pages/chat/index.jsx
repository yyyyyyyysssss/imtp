import { NativeBaseProvider, Text } from 'native-base';
import React, { useState } from 'react';

const Chat = () => {

    return (
        <NativeBaseProvider>
            <Text>This is Chat</Text>
        </NativeBaseProvider>
    )
}

export default Chat;