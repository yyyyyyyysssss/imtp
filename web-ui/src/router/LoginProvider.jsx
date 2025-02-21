import { Navigate,useSearchParams } from 'react-router-dom';
import React, { useEffect } from 'react'
import Cookies from 'js-cookie'
import {isLoginIn} from './AuthProvider'


const AuthenticatedProvider = ({children }) => {
    //路由参数
    const [params] = useSearchParams();
    useEffect(() => {
        // oauth授权码登录跳转
        if(params.get('target') && isLoginIn()){
            console.log(params.get('target'))
            const token = Cookies.get("accessToken");
            const target = params.get('target') + '&access_token=' + token ;
            console.log(target)
            window.location.href = target;
        }
    },[])
    //已登录情况下且不是oauth2回调的访问直接跳转主页
    return (!params.get('code') && isLoginIn()) ? <Navigate to='/home' replace={true} /> : children;
}


export default AuthenticatedProvider;