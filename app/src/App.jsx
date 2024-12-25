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
import Home from './pages/home';
import VideoPlay from './components/VideoPlay';
import { showToast } from './components/Utils';
import { NativeModules } from 'react-native';

const { NettyClientModule } = NativeModules

const HomeTab = createBottomTabNavigator({
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
            headerShown: false,
            animation: 'slide_from_right',
            gestureEnabled: true
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
        },
        VideoPlay: {
          screen: VideoPlay,
          options: {
            headerShown: false,
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
      console.log('App init')
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
              logoutHandler()
            } else {
              loginSuccessHandler(userToken, userInfo)
            }
          },
          (error) => {
            dispatch(restoreToken({ token: null, userInfo: null }))
          }
        )
      } else {
        dispatch(restoreToken({ token: null, userInfo: null }))
      }
    }
    bootstrapAsync()
  }, [])

  const authContext = useMemo(() => ({
    signIn: async (userToken) => {
      //登录之后获取用户信息
      api.get('/social/userInfo',{
        headers: {
          Authorization: `Bearer ${userToken.accessToken}`
        }
      })
        .then(
          (res) => {
            const userInfo = res.data
            loginSuccessHandler(userToken, userInfo)
          }
        )
    },
    signOut: async () => {
      logoutHandler()
    }
  }))

  const logoutHandler = async () => {
    await Storage.multiRemove(['userToken', 'userInfo'])
    dispatch(signOut())
    NettyClientModule.destroy()
    .then(
      (res) => {
        console.log('NettyClientModule destroy', res ? 'succeed' : 'failed')
      },
      (error) => {
        console.log('NettyClientModule destroy', 'failed',error.message)
      }
    )
  }

  const loginSuccessHandler = async (userToken, userInfo) => {
    await Storage.batchSave({
      userInfo: userInfo,
      userToken: userToken
    })
    dispatch(restoreToken({ token: userToken, userInfo: userInfo }))
    NettyClientModule.init(JSON.stringify(userToken))
    .then(
      (res) => {
        console.log('NettyClientModule init',res ? 'succeed' : 'failed')
      },
      (error) => {
        console.log('NettyClientModule init', 'failed',error.message)
      }
    )
  }

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
