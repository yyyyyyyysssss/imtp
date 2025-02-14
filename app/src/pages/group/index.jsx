import { FlatList, Pressable, VStack } from 'native-base';
import React, { useEffect, useCallback, useContext } from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import UserGroupItem, { UserGroupItemFooter, UserGroupItemSeparator } from '../../components/UserGroupItem';
import { useDispatch, useSelector } from 'react-redux';
import { loadUserGroup, addSession } from '../../redux/slices/chatSlice';
import { DeliveryMethod } from '../../enum';
import { createUserSession } from '../../api/ApiService';

const Group = () => {

    const navigation = useNavigation();

    const sessions = useSelector(state => state.chat.entities.sessions)

    const userGroupResult = useSelector(state => state.chat.userGroups.result)
    const userGroups = useSelector(state => state.chat.userGroups.entities.groups)

    const dispatch = useDispatch()

    const userInfo = useSelector(state => state.auth.userInfo)

    const toChatItem = async (groupId) => {
        const item = userGroups[groupId]
        let sessionId;
        const session = Object.values(sessions).find(s => s.receiverUserId === groupId);
        if (session) {
            sessionId = session.id
        } else {
            sessionId = await createUserSession(groupId, DeliveryMethod.GROUP)
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
            <UserGroupItemSeparator />
        )
    }, [])

    const renderItem = ({ item, index }) => {

        return (
            <Pressable
                onPress={() => toChatItem(item)}
            >
                {({ isHovered, isFocused, isPressed }) => {
                    return (
                        <UserGroupItem groupId = {item} isPressed={isPressed}/>
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
                    data={userGroupResult}
                    renderItem={renderItem}
                    ItemSeparatorComponent={itemSeparator}
                    ListFooterComponent={<UserGroupItemFooter />}
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