import { Button, Center, Input, Pressable, VStack, Image, Box, Text } from 'native-base';
import React, { useState } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';
import { AlphabetList } from "react-native-section-alphabet-list";
import { tr } from 'rn-emoji-keyboard';

const data = [
    { value: 'Lillie-Mai Allen', key: 'lCUTs2' },
    { value: 'Emmanuel Goldstein', key: 'TXdL0c' },
    { value: 'Winston Smith', key: 'zqsiEw' },
    { value: 'William Blazkowicz', key: 'psg2PM' },
    { value: 'Gordon Comstock', key: '1K6I18' },
    { value: 'Philip Ravelston', key: 'NVHSkA' },
    { value: 'Rosemary Waterlow', key: 'SaHqyG' },
    { value: 'Julia Comstock', key: 'iaT1Ex' },
    { value: 'Mihai Maldonado', key: 'OvMd5e' },
    { value: 'Murtaza Molina', key: '25zqAO' },
    { value: 'Peter Petigrew', key: '8cWuu3' },
]

const Friend = () => {
    const navigation = useNavigation();

    const toFriendItem = () => {
        navigation.navigate('FriendItem')
    }


    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Search />
                <AlphabetList
                    scrollEnabled={true}
                    style={styles.alphabetList}
                    data={data}
                    indexLetterStyle={{
                        color: 'black',
                        fontSize: 12,
                    }}
                    indexLetterContainerStyle={{
                        margin: 5
                    }}
                    indexContainerStyle={{
                        marginRight: 6,
                    }}
                    renderCustomItem={(item) => (
                        <Box style={styles.customItem}>
                            <Text>{item.value}</Text>
                        </Box>
                    )}
                    renderCustomSectionHeader={(section) => (
                        <Box style={styles.sectionHeader}>
                            <Text>{section.title}</Text>
                        </Box>
                    )}
                />
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
    },
    alphabetList: {
        padding: 10,
        width: '100%',
        height: '100%'
    },
    customItem: {
        padding: 10,
    },
    sectionHeader: {
        paddingTop: 10,
        paddingLeft: 10
    }
})

export default Friend;