import './App.css';
import router from './router/router';
import { Provider } from 'react-redux';
import store from './redux/store';
import { RouterProvider } from 'react-router-dom';
import { useEffect } from 'react';
import { clearToken, getToken } from './router/AuthProvider';
import { tokenValid } from './api/ApiService';

function App() {

  useEffect(() => {
    const checkElectronLogin = async () => {
      const token = getToken()
      if (token) {
        const valid = await tokenValid(token)
        if (valid.active === true) {
          window.electronAPI.loginSuccess()
        }
      }
    }
    if (window.electronAPI) {
      window.electronAPI.checkLogin(() => {
        checkElectronLogin()
      })
      window.electronAPI.onQuit(() => {
        clearToken()
      })
    }
  }, [])

  return (
    <Provider store={store}>
      <RouterProvider 
        router={router} 
        future={{
          v7_startTransition: true,
          v7_relativeSplatPath: true
        }}
      />
    </Provider>
  );
}

export default App;
