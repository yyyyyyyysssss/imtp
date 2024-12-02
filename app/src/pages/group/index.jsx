import { Button } from 'native-base';
import React, { useState } from 'react';
import {useNavigation,} from '@react-navigation/native';

const Group = () => {

    const navigation = useNavigation();

    const toGroupItem = () => {
        navigation.navigate('GroupItem')
    }


    return (
        <>
            <Button onPress={toGroupItem}>This is Group</Button>
        </>
    )
}

export default Group;