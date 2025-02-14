import React, { useEffect, useMemo, useState } from 'react';
import { createStaticNavigation, getStateFromPath, NavigationContainer, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Splash from './pages/Splash';
import Login from './pages/login';
import ChatItem from './pages/chat/chat-item';
import FriendItem from './pages/friend/friend-item';
import Group from './pages/group';
import Feather from 'react-native-vector-icons/Feather';
import { AuthContext, SignInContext, useIsSignedIn, useIsSignedOut, UserInfoContext } from './context';
import Storage from './storage/storage';
import { useSelector, useDispatch } from 'react-redux'
import { signIn, signOut } from './redux/slices/authSlice';
import Home from './pages/home';
import VideoPlay from './components/VideoPlay';
import { fetchUserInfo, tokenValid } from './api/ApiService';
import { navigate, navigationRef } from './RootNavigation';
import NotFound from './pages/NotFound';


const RootStack = createNativeStackNavigator({
  groups: {
    // 登录情况下可以访问的页面
    SignedIn: {
      if: useIsSignedIn,
      screens: {
        Home: {
          screen: Home,
          linking: {
            path: 'home'
          },
          options: {
            headerShown: false
          }
        },
        ChatItem: {
          screen: ChatItem,
          linking: {
            path: 'chatItem/:sessionId?',
            parse: {
              sessionId: (sessionId) => sessionId.replace(/^@/, ''),
            }
          },
          options: {
            headerShown: false,
            animation: 'slide_from_right',
            gestureEnabled: true
          }
        },
        FriendItem: {
          screen: FriendItem,
          linking: {
            path: 'friendItem/:friendId?',
            parse: {
              friendId: (friendId) => friendId.replace(/^@/, ''),
            }
          },
          options: {
            title: '',
            headerRight: () => (
              <Feather name='more-horizontal' size={28} />
            )
          }
        },
        Group: {
          screen: Group,
          linking: {
            path: 'group'
          },
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
          screen: Login,
          linking: {
            path: 'login'
          },
          options: {
            headerShown: false
          }
        }
      }
    }
  },
  screens: {
    NotFound: {
      screen: NotFound
    }
  }
})

// 深度链接
const linking = {
  // 自动为所有屏幕生成路径
  enabled: 'auto',
  prefixes: ['ychat://', 'https://ychat.com', 'http://ychat.com'],
  config: {

  }
}

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
              const { active } = data
              if (!active) {
                logout()
              } else {
                login(userToken)
              }
            },
            (error) => {
              logout()
            }
          )
      } else {
        logout()
      }
    }
    bootstrapAsync()
  }, [])

  const login = async (userToken) => {
    //登录之后获取用户信息
    const userInfo = await fetchUserInfo(userToken.accessToken)
    dispatch(signIn({ token: userToken, userInfo: userInfo }))
  }

  const logout = async () => {
    dispatch(signOut())
  }

  const authContext = useMemo(() => ({
    signIn: login,
    signOut: logout
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
        <Navigation linking={linking} ref={navigationRef}/>
      </SignInContext.Provider>
    </AuthContext.Provider>
  )
}

export default App;
