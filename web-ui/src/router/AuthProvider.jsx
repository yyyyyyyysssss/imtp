import { Navigate } from 'react-router-dom';
import Cookies from 'js-cookie'
import { jwtDecode } from 'jwt-decode'
import { tokenValid } from '../api/ApiService';
import { useEffect, useState } from 'react';

export const isLoginIn = () => {

    let isLoginIn = false;
    const token = Cookies.get("accessToken");
    if (token) {
        const tokenInfo = jwtDecode(token);
        const expiration = tokenInfo.expiration;
        if (Date.now() < expiration) {
            isLoginIn = true;
        }
    }
    return isLoginIn;
}


export const saveToken = (tokenInfo) => {
    Cookies.set('accessToken', tokenInfo.accessToken)
    Cookies.set('refreshToken', tokenInfo.refreshToken)
    if (tokenInfo.rememberMeToken) {
        localStorage.setItem('rememberMeToken', tokenInfo.rememberMeToken)
    }
    if (window.electronAPI) {
        localStorage.setItem('accessToken', tokenInfo.accessToken)
        localStorage.setItem('refreshToken', tokenInfo.refreshToken)
    }
}

export const getToken = () => {
    let token = Cookies.get("accessToken");
    if (token) {
        return token
    }
    return localStorage.getItem('accessToken')
}

export const clearToken = () => {
    Cookies.remove('accessToken');
    Cookies.remove('refreshToken');
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
}

const AuthProvider = ({ children }) => {
    const [loginFlag, setLoginFlag] = useState(null)

    useEffect(() => {
        const checkLogin = async () => {
            const token = getToken()
            if (token) {
                const valid = await tokenValid(token)
                setLoginFlag(valid.active)
            } else {
                setLoginFlag(false)
            }
        }
        checkLogin()
    }, [])

    if (loginFlag === null) {
        return <></>
    }
    if (loginFlag === false) {
        return <Navigate to='/login' replace={true} />
    }
    return children;
}


export default AuthProvider;