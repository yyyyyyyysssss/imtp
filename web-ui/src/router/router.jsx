import { lazy } from "react";
import { Navigate,createBrowserRouter } from 'react-router-dom';
import AuthProvider from "./AuthProvider";
import LoginProvider from "./LoginProvider.jsx";
import { WebSocketProvider } from '../context'
const Login = lazy(() => import('../pages/login'));
const Home = lazy(() => import('../pages/home'));
const Consent = lazy(() => import('../pages/consent'));
const Activate = lazy(() => import('../pages/activate'));
const Activated = lazy(() => import('../pages/activate/activated'));

const routes = [
    {
        path: "/",
        element: <Navigate to="/home" />,
        protected: false
    },
    {
        path: "/login",
        element: <LoginProvider><Login/></LoginProvider>,
        protected: false
    },
    {
        path: "/consent",
        element: <Consent />,
        protected: true
    },
    {
        path: "/activate",
        element: <Activate />,
        protected: true
    },
    {
        path: "/activated",
        element: <Activated />,
        protected: true
    },
    {
        path: "/home",
        element: <WebSocketProvider><Home /></WebSocketProvider>,
        protected: true
    }
]


const finalRoutes = routes.map((route) => {
    return {
        ...route,
        element: route.protected ? (<AuthProvider>{route.element}</AuthProvider>) : route.element
    }
})

const router = createBrowserRouter(finalRoutes)

export default router;