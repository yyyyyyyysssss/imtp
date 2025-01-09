import api from "./api"

// 登录
export const login = (req) => {

    return new Promise((resolve, reject) => {
        api.post('/login', req)
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 登出
export const logout = () => {

    return new Promise((resolve, reject) => {
        api.post('/logout', null)
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 获取当前登录用户的信息
export const fetchUserInfo = (token) => {

    return new Promise((resolve, reject) => {
        api.get('/social/userInfo', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 验证token是否有效
export const tokenValid = (token, tokenType = 'ACCESS_TOKEN') => {

    return new Promise((resolve, reject) => {
        api.get('/open/tokenValid', {
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
        api.get('/social/userSession/{userId}')
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 创建用户会话
export const createUserSession = (req) => {

    return new Promise((resolve, reject) => {
        api.post('/social/userSession/{userId}', req)
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 根据会话id删除会话
export const deleteUserSessionById = (id) => {
    const deleteUserSessionReq = {
        id: id
    }
    return new Promise((resolve, reject) => {
        api.delete('/social/userSession/{userId}', { data: deleteUserSessionReq })
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 分页获取用户会话关联的消息
export const fetchMessageByUserSessionId = (sessionId, pageNum = 1, pageSize = 20) => {

    return new Promise((resolve, reject) => {
        api.get('/social/userMessage/{userId}', {
            params: {
                sessionId: sessionId,
                pageNum: pageNum,
                pageSize: pageSize
            }
        })
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 获取用户好友
export const fetchUserFriends = () => {

    return new Promise((resolve, reject) => {
        api.get('/social/userFriend/{userId}')
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}

// 获取用户群组
export const fetchUserGroups = () => {

    return new Promise((resolve, reject) => {
        api.get('/social/userGroup/{userId}')
            .then(res => resolve(res.data))
            .catch(error => reject(error))
    })
}