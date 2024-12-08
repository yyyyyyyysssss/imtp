import React, { useState } from 'react';
import { Box, HStack, Text, VStack } from 'native-base';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Feather from 'react-native-vector-icons/Feather';
import { useNavigation } from '@react-navigation/native';
import { Pressable } from 'native-base';

const ItemHeader = ({ title, moreOps, flex }) => {

    const navigation = useNavigation();

    const goBack = () => {
        navigation.goBack()
    }

    const more = () => {
        moreOps()
    }

    return (
        <>
            <Box flex={flex} style={{ overflow: 'hidden', paddingBottom: 5 }}>
                <HStack
                    flex={1}
                    justifyContent='center'
                    alignItems='center'
                    style={{
                        backgroundColor: '#fff',
                        shadowColor: '#000',
                        shadowOffset: { width: 1, height: 1 },
                        shadowOpacity: 0.4,
                        shadowRadius: 3,
                        elevation: 3,
                    }}>
                    <HStack flex={1}>
                        <Pressable
                            onPress={goBack}
                        >
                            {({ isPressed }) => {
                                return (
                                    <Ionicons
                                        name="arrow-back"
                                        style={{
                                            backgroundColor: isPressed ? '#C8C6C5' : 'transparent',
                                            borderRadius: 100,
                                            padding: 5,
                                        }}
                                        size={25}
                                    />
                                )
                            }}
                        </Pressable>
                    </HStack>
                    <HStack flex={8} justifyContent='center' alignItems='center'>
                        <Text style={{ fontWeight: 'bold', fontSize: 18 }}>{title}</Text>
                    </HStack>
                    <HStack flex={1}>
                        <Pressable
                            onPress={more}
                        >
                            {({ isPressed }) => {
                                return (
                                    <Feather
                                        name='more-horizontal'
                                        style={{
                                            backgroundColor: isPressed ? '#C8C6C5' : 'transparent',
                                            borderRadius: 100,
                                            padding: 5,
                                        }}
                                        size={28}
                                    />
                                )
                            }}
                        </Pressable>
                    </HStack>
                </HStack>
            </Box>
        </>
    )
}



export default ItemHeader;