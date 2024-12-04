import React, { useEffect, useMemo, useState } from 'react';
import { NativeBaseProvider, Image, VStack } from 'native-base';
import { createStaticNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Splash from './src/pages/Splash';
import Login from './src/pages/login';
import Chat from './src/pages/chat';
import ChatItem from './src/pages/chat/chat-item';
import Friend from './src/pages/friend';
import FriendItem from './src/pages/friend/friend-item';
import Group from './src/pages/group';
import GroupItem from './src/pages/group/group-item';
import Me from './src/pages/me';
import Feather from 'react-native-vector-icons/Feather';
import ChatTools from './src/component/ChatTools';
import { AuthContext, SignInContext, useIsSignedIn, useIsSignedOut } from './src/context';
import Storage from './src/storage/storage';
import { NativeBaseConfigProvider } from 'native-base/lib/typescript/core/NativeBaseContext';

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
        headerRight: () => <ChatTools />
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
  groups: {
    // 登录情况下可以访问的页面
    SignedIn: {
      if: useIsSignedIn,
      screens: {
        Home: {
          if: useIsSignedIn,
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
              <Feather name='more-horizontal' size={28} />
            )
          }
        },
        FriendItem: {
          screen: FriendItem,
          options: {
            title: '',
            headerRight: () => (
              <Feather name='more-horizontal' size={28} />
            )
          }
        },
        GroupItem: {
          screen: GroupItem,
          options: {
            title: '',
            headerRight: () => (
              <Feather name='more-horizontal' size={28} />
            )
          }
        }
      }
    },
    // 未登录情况下可以访问的页面
    SignedOut: {
      if: useIsSignedOut,
      screens: {
        Login: {
          if: useIsSignedOut,
          screen: Login,
          options: {
            headerShown: false
          }
        },
      }
    }
  }
})

const Navigation = createStaticNavigation(RootStack)

const App = () => {

  const [userToken, setUserToken] = useState(null)

  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const bootstrapAsync = async () => {
      let userToken = await Storage.get('userToken');
      if (userToken) {
        setUserToken(userToken)
      }
      setIsLoading(false)
    }
    bootstrapAsync()
  }, [])

  const authContext = useMemo(() => ({
    signIn: async (token) => {
      setUserToken(token)
    },
    signOut: async () => {
      setUserToken(null)
    }
  }))

  //未确认用户是否已登录之前显示启动页
  if (isLoading) {
    return (
      <NativeBaseProvider>
        <Splash />
      </NativeBaseProvider>
    )
  }

  const isSignedIn = userToken != null;

  return (
    <NativeBaseProvider>
      <AuthContext.Provider value={authContext}>
        <SignInContext.Provider value={isSignedIn}>
          <Navigation />
        </SignInContext.Provider>
      </AuthContext.Provider>
    </NativeBaseProvider>

  )
}

export default App;
