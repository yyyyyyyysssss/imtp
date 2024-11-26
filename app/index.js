/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import Login from './src/pages/login';
import {name as appName} from './app.json';

AppRegistry.registerComponent(appName, () => Login);
