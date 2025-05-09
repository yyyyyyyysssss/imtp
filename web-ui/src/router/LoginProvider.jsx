import { Navigate, useSearchParams } from 'react-router-dom';
import React, { useEffect } from 'react'
import Cookies from 'js-cookie'
import { getToken } from './AuthProvider'
import { useState } from 'react';
import { tokenValid } from '../api/ApiService';
import LoggingIn from '../components/logging-in';


const AuthenticatedProvider = ({ children }) => {

    const [loginFlag, setLoginFlag] = useState(null)

    //路由参数
    const [params] = useSearchParams();
    useEffect(() => {
        const checkLogin = async () => {
            const token = getToken()
            let loginIn  = false
            if (token) {
                const valid = await tokenValid(token)
                setLoginFlag(valid.active)
                loginIn = valid.active
            } else {
                setLoginFlag(false)
            }
            // oauth授权码登录跳转
            if (params.get('target') && loginIn) {
                const token = Cookies.get("accessToken");
                const target = params.get('target') + '&access_token=' + token;
                window.location.href = target;
            }
        }
        checkLogin()
    }, [])


    if(loginFlag === null){
        if(window.electronAPI){
            return <LoggingIn/>
        }
        return <></>
    }
    if(loginFlag === true){
        if(window.electronAPI){
            window.electronAPI.loginSuccess()
            return
        }
    }
    //已登录情况下且不是oauth2回调的访问直接跳转主页
    return (!params.get('code') && loginFlag === true) ? <Navigate to='/home' replace={true} /> : children;
}


export default AuthenticatedProvider;