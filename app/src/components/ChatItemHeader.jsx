import React, { useState } from 'react';
import { Box, HStack, Text, Avatar } from 'native-base';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Feather from 'react-native-vector-icons/Feather';
import { useNavigation } from '@react-navigation/native';
import { Pressable } from 'native-base';
import { useSelector } from 'react-redux';

const ItemHeader = ({ title, moreOps, flex }) => {

    const unreadCount = useSelector(state => state.chat.unreadCount)

    const navigation = useNavigation();

    const goBack = () => {
        navigation.goBack()
    }


    const more = () => {
        moreOps()
    }

    return (
        <>
            <Box style={{ overflow: 'hidden', paddingBottom: 5, height: 65 }}>
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
                    <HStack flex={2}>
                        <Pressable
                            onPress={goBack}
                        >
                            {({ isPressed }) => {
                                return (
                                    <HStack justifyContent='center' alignItems='center'>
                                        <Ionicons
                                            name="chevron-back"
                                            color="black"
                                            style={{
                                                borderRadius: 100,
                                            }}
                                            size={28}
                                        />
                                        {unreadCount > 0 && (
                                            <Box
                                                position='relative'
                                                style={{
                                                    width: 26,
                                                    height: 26,
                                                    borderRadius: 13,
                                                    backgroundColor: '#C8C6C5',
                                                    justifyContent: 'center',
                                                    alignItems: 'center',
                                                    marginLeft: -5
                                                }}
                                            >
                                                <Text
                                                    style={{
                                                        position: 'absolute',
                                                        fontWeight: 'bold',
                                                        textAlign: 'center',
                                                        lineHeight: 26,
                                                    }}
                                                >
                                                    {unreadCount > 99 ? '99+' : unreadCount}
                                                </Text>
                                            </Box>
                                        )}
                                    </HStack>
                                )
                            }}
                        </Pressable>

                    </HStack>
                    <HStack flex={6} justifyContent='center' alignItems='center'>
                        <Text style={{ fontWeight: 'bold', fontSize: 18 }}>{title}</Text>
                    </HStack>
                    <HStack flex={2} justifyContent='flex-end'>
                        <Pressable
                            onPress={more}
                        >
                            {({ isPressed }) => {
                                return (
                                    <Feather
                                        name='more-horizontal'
                                        color="black"
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