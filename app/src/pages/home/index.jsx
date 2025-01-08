import { VStack, Image, Badge, Text, Avatar, Box } from 'native-base';
import React, { useState } from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import ChatTools from '../../components/ChatTools';
import Chat from '../chat';
import Friend from '../friend';
import Group from '../group';
import Me from '../me';
import Notification from '../../components/Notification';

const Tab = createBottomTabNavigator();

const Home = () => {

    console.log('Home')

    return (
        <Tab.Navigator 
            initialRouteName='Chat'
        >
            <Tab.Screen
                name='Chat'
                component={Chat}
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
                component={Friend}
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
                component={Me}
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

