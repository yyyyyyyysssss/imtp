import { Button, Center, Input, Pressable, VStack,Image } from 'native-base';
import React, { useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';

const Friend = () => {
    const navigation = useNavigation();

    const toFriendItem = () => {
        navigation.navigate('FriendItem')
    }


    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Search/>
                <Center>
                    <Button onPress={toFriendItem}>This is Friend</Button>
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

export default Friend;