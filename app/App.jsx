import React from 'react';
import { NativeBaseProvider, Image, VStack, Badge } from 'native-base';
import { createStaticNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Login from './src/pages/login';
import Chat from './src/pages/chat';
import ChatItem from './src/pages/chat/chat-item';
import Friend from './src/pages/friend';
import FriendItem from './src/pages/friend/friend-item';
import Group from './src/pages/group';
import GroupItem from './src/pages/group/group-item';
import Me from './src/pages/me';
import { AddIcon } from './src/component/CustomIcon';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Feather from 'react-native-vector-icons/Feather';

const Home = createBottomTabNavigator({
  initialRouteName: 'Chat',
  screens: {
    Chat: {
      screen: Chat,
      options: {
        title: '聊天',
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
          focused ?
            <VStack>
              <Image alt='' color={color} size={size} source={require('./src/assets/img/chat-icon-50-selected.png')} />
            </VStack>
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/chat-icon-50.png')} />
        ),
        headerRight: () => (
          <Ionicons style={{marginRight: 12}} name="add-circle-outline" size={28}/>
        )
      }
    },
    Friend: {
      screen: Friend,
      options: {
        title: '好友',
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./src/assets/img/friend-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/friend-icon-50.png')} />
        ),
        headerRight: () => (
          <Image alt='' size={7} mr={4} source={require('./src/assets/img/add-friend-icon-50.png')} />
        )
      }
    },
    Group: {
      screen: Group,
      options: {
        title: '群组',
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontWeight: 'bold'
        },
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./src/assets/img/group-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/group-icon-50.png')} />
        ),
        headerRight: () => (
          <Image alt='' size={7} mr={4} source={require('./src/assets/img/add-group-icon-50.png')} />
        )
      }
    },
    Me: {
      screen: Me,
      options: {
        title: '我',
        headerShown: false,
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./src/assets/img/me-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/me-icon-50.png')} />
        )
      }
    }
  }
})

const RootStack = createNativeStackNavigator({
  initialRouteName: 'Home',
  screens: {
    Login: {
      screen: Login,
      options: {
        headerShown: false
      }
    },
    Home: {
      screen: Home,
      options: {
        headerShown: false
      }
    },
    ChatItem: {
      screen: ChatItem,
      options: {
        title: '张三',
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontWeight: 'bold'
        },
        headerRight: () => (
          <Feather name='more-horizontal' size={28}/>
        )
      }
    },
    FriendItem: {
      screen: FriendItem,
      options: {
        title: '',
        headerRight: () => (
          <Feather name='more-horizontal' size={28}/>
        )
      }
    },
    GroupItem: {
      screen: GroupItem,
      options: {
        title: '',
        headerRight: () => (
          <Feather name='more-horizontal' size={28}/>
        )
      }
    }
  }
})

const Navigation = createStaticNavigation(RootStack)

const App = () => {

  return (
    <NativeBaseProvider>
      <Navigation />
    </NativeBaseProvider>

  )
}

export default App;
