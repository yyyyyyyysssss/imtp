import axios from "axios";
import Cookies from 'js-cookie'
import { message } from "antd"
import router from '../router/router';
import { jwtDecode } from 'jwt-decode'


const httpWrapper = axios.create({
    baseURL: 'http://127.0.0.1:9090',
    timeout: 60000
})

httpWrapper.interceptors.request.use(
    (req) => {
        const token = Cookies.get("accessToken");
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
                Cookies.remove('accessToken');
                Cookies.remove('refreshToken');
                if (error.config.url !== '/login') {
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
