import { Button } from 'native-base';
import React, { useState } from 'react';
import {useNavigation,} from '@react-navigation/native';

const Friend = () => {
    const navigation = useNavigation();

    const toFriendItem = () => {
        navigation.navigate('FriendItem')
    }


    return (
        <>
            <Button onPress={toFriendItem}>This is Friend</Button>
        </>
    )
}

export default Friend;