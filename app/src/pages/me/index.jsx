import { NativeBaseProvider, Text } from 'native-base';
import React, { useState } from 'react';

const Me = () => {

    return (
        <NativeBaseProvider>
            <Text>This is Me</Text>
        </NativeBaseProvider>
    )
}

export default Me;