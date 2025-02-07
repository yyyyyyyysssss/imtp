import React, { useEffect, useMemo, useState } from 'react';
import { createStaticNavigation, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Splash from './pages/Splash';
import Login from './pages/login';
import ChatItem from './pages/chat/chat-item';
import FriendItem from './pages/friend/friend-item';
import Group from './pages/group';
import GroupItem from './pages/group/group-item';
import Feather from 'react-native-vector-icons/Feather';
import { AuthContext, SignInContext, useIsSignedIn, useIsSignedOut, UserInfoContext } from './context';
import Storage from './storage/storage';
import { useSelector, useDispatch } from 'react-redux'
import { signIn, signOut } from './redux/slices/authSlice';
import Home from './pages/home';
import VideoPlay from './components/VideoPlay';
import { showToast } from './components/Utils';
import { fetchUserInfo, tokenValid } from './api/ApiService';
import { navigationRef } from './RootNavigation';


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
        Group: {
          screen: Group,
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
        },
        AuthLogin: {
          screen: Login,
          options: {
            headerShown: false
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
        }
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
        // 验证token是否有效
        tokenValid(userToken.accessToken)
          .then(
            (data) => {
              const { active, userInfo } = data
              if (!active) {
                dispatch(signOut())
              } else {
                dispatch(signIn({ token: userToken, userInfo: userInfo }))
              }
            },
            (error) => {
              dispatch(signOut())
            }
          )
      } else {
        dispatch(signOut())
      }
    }
    bootstrapAsync()
  }, [])

  const authContext = useMemo(() => ({
    signIn: async (userToken) => {
      //登录之后获取用户信息
      const userInfo = await fetchUserInfo(userToken.accessToken)
      dispatch(signIn({ token: userToken, userInfo: userInfo }))
    },
    signOut: async () => {
      dispatch(signOut())
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
        <Navigation ref={navigationRef} />
      </SignInContext.Provider>
    </AuthContext.Provider>
  )
}

export default App;
