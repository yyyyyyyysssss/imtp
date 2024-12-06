import React, { useEffect, useMemo } from 'react';
import { Image, VStack } from 'native-base';
import { createStaticNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Splash from './pages/Splash';
import Login from './pages/login';
import Chat from './pages/chat';
import ChatItem from './pages/chat/chat-item';
import Friend from './pages/friend';
import FriendItem from './pages/friend/friend-item';
import Group from './pages/group';
import GroupItem from './pages/group/group-item';
import Me from './pages/me';
import Feather from 'react-native-vector-icons/Feather';
import ChatTools from './components/ChatTools';
import { AuthContext, SignInContext, useIsSignedIn, useIsSignedOut } from './context';
import Storage from './storage/storage';
import api from './api/api';
import { useSelector, useDispatch } from 'react-redux'
import { restoreToken, signIn, signOut } from './redux/slices/authSlice';

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
              <Image alt='' color={color} size={size} source={require('./assets/img/chat-icon-50-selected.png')} />
            </VStack>
            :
            <Image alt='' color={color} size={size} source={require('./assets/img/chat-icon-50.png')} />
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
        headerStyle: {
          borderBottomWidth: 0,
          elevation: 0,
          shadowOpacity: 0,
          backgroundColor: '#F5F5F5'
        },
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./assets/img/friend-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./assets/img/friend-icon-50.png')} />
        ),
        headerRight: () => (
          <Image alt='' size={7} mr={4} source={require('./assets/img/add-friend-icon-50.png')} />
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
        headerStyle: {
          borderBottomWidth: 0,
          elevation: 0,
          shadowOpacity: 0,
          backgroundColor: '#F5F5F5'
        },
        tabBarIcon: ({ focused, color, size }) => (
          focused ?
            <Image alt='' color={color} size={size} source={require('./assets/img/group-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./assets/img/group-icon-50.png')} />
        ),
        headerRight: () => (
          <Image alt='' size={7} mr={4} source={require('./assets/img/add-group-icon-50.png')} />
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
            <Image alt='' color={color} size={size} source={require('./assets/img/me-icon-50-selected.png')} />
            :
            <Image alt='' color={color} size={size} source={require('./assets/img/me-icon-50.png')} />
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
            headerShown: false,
            headerTitleAlign: 'center',
            headerTitleStyle: {
              fontWeight: 'bold'
            },
            headerRight: () => (
              <Feather name='more-horizontal' size={28} />
            ),
            animation: 'slide_from_right'
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

  const { userToken, isLoading } = useSelector(state => state.auth)
  const dispatch = useDispatch()

  useEffect(() => {
    const bootstrapAsync = async () => {
      let userToken = await Storage.get('userToken');
      if (userToken) {
        //验证token是否有效
        api.get('/open/tokenValid', {
          params: {
            token: userToken.accessToken,
            tokenType: 'ACCESS_TOKEN'
          }
        }).then(
          (res) => {
            const { active, userInfo } = res.data
            if (!active) {
              Storage.multiRemove(['userToken', 'userInfo'])
            } else {
              Storage.save('userInfo', userInfo)
              dispatch(restoreToken({ token: userToken, userInfo: userInfo }))
            }
          }
        )
      } else {
        dispatch(restoreToken({ token: null }))
      }
    }
    bootstrapAsync()
  }, [])

  const authContext = useMemo(() => ({
    signIn: async (token) => {
      //登录之后获取用户信息
      Storage.save('userToken', token)
        .then(() => {
          api.get('/social/userInfo')
            .then(
              (res) => {
                const userInfo = res.data
                Storage.save('userInfo', userInfo)
                  .then(() => {
                    dispatch(signIn({ token: token, userInfo: userInfo }))
                  })

              }
            )
        })
    },
    signOut: async () => {
      Storage.remove('userToken', 'userInfo')
        .then(() => {
          dispatch(signOut())
        })
    }
  }))

  //未确认用户是否已登录之前显示启动页
  if (isLoading) {
    return (
      <Splash />
    )
  }

  const isSignedIn = userToken != null;

  return (
    <AuthContext.Provider value={authContext}>
      <SignInContext.Provider value={isSignedIn}>
        <Navigation />
      </SignInContext.Provider>
    </AuthContext.Provider>
  )
}

export default App;
