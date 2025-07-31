import axios from "axios";
import { showToast } from "../components/Utils";
import Storage from "../storage/storage";
import global from "../../global";
import reduxStore from "../redux/store";
import { authSlice } from "../redux/slices/authSlice";


const api = axios.create({
    baseURL: global.apiUrl,
    timeout: 60000
})

api.interceptors.request.use(
    async (req) => {
        const { userToken, userInfo } = await Storage.multiGet(['userToken', 'userInfo'])
        if (userToken) {
            const path = req.url;
            //用户id路径参数解析
            if (path.includes("{userId}")) {
                req.url = path.replaceAll("{userId}", userInfo.id);
            }
            const { accessToken } = userToken
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
        if (!error.response) {
            message.error(error.message || '网络错误');
            return Promise.reject(error);
        }
        const { status, message: errorMessage, config } = error.response;
        switch (status) {
            case 401:
                if (status === 401 && config.url != '/login' && config.url != '/logout') {
                    reduxStore.dispatch(authSlice.actions.signOut())
                }
                break
            case 403:
                showToast("未经授权的访问");
                break
            case 500:
                showToast('服务器内部错误');
                break
            default:
                showToast(errorMessage || '未知错误');
        }
        return Promise.reject(error)
    }
)

export default api