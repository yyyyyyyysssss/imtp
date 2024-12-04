import axios from "axios";
import { showToast } from "../component/Utils";
import Storage from "../storage/storage";


const api = axios.create({
    baseURL: 'http://10.0.2.2:9090',
    timeout: 60000
})

api.interceptors.request.use(
    async (req) => {
        const usetToken = await Storage.get('userToken')
        if(usetToken){
            const {accessToken} = usetToken
            req.headers['Authorization'] = `Bearer ${accessToken}`
        }
        return req;
    },
    (error) => {
        return Promise.reject(error)
    }
)

api.interceptors.response.use(
    (res) => {
        if (res.status == 200) {
            return res.data
        }
        return Promise.reject(res)
    },
    (error) => {
        if (error.response) {
            if (error.response.status === 401 && error.response.config.url != '/login' && error.response.config.url != '/logout') {
                return showToast('身份认证失败')
            }
            if (error.response.status === 403) {
                return showToast("无权限访问");
            }
            if (error.response.status === 500) {
                return showToast(error.message);
            }
        }
        return Promise.reject(error)
    }
)

export default api