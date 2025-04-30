import axios from "axios";
import { message } from "antd"
import router from '../router/router';
import { jwtDecode } from 'jwt-decode'
import env from "../env";
import { clearToken, getToken } from "../router/AuthProvider";


const httpWrapper = axios.create({
    baseURL: env.apiUrl,
    timeout: 60000
})

httpWrapper.interceptors.request.use(
    (req) => {
        const token = getToken()
        if (token) {
            const path = req.url;
            //用户id路径参数解析
            if(path.includes("{userId}")){
                const tokenInfo = jwtDecode(token);
                req.url = path.replaceAll("{userId}",tokenInfo.subject);
            }
            req.headers['Authorization'] = `Bearer ${token}`
        }
        return req;
    },
    (error) => {
        Promise.reject(error);
    }
)


httpWrapper.interceptors.response.use(
    (res) => {
        if (res.status < 200 || res.status > 300) {
            message.error(res.msg)
            return Promise.reject(new Error(res.msg) || 'Unknown Error');
        } else {
            return res.data;
        }
    },
    (error) => {
        if (error.response) {
            if (error.response.status === 401) {
                clearToken()
                if (error.config.url !== '/login') {
                    if(window.electronAPI){
                        window.electronAPI.logout()
                    }
                    return router.navigate('/login');
                }
            }
            if (error.response.status === 403) {
                return message.error("未经授权的访问");
            }
            if(error.response.status === 500){
                return message.error(error.message);
            }
        } else {
            return message.error(error.message);
        }
        return Promise.reject(error);
    }
)



export default httpWrapper;
