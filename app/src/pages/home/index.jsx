import { NativeBaseProvider, Text } from 'native-base';
import { createStaticNavigation,NavigationContainer } from '@react-navigation/native';
import React, { useState } from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

const Tab = createBottomTabNavigator();

const RootStack = createNativeStackNavigator()

const Home = () => {

    return (
        <NavigationContainer>
            <RootStack.Navigator initialRouteName='Home'>
                <RootStack.Screen
                    name='Login'
                    component={Login}
                    options={{
                        headerShown: false
                    }}
                />
                <RootStack.Screen
                    name='Home'
                    component={HomeTab}
                    options={{
                        headerShown: false
                    }}
                />
                <RootStack.Screen
                    name='ChatItem'
                    component={ChatItem}
                    options={{
                        title: '张三',
                        headerTitleAlign: 'center',
                        headerTitleStyle: {
                            fontWeight: 'bold'
                        },
                        headerRight: () => (
                            <Feather name='more-horizontal' size={28} />
                        )
                    }}
                />
                <RootStack.Screen
                    name='FriendItem'
                    component={FriendItem}
                    options={{
                        title: '',
                        headerRight: () => (
                            <Feather name='more-horizontal' size={28} />
                        )
                    }}
                />
                <RootStack.Screen
                    name='GroupItem'
                    component={GroupItem}
                    options={{
                        title: '',
                        headerRight: () => (
                            <Feather name='more-horizontal' size={28} />
                        )
                    }}
                />
            </RootStack.Navigator>
        </NavigationContainer>
    )
};

export default Home;

