import { showToast } from "../components/Utils"
import api from "./api"

// 登录
export const login = (req) => {

    return apiRequestWrapper(() => api.post('/login', req))
}

// 登出
export const logout = () => {

    return apiRequestWrapper(() => api.post('/logout', null))
}

// 获取oauth2三方登录的配置信息
export const fetchOAuth2ClientConfig = () => {

    return apiRequestWrapper(() => api.get('/oauth2/client/other/config', {
        params: {
            clientType: 'APP'
        }
    }))
}

// 获取oauth2三方登录的配置信息
export const loginByGoogle = (code) => {

    return apiRequestWrapper(() => api.get('/oauth2/client/google/login', {
        params: {
            code: code
        }
    }))
}

// 获取当前登录用户的信息
export const fetchUserInfo = (token, userId) => {

    return apiRequestWrapper(() => api.get(`/api/social/userInfo/${userId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    }))
}

// 验证token是否有效
export const tokenValid = (token, tokenType = 'ACCESS_TOKEN') => {

    return apiRequestWrapper(() => api.get('/api/open/tokenValid', {
        params: {
            token: token,
            tokenType: tokenType
        }
    }))
}

// 获取用户会话信息
export const fetchUserSessions = () => {

    return apiRequestWrapper(() => api.get('/api/social/userSession/{userId}'))
}

// 创建用户会话
export const createUserSession = (receiverUserId, deliveryMethod) => {
    const createUserSessionReq = {
        receiverUserId: receiverUserId,
        deliveryMethod: deliveryMethod
    }

    return apiRequestWrapper(() => api.post('/api/social/userSession/{userId}', createUserSessionReq))
}

// 根据会话id删除会话
export const deleteUserSessionById = (id) => {
    const deleteUserSessionReq = {
        id: id
    }
    return apiRequestWrapper(() => api.delete('/api/social/userSession/{userId}', { data: deleteUserSessionReq }))
}

// 分页获取用户会话关联的消息
export const fetchMessageByUserSessionId = (sessionId, prevMsgId = null, pageNum = 1, pageSize = 20) => {

    return apiRequestWrapper(() => api.get('/api/social/userMessage/{userId}', {
        params: {
            sessionId: sessionId,
            prevMsgId: prevMsgId,
            pageNum: pageNum,
            pageSize: pageSize
        }
    }))
}

// 删除消息
export const deleteUserMessageById = (messageId) => {
    const deleteUserMessageReq = {
        id: messageId
    }
    return apiRequestWrapper(() => api.delete('/api/social/userMessage/{userId}', { data: deleteUserMessageReq }))
}

// 获取用户好友
export const fetchUserFriends = () => {

    return apiRequestWrapper(() => api.get('/api/social/userFriend/{userId}'))
}

// 获取用户群组
export const fetchUserGroups = () => {

    return apiRequestWrapper(() => api.get('/api/social/userGroup/{userId}'))
}


const apiRequestWrapper = async (requestFn) => {
    try {
        const res = await requestFn()
        return res.data
    } catch (err) {
        handleError(err)
        throw err
    }
}

const handleError = (error) => {
    // 这里可以加入更多的错误处理逻辑，比如发送错误日志、显示错误信息等
    showToast(error.message || 'An unexpected error occurred');
}