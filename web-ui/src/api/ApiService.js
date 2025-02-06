import { message } from "antd"
import httpWrapper from "./axiosWrapper"

// 登录
export const login = (req) => {

    return new Promise((resolve, reject) => {
        httpWrapper.post('/login', req)
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 登出
export const logout = () => {

    return new Promise((resolve, reject) => {
        httpWrapper.post('/logout', null)
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 获取当前登录用户的信息
export const fetchUserInfo = (token) => {

    return new Promise((resolve, reject) => {
        httpWrapper.get('/social/userInfo', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 验证token是否有效
export const tokenValid = (token, tokenType = 'ACCESS_TOKEN') => {

    return new Promise((resolve, reject) => {
        httpWrapper.get('/open/tokenValid', {
            params: {
                token: token,
                tokenType: tokenType
            }
        })
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 获取用户会话信息
export const fetchUserSessions = () => {
    return new Promise((resolve, reject) => {
        httpWrapper.get('/social/userSession/{userId}')
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 创建用户会话
export const createUserSession = (receiverUserId, deliveryMethod) => {
    const createUserSessionReq = {
        receiverUserId: receiverUserId,
        deliveryMethod: deliveryMethod
    }
    return new Promise((resolve, reject) => {
        httpWrapper.post('/social/userSession/{userId}', createUserSessionReq)
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 根据会话id删除会话
export const deleteUserSessionById = (id) => {
    const deleteUserSessionReq = {
        id: id
    }
    return new Promise((resolve, reject) => {
        httpWrapper.delete('/social/userSession/{userId}', { data: deleteUserSessionReq })
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 分页获取用户会话关联的消息
export const fetchMessageByUserSessionId = (sessionId, pageNum = 1, pageSize = 20) => {

    return new Promise((resolve, reject) => {
        httpWrapper.get('/social/userMessage/{userId}', {
            params: {
                sessionId: sessionId,
                pageNum: pageNum,
                pageSize: pageSize
            }
        })
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 获取用户好友
export const fetchUserFriends = () => {

    return new Promise((resolve, reject) => {
        httpWrapper.get('/social/userFriend/{userId}')
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

// 获取用户群组
export const fetchUserGroups = () => {

    return new Promise((resolve, reject) => {
        httpWrapper.get('/social/userGroup/{userId}')
            .then(res => resolve(res.data))
            .catch(error => handleError)
    })
}

const handleError = (error) => {
    // 这里可以加入更多的错误处理逻辑，比如发送错误日志、显示错误信息等
    message.error(error.message || 'An unexpected error occurred');
}