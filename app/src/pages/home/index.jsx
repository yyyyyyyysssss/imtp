import { Image } from 'native-base';
import React, { useRef, useState } from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import ChatTools from '../../components/ChatTools';
import Chat from '../chat';
import Friend from '../friend';
import Me from '../me';
import Notification from '../../components/Notification';

const Tab = createBottomTabNavigator();

const Home = () => {

    const userFriendRef = useRef()

    const findFriendByFriendId = (friendId) => {

        return userFriendRef.current.findFriendByFriendId(friendId)
    }

    const findGroupByGroupId = (groupId) => {

        return userFriendRef.current.findGroupByGroupId(groupId)
    }

    const findFriendByGroupIdAndFriendId = (groupId, friendId) => {

        return userFriendRef.current.findFriendByGroupIdAndFriendId(groupId, friendId)
    }

    return (
        <Tab.Navigator
            initialRouteName='Chat'
        >
            <Tab.Screen
                name='Chat'
                children={() => (
                    <Chat
                        findFriendByFriendId={findFriendByFriendId}
                        findGroupByGroupId={findGroupByGroupId}
                        findFriendByGroupIdAndFriendId={findFriendByGroupIdAndFriendId}
                    />
                )}
                options={{
                    title: '聊天',
                    lazy: false,
                    headerTitleAlign: 'center',
                    headerTitleStyle: {
                        fontWeight: 'bold'
                    },
                    headerStyle: {
                        borderBottomWidth: 0,
                        elevation: 0,
                        shadowOpacity: 0,
                        backgroundColor: '#F5F5F5'
                    },
                    tabBarIcon: ({ focused, color, size }) => (
                        <Notification size={22}>
                            {
                                focused ?
                                    <Image alt='' color={color} size={size} source={require('../../assets/img/chat-icon-50-selected.png')} />
                                    :
                                    <Image alt='' color={color} size={size} source={require('../../assets/img/chat-icon-50.png')} />
                            }
                        </Notification>
                    ),
                    headerRight: () => <ChatTools />
                }}
            />
            <Tab.Screen
                name='Friend'
                children={() => (
                    <Friend ref={userFriendRef} />
                )}
                options={{
                    lazy: false,
                    title: '好友',
                    headerTitleAlign: 'center',
                    headerTitleStyle: {
                        fontWeight: 'bold',
                    },
                    headerStyle: {
                        borderBottomWidth: 0,
                        elevation: 0,
                        shadowOpacity: 0,
                        backgroundColor: '#F5F5F5'
                    },
                    tabBarIcon: ({ focused, color, size }) => (
                        focused ?
                            <Image alt='' color={color} size={size} source={require('../../assets/img/friend-icon-50-selected.png')} />
                            :
                            <Image alt='' color={color} size={size} source={require('../../assets/img/friend-icon-50.png')} />
                    ),
                    headerRight: () => (
                        <Image alt='' size={7} mr={4} source={require('../../assets/img/add-friend-icon-50.png')} />
                    )
                }}
            />
            <Tab.Screen
                name='Me'
                children={() => (
                    <Me />
                )}
                options={{
                    title: '我',
                    lazy: false,
                    headerShown: false,
                    tabBarIcon: ({ focused, color, size }) => (
                        focused ?
                            <Image alt='' color={color} size={size} source={require('../../assets/img/me-icon-50-selected.png')} />
                            :
                            <Image alt='' color={color} size={size} source={require('../../assets/img/me-icon-50.png')} />
                    )
                }}
            />
        </Tab.Navigator>
    )
};

export default Home;

