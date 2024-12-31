import { Button, Center, Input, Pressable, VStack, Divider, Box, Text, HStack, Avatar } from 'native-base';
import React, { useEffect, useState, useCallback } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';
import { AlphabetList } from "react-native-section-alphabet-list";
import api from '../../api/api';

const alphabet = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

const Friend = () => {
    const navigation = useNavigation();

    const [userFriends, setUserFriends] = useState([])

    useEffect(() => {
        const fetchData = async () => {
            api.get('/social/userFriend/{userId}')
                .then(
                    (res) => {
                        const userFriends = res.data
                        if (userFriends) {
                            for (let userFriend of userFriends) {
                                userFriend.key = userFriend.id
                                let notePinyin;
                                if((notePinyin = userFriend.notePinyin) && notePinyin.charAt(0) !== -1){
                                    userFriend.value = notePinyin
                                }else {
                                    userFriend.value = '#'
                                }
                            }
                            setUserFriends(userFriends)
                        }

                    }
                )
        }
        fetchData()
    }, [])

    const toFriendItem = () => {
        navigation.navigate('FriendItem')
    }


    const itemSeparator = useCallback(() => {
        return (
            <HStack flex={1} space={5} alignItems='flex-end' justifyContent='center' style={styles.customItem}>
                <VStack flex={1}>

                </VStack>
                <VStack flex={6}>
                    <Divider style={styles.userFriendListItemSeparatorDivider} />
                </VStack>
            </HStack>
        )
    }, [])

    const renderSectionHeader = (section) => {
        return (
            <VStack height={10} justifyContent='space-between' style={styles.sectionHeader}>
                <Divider style={styles.userFriendListItemSeparatorDivider} />
                <Text>{section.title}</Text>
            </VStack>
        )
    }

    const renderItem = (item) => {

        return (
            <HStack flex={1} space={5} alignItems='center' style={styles.customItem}>
                <VStack flex={1} justifyContent='center'>
                    <Avatar
                        size="60px"
                        _image={{
                            borderRadius: 8
                        }}
                        source={{ uri: item.avatar }}
                    />
                </VStack>

                <VStack flex={6} justifyContent='center' style={styles.customItemVStack}>
                    <Text style={styles.customItemVStackText}>
                        {item.note}
                    </Text>
                </VStack>
            </HStack>
        )
    }


    return (
        <>
            <VStack style={styles.rootVStack} space={3}>
                <Search />
                <AlphabetList
                    scrollEnabled={true}
                    style={styles.alphabetList}
                    data={userFriends}
                    indexLetterStyle={{
                        color: 'black',
                        fontSize: 12,
                    }}
                    indexLetterContainerStyle={{
                        margin: 4
                    }}
                    indexContainerStyle={{
                        marginRight: 6,
                        marginTop: '-60%',
                        zIndex: 1000,
                    }}
                    renderCustomItem={renderItem}
                    ItemSeparatorComponent={itemSeparator}
                    renderCustomSectionHeader={renderSectionHeader}
                    ListFooterComponent={(
                        <Divider style={styles.userFriendListItemSeparatorDivider} />
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
        width: '100%',
        height: '100%'
    },
    customItem: {
        padding: 10,
        paddingRight: 0
    },
    customItemVStack: {
        width: '100%',
        height: '100%'
    },
    customItemVStackText: {
        fontSize: 16
    },
    userFriendListItemSeparatorDivider: {
        height: 1,
        backgroundColor: '#D3D3D3',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
    },
    sectionHeader: {
        paddingLeft: 10,
    }
})

export default Friend;