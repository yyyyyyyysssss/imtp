import React from 'react';
import { NativeBaseProvider, Image, VStack, Badge } from 'native-base';
import { createStaticNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Login from './src/pages/login';
import Chat from './src/pages/chat';
import Friend from './src/pages/friend';
import Group from './src/pages/group';
import Me from './src/pages/me';

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
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <VStack>
              {/* <Badge colorScheme="danger" rounded="full" mb={-4} mr={-4} zIndex={1} variant="solid" alignSelf="flex-end" _text={{
                fontSize: 8
              }}>
                2
              </Badge> */}
              <Image alt='' color={color} size={size} source={require('./src/assets/img/chat-icon-50-selected.png')} />
            </VStack>
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/chat-icon-50.png')} />
        ),
      }
    },
    Friend: {
      screen: Friend,
      options: {
        title: '好友',
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontWeight: 'bold'
        },
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./src/assets/img/friend-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./src/assets/img/friend-icon-50.png')} />
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
        headerShown: false,
        headerTitleAlign: 'center'
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
