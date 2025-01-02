import { Button, Pressable, Input, Image, VStack, Divider, Box, Text, HStack, Avatar } from 'native-base';
import React, { useEffect, useState, useCallback } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';
import { AlphabetList } from "react-native-section-alphabet-list";
import api from '../../api/api';
import UserFriendItem, { UserFriendItemFooter, UserFriendItemSeparator } from '../../components/UserFriendItem';

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
                                if ((notePinyin = userFriend.notePinyin) && alphabet.indexOf(notePinyin.charAt(0)) !== -1) {
                                    userFriend.value = notePinyin
                                } else {
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

    const toFriendItem = (item) => {
        navigation.navigate('FriendItem', {
            friendItem: item
        })
    }

    const toGroupItem = (item) => {
        navigation.navigate('Group', {
            friend: item
        })
    }


    const itemSeparator = useCallback(() => {
        return (
            <UserFriendItemSeparator/>
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

    const renderItemHeader = () => {

        return (
            <Pressable
                onPress={() => toGroupItem({})}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <HStack space={5} style={{ backgroundColor: isPressed ? '#C8C6C5' : '#F5F5F5', padding: 10, paddingRight: 0 }}>
                            <Box style={styles.renderItemHeaderBox}>
                                <Image alt='' size={35} source={require('../../assets/img/friend-icon-50-white.png')} />
                            </Box>
                            <VStack justifyContent='center'>
                                <Text style={styles.customItemVStackText}>
                                    群聊
                                </Text>
                            </VStack>
                        </HStack>
                    )
                }}
            </Pressable>

        )
    }

    const renderItem = (item) => {

        return (
            <Pressable
                onPress={() => toFriendItem(item)}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <UserFriendItem
                            avatar = {item.avatar}
                            name = {item.note}
                            isPressed = {isPressed}
                        />
                    )
                }}
            </Pressable>
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
                    renderCustomListHeader={renderItemHeader}
                    ListFooterComponent={<UserFriendItemFooter/>}
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
        backgroundColor: '#D3D3D3'
    },
    sectionHeader: {
        paddingLeft: 10,
    },
    renderItemHeaderBox: {
        backgroundColor: '#70BFFF',
        padding: 10,
        borderRadius: 8
    }
})

export default Friend;