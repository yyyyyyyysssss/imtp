import { Button, Center, Input, Pressable, VStack } from 'native-base';
import React, { useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';

const Group = () => {

    const navigation = useNavigation();

    const toGroupItem = () => {
        navigation.navigate('GroupItem')
    }


    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Center style={styles.searchCenter}>
                    <Input
                        borderWidth="0"
                        backgroundColor='white'
                        size='lg'
                        shadow={1}
                        InputLeftElement={
                            <Pressable style={{ marginLeft: 8 }}>
                                <AntDesignIcon name='search1' size={18} color="gray" />
                            </Pressable>
                        }
                        placeholder='搜索'
                    />
                </Center>
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