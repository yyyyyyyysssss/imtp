import { Avatar, Button, Center, FlatList, HStack, Input, Pressable, Text, VStack } from 'native-base';
import React, { useEffect, useState,useCallback } from 'react';
import { useNavigation, } from '@react-navigation/native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { StyleSheet } from 'react-native';
import Search from '../../components/Search';
import api from '../../api/api';
import UserFriendItem, { UserFriendItemFooter, UserFriendItemSeparator } from '../../components/UserFriendItem';

const Group = () => {



    const [userGroups, setUserGroups] = useState([])

    useEffect(() => {
        const fetchData = async () => {
            api.get('/social/userGroup/{userId}')
                .then(
                    (res) => {
                        const userGroups = res.data
                        if (userGroups) {
                            setUserGroups(userGroups)
                        }

                    }
                )
        }
        fetchData()
    }, [])

    const navigation = useNavigation();

    const toChatItem = (item) => {
        console.log('item',item)
    }

    const itemSeparator = useCallback(() => {
            return (
                <UserFriendItemSeparator/>
            )
        }, [])

    const renderItem = ({ item, index }) => {

        return (
            <Pressable
                onPress={() => toChatItem(item.id)}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <UserFriendItem
                            avatar = {item.avatar}
                            name = {item.groupName}
                            isPressed = {isPressed}
                        />
                    )
                }}
            </Pressable>
        )
    }


    return (
        <>
            <VStack flex={1} style={styles.rootVStack}>
                <FlatList
                    style={{
                        padding: 10,
                        paddingRight: 0
                    }}
                    data={userGroups}
                    renderItem={renderItem}
                    ItemSeparatorComponent={itemSeparator}
                    ListFooterComponent={<UserFriendItemFooter/>}
                />
            </VStack>
        </>
    )
}

const styles = StyleSheet.create({
    rootVStack: {
        backgroundColor: '#F5F5F5'
    }
})

export default Group;