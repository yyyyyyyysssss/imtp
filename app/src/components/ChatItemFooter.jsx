import React, { useState } from 'react';
import { Box, HStack, Text, VStack, Input } from 'native-base';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import Feather from 'react-native-vector-icons/Feather';
import SimpleLineIcons from 'react-native-vector-icons/SimpleLineIcons';

const ChatItemFooter = ({ flex }) => {

    return (
        <>
            <Box flex={flex} style={{ overflow: 'hidden', paddingTop: 5 }}>
                <HStack
                    flex={1}
                    justifyContent='center'
                    alignItems='center'
                    style={{
                        backgroundColor: '#F5F5F5',
                        shadowColor: '#000',
                        shadowOffset: { width: 1, height: 1 },
                        shadowOpacity: 0.4,
                        shadowRadius: 3,
                        elevation: 5,
                        borderTopColor: 'blank',
                        borderTopWidth: 0.1
                    }}
                >
                    <HStack flex={1} justifyContent='center'>
                        <MaterialIcon name="keyboard-voice" size={30} />
                    </HStack>
                    <HStack flex={5.5} justifyContent='center' alignItems='center'>
                        <Input
                            size='lg'
                            focusOutlineColor='none'
                            borderWidth={0}
                            backgroundColor='white'
                            w={{
                                base: "100%",
                            }}
                        />
                    </HStack>
                    <HStack flex={2.5} justifyContent='center' alignContent='center' space={3}>
                        <SimpleLineIcons name="emotsmile" size={25} />
                        <SimpleLineIcons name="plus" size={25} />
                    </HStack>
                </HStack>
            </Box>
        </>
    )
}

export default ChatItemFooter