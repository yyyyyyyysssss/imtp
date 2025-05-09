import './App.css';
import router from './router/router';
import { Provider } from 'react-redux';
import store from './redux/store';
import { RouterProvider } from 'react-router-dom';
import { useEffect } from 'react';
import { clearToken } from './router/AuthProvider';

function App() {

  useEffect(() => {
    if (window.electronAPI) {
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
