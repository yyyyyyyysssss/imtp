import { FlatList, Pressable, VStack } from 'native-base';
import React, { useEffect, useCallback, useContext } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import UserFriendItem, { UserFriendItemFooter, UserFriendItemSeparator } from '../../components/UserFriendItem';
import { useDispatch, useSelector } from 'react-redux';
import { loadUserGroup, addSession } from '../../redux/slices/chatSlice';
import { UserInfoContext } from '../../context';
import { DeliveryMethod } from '../../enum';
import { createUserSession, fetchUserGroups } from '../../api/ApiService';

const Group = () => {

    const navigation = useNavigation();

    const sessions = useSelector(state => state.chat.entities.sessions)

    const userGroups = useSelector(state => state.chat.userGroups)

    const dispatch = useDispatch()

    const userInfo = useContext(UserInfoContext)

    const toChatItem = async (item) => {
        const groupId = item.id
        let sessionId;
        const session = Object.values(sessions).find(s => s.receiverUserId === groupId);
        if (session) {
            sessionId = session.id
        } else {
            const createUserSessionReq = {
                receiverUserId: groupId,
                deliveryMethod: DeliveryMethod.GROUP
            }
            sessionId  = await createUserSession(createUserSessionReq)
            const userSessionItem = {
                id: sessionId,
                userId: userInfo.id,
                name: item.groupName,
                receiverUserId: groupId,
                avatar: item.avatar,
                deliveryMethod: DeliveryMethod.GROUP
            }
            dispatch(addSession({ session: userSessionItem }))
        }
        navigation.navigate('ChatItem', {
            sessionId: sessionId,
        })
    }

    const itemSeparator = useCallback(() => {
        return (
            <UserFriendItemSeparator />
        )
    }, [])

    const renderItem = ({ item, index }) => {

        return (
            <Pressable
                onPress={() => toChatItem(item)}
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