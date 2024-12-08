import { Button, Center, Input, Pressable, VStack } from 'native-base';
import React, { useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';

const Group = () => {

    const navigation = useNavigation();

    const toGroupItem = () => {
        navigation.navigate('GroupItem')
    }


    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Search/>
                <Center>
                    <Button onPress={toGroupItem}>This is Group</Button>
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

export default Group;