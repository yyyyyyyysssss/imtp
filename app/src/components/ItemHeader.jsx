import React, { useState } from 'react';
import { Box, HStack, Text, VStack } from 'native-base';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Feather from 'react-native-vector-icons/Feather';
import { useNavigation } from '@react-navigation/native';
import { Pressable } from 'native-base';

const ItemHeader = ({ title }) => {

    const navigation = useNavigation();

    const goBack = () => {
        navigation.goBack()
    }

    return (
        <>
            <HStack flex={1}>
                <Pressable
                    onPress={goBack}
                >
                    {({ isPressed }) => {
                        return (
                            <Box style={{borderRadius: 100,backgroundColor: isPressed ? '#C8C6C5' : '',padding: 5}}>
                                <Ionicons name="arrow-back" size={25} />
                            </Box>
                        )
                    }}
                </Pressable>
            </HStack>
            <HStack flex={8} justifyContent='center' alignItems='center'>
                <Text style={{ fontWeight: 'bold', fontSize: 18 }}>{title}</Text>
            </HStack>
            <HStack flex={1}>
                <Feather name='more-horizontal' size={28} />
            </HStack>
        </>
    )
}

export default ItemHeader;