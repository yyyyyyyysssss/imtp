import { message } from "antd"
import httpWrapper from "./axiosWrapper"

// 登录
export const login = (req) => {

    return apiRequestWrapper(() => httpWrapper.post('/login', req))
}

// 一次性token登录
export const loginByOTT = (ottToken) => {

    return apiRequestWrapper(() => httpWrapper.get('/login/ott', {
        params: {
            ottToken: ottToken,
            clientType: 'WEB'
        }
    }))
}

// 登出
export const logout = () => {

    return apiRequestWrapper(() => httpWrapper.post('/logout', null))
}

//发送邮箱验证码
export const sendEmailVerificationCode = (email) => {

    return apiRequestWrapper(() => httpWrapper.get('/open/sendEmailVerificationCode', {
        params: {
            email: email
        }
    }))
}

// oauth2 登录
export const oauth2Login = (code, state) => {

    return apiRequestWrapper(() => httpWrapper.get('/oauth2/client/other/login', {
        params: {
            code: code,
            state: state
        }
    }))
}

// 获取oauth2三方登录的配置信息
export const fetchOAuth2ClientConfig = () => {

    return apiRequestWrapper(() => httpWrapper.get('/oauth2/client/other/config'))
}

// 获取当前登录用户的信息
export const fetchUserInfo = () => {

    return apiRequestWrapper(() => httpWrapper.get('/api/social/userInfo/{userId}'))
}

// 验证token是否有效
export const tokenValid = (token, tokenType = 'ACCESS_TOKEN') => {

    return apiRequestWrapper(() => httpWrapper.get('/api/open/tokenValid', {
        params: {
            token: token,
            tokenType: tokenType
        }
    }))
}

// 获取用户会话信息
export const fetchUserSessions = () => {

    return apiRequestWrapper(() => httpWrapper.get('/api/social/userSession/{userId}'))
}

// 创建用户会话
export const createUserSession = (receiverUserId, deliveryMethod) => {
    const createUserSessionReq = {
        receiverUserId: receiverUserId,
        deliveryMethod: deliveryMethod
    }
    return apiRequestWrapper(() => httpWrapper.post('/api/social/userSession/{userId}', createUserSessionReq))
}

// 根据会话id删除会话
export const deleteUserSessionById = (id) => {
    const deleteUserSessionReq = {
        id: id
    }
    return apiRequestWrapper(() => httpWrapper.delete('/api/social/userSession/{userId}', { data: deleteUserSessionReq }))
}

// 分页获取用户会话关联的消息
export const fetchMessageByUserSessionId = (sessionId, prevMsgId = null, pageNum = 1, pageSize = 10) => {

    return apiRequestWrapper(() => httpWrapper.get('/api/social/userMessage/{userId}', {
        params: {
            sessionId: sessionId,
            prevMsgId: prevMsgId,
            pageNum: pageNum,
            pageSize: pageSize
        }
    }))
}

export const deleteUserMessageById = (messageId) => {
    const deleteUserMessageReq = {
        id: messageId
    }

    return apiRequestWrapper(() => httpWrapper.delete('/api/social/userMessage/{userId}', { data: deleteUserMessageReq }))
}

// 获取用户好友
export const fetchUserFriends = () => {

    return apiRequestWrapper(() => httpWrapper.get('/api/social/userFriend/{userId}'))
}

// 获取用户群组
export const fetchUserGroups = () => {

    return apiRequestWrapper(() => httpWrapper.get('/api/social/userGroup/{userId}'))
}

// 获取上传任务id
export const fetchUploadId = (fileInfo) => {

    return apiRequestWrapper(() => httpWrapper.post('/api/file/uploadId', fileInfo))
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
    message.error(error.message || 'An unexpected error occurred');
}