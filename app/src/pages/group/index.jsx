import { FlatList, Pressable, VStack } from 'native-base';
import React, { useEffect, useCallback } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import api from '../../api/api';
import UserFriendItem, { UserFriendItemFooter, UserFriendItemSeparator } from '../../components/UserFriendItem';
import { useDispatch, useSelector } from 'react-redux';
import { loadUserGroup } from '../../redux/slices/chatSlice';

const Group = () => {

    const navigation = useNavigation();

    const sessions = useSelector(state => state.chat.entities.sessions)

    const userGroups = useSelector(state => state.chat.userGroups)

    const dispatch = useDispatch()

    useEffect(() => {
        const fetchData = async () => {
            api.get('/social/userGroup/{userId}')
                .then(
                    (res) => {
                        const userGroupList = res.data
                        if (userGroupList) {
                            dispatch(loadUserGroup(userGroupList))
                        }

                    }
                )
        }
        if (!userGroups.length) {
            fetchData()
        }
    }, [])

    const toChatItem = (item) => {
        console.log('item', item)
    }

    const itemSeparator = useCallback(() => {
        return (
            <UserFriendItemSeparator />
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
                            avatar={item.avatar}
                            name={item.groupName}
                            isPressed={isPressed}
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
                    ListFooterComponent={<UserFriendItemFooter />}
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