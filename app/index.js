/**
 * @format
 */

import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import { Provider } from 'react-redux';
import store from './src/redux/store';
import { NativeBaseProvider } from 'native-base';
import { SafeAreaProvider } from 'react-native-safe-area-context';

AppRegistry.registerComponent(appName, () => {
    return () => (
        <Provider store={store}>
            <NativeBaseProvider>
                <SafeAreaProvider>
                    <App />
                </SafeAreaProvider>
            </NativeBaseProvider>
        </Provider>
    )
});
