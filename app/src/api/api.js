import axios from "axios";
import { showToast } from "../components/Utils";
import Storage from "../storage/storage";
import global from "../../global";
import { navigate } from "../RootNavigation";


const api = axios.create({
    baseURL: global.apiUrl,
    timeout: 60000
})

api.interceptors.request.use(
    async (req) => {
        const {userToken,userInfo} = await Storage.multiGet(['userToken','userInfo'])
        if(userToken){
            const path = req.url;
            //用户id路径参数解析
            if(path.includes("{userId}")){
                req.url = path.replaceAll("{userId}",userInfo.id);
            }
            const {accessToken} = userToken
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
                // navigate('AuthLogin')
            }
            if (error.response.status === 403) {
                return showToast("无权限访问");
            }
            if (error.response.status === 500) {
                showToast(error.message)
                return Promise.reject(error)
            }
        }
        return Promise.reject(error)
    }
)

export default api