import { NativeBaseProvider, Text } from 'native-base';
import React, { useState } from 'react';

const Friend = () => {

    return (
        <NativeBaseProvider>
            <Text>This is Friend</Text>
        </NativeBaseProvider>
    )
}

export default Friend;