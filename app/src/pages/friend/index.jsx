import { Pressable, Image, VStack, Divider, Box, Text, HStack } from 'native-base';
import React, { useEffect, useState, useCallback, useRef, forwardRef, useImperativeHandle } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';
import { AlphabetList } from "react-native-section-alphabet-list";
import UserFriendItem, { UserFriendItemFooter, UserFriendItemSeparator } from '../../components/UserFriendItem';
import { useDispatch, useSelector } from 'react-redux';
import { loadUserFriend, loadUserGroup } from '../../redux/slices/chatSlice';
import { fetchUserFriends, fetchUserGroups } from '../../api/ApiService';

const alphabet = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

const Friend = forwardRef((props, ref) => {
    const navigation = useNavigation();

    const userFriends = useSelector(state => state.chat.userFriends)
    const userGroups = useSelector(state => state.chat.userGroups)

    const userFriendMapRef = useRef(new Map())
    const userGroupMapRef = useRef(new Map())

    const dispatch = useDispatch()

    const fetchUserFriendData = async () => {
        if (userFriends && userFriends.length) {
            return userFriends
        }
        try {
            const userFriendList = await fetchUserFriends() || []
            for (let userFriend of userFriendList) {
                userFriend.key = userFriend.id
                let notePinyin;
                if ((notePinyin = userFriend.notePinyin) && alphabet.indexOf(notePinyin.charAt(0)) !== -1) {
                    userFriend.value = notePinyin
                } else {
                    userFriend.value = '#'
                }
            }
            dispatch(loadUserFriend(userFriendList))
            return userFriendList
        } catch (error) {
            console.error('Error fetching user friends:', error);
            return []
        }
    }

    const fetchUserGroupData = async () => {
        if (userGroups && userGroups.length) {
            return userGroups
        }
        try {
            const userGroupList = await fetchUserGroups() || []
            dispatch(loadUserGroup(userGroupList))
            return userGroupList
        } catch (error) {
            console.error('Error fetching user groups:', error);
            return []
        }
    }

    useEffect(() => {
        fetchUserFriendData()
        fetchUserGroupData()
    }, [])

    useEffect(() => {
        userFriendMapRef.current = new Map()
        userFriends.forEach(item => userFriendMapRef.current.set(item.id, item))
    }, [userFriends])

    useEffect(() => {
        userGroupMapRef.current = new Map()
        for (let userGroup of userGroups) {
            const groupId = userGroup.id
            userGroupMapRef.current.set(groupId, {id: groupId,note: userGroup.note,avatar: userGroup.avatar})
            const { groupUserInfos } = userGroup
            groupUserInfos.forEach(item => userGroupMapRef.current.set(groupId + '-' + item.id, item))
        }
    }, [userGroups])

    useImperativeHandle(ref, () => ({
        findFriendByFriendId: findFriendByFriendId,
        findGroupByGroupId: findGroupByGroupId,
        findFriendByGroupIdAndFriendId: findFriendByGroupIdAndFriendId
    }))

    const findFriendByFriendId = (friendId) => {

        return userFriendMapRef.current.get(friendId)
    }

    const findGroupByGroupId = (groupId) => {

        return userGroupMapRef.current.get(groupId)
    }

    const findFriendByGroupIdAndFriendId = (groupId, friendId) => {
        const key = groupId + '-' + friendId
        return userGroupMapRef.current.get(key)
    }

    const toFriendItem = (item) => {
        navigation.navigate('FriendItem', {
            friendItem: item
        })
    }

    const toGroupItem = () => {
        navigation.navigate('Group')
    }


    const itemSeparator = useCallback(() => {
        return (
            <UserFriendItemSeparator />
        )
    }, [])

    const renderSectionHeader = (section) => {
        return (
            <VStack justifyContent='space-between' style={styles.sectionHeader}>
                <Divider style={styles.userFriendListItemSeparatorDivider} />
                <Box height={4} />
                <Text>{section.title}</Text>
            </VStack>
        )
    }

    const renderItemHeader = () => {

        return (
            <Pressable
                onPress={() => toGroupItem()}
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
                            avatar={item.avatar}
                            name={item.note}
                            isPressed={isPressed}
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
                        margin: 4,
                    }}
                    indexContainerStyle={{
                        marginRight: 6,
                        marginTop: '-60%',
                        zIndex: 1000
                    }}
                    renderCustomItem={renderItem}
                    ItemSeparatorComponent={itemSeparator}
                    renderCustomSectionHeader={renderSectionHeader}
                    renderCustomListHeader={renderItemHeader}
                    ListFooterComponent={<UserFriendItemFooter />}
                />
            </VStack>
        </>
    )
})



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
        height: '100%',
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
        paddingLeft: 10
    },
    renderItemHeaderBox: {
        backgroundColor: '#70BFFF',
        padding: 10,
        borderRadius: 8
    }
})

export default Friend;