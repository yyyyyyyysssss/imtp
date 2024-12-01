import { NativeBaseProvider, Text } from 'native-base';
import { createStaticNavigation } from '@react-navigation/native';
import React, { useState } from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Chat from '../chat';
import Friend from '../friend';
import Group from '../group';
import Me from '../me';


const BottomTabNavigator = createBottomTabNavigator({
    initialRouteName: 'Chat',
    screens: {
        Chat: {
            screen: Chat,
            options: {
                title: '聊天'
            }
        },
        Friend: {
            screen: Friend,
            options: {
                title: '好友'
            }
        },
        Group: {
            screen: Group,
            options: {
                title: '群组'
            }
        },
        Me: {
            screen: Me,
            options: {
                title: '我'
            }
        }
    }
})

const Home = BottomTabNavigator;

export default Home;

