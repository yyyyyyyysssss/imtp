/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import { createStaticNavigation } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Login from './src/pages/login';
import Home from './src/pages/home';


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
        title: 'Home',
        headerTitleAlign: 'center'
      }
    }
  }
})

const Navigation = createStaticNavigation(RootStack)

const App = () => {

  return (
    <Navigation/>
  )
}

export default App;
