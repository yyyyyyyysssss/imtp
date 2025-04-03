import { createSlice } from '@reduxjs/toolkit'
import { message } from 'antd'


const initialState = {
    panel: 'CHAT_PANEL',
    userInfo: null,
    entities: {
        sessions: {},
        messages: {}
    },
    result: [],
    selectedSessionId: null,
    selectedHeadName: null,
    userFriends: [],
    userGroups: [],
    unreadCount: 0, //未读消息合计
    uploadProgress: {},  //上传进度
    voiceCall: {
        visible: false,
        sessionId: null,
        callOperation: null,
        callType: null
    }
}

export const chatSlice = createSlice({
    name: 'chat',
    initialState: initialState,
    reducers: {
        reset: () => initialState,
        switchPanel: (state, action) => {
            const { payload } = action
            const { panel } = payload
            state.panel = panel
        },
        setUserInfo: (state, action) => {
            const { payload } = action
            const { userInfo } = payload
            state.userInfo = userInfo
        },
        loadSession: (state, action) => {
            const { payload } = action
            state.entities = payload.entities
            state.result = payload.result
        },
        addSession: (state, action) => {
            const { payload } = action
            const { session } = payload
            const sessionId = session.id
            state.entities.sessions[sessionId] = session
            state.result.unshift(sessionId)
        },
        selectSession: (state, action) => {
            const { payload } = action
            const { sessionId } = payload
            if (sessionId === state.selectedSessionId) {
                return
            }
            state.selectedSessionId = sessionId
            if (sessionId === null) {
                return
            }
            const session = state.entities.sessions[sessionId]
            state.selectedHeadName = session.name
            //会话未读消息
            const unreadMessageCount = session.unreadMessageCount || 0
            if (unreadMessageCount === 0) {
                return
            }
            state.entities.sessions[sessionId].unreadMessageCount = 0
            //未读消息总计
            const unreadCount = state.unreadCount || 0
            state.unreadCount = unreadCount - unreadMessageCount
        },
        removeSession: (state, action) => {
            const { payload } = action
            const { sessionId } = payload
            if (sessionId === state.selectedSessionId) {
                state.selectedSessionId = null
                state.selectedHeadName = null
            }
            // 清除该会话的未读消息
            const unreadMessageCount = state.entities.sessions[sessionId].unreadMessageCount || 0
            // 消息总计
            const unreadCount = state.unreadCount || 0
            state.unreadCount = unreadCount - unreadMessageCount
            delete state.entities.sessions[sessionId]
            state.result = [...state.result.filter(item => item !== sessionId)]
        },
        loadMessage: (state, action) => {
            const { payload } = action
            const { sessionId, messages, more } = payload
            const messageInit = state.entities.sessions[sessionId].messageInit
            let lastMessage;
            let firstMessage;
            messages.forEach(message => {
                if (!state.entities.messages) {
                    state.entities.messages = {}
                }
                state.entities.messages[message.id] = { ...message }
                if (!state.entities.sessions[sessionId].messages) {
                    state.entities.sessions[sessionId].messages = []
                }
                if (messageInit === undefined || messageInit === false || more === true) {
                    state.entities.sessions[sessionId].messages.unshift(message.id)
                } else {
                    state.entities.sessions[sessionId].messages.push(message.id)
                }
                firstMessage = message
                if(!lastMessage){
                    lastMessage = message
                }
            });
            if(firstMessage){
                state.entities.sessions[sessionId].prevMsgId = firstMessage.id
            }
            if(messageInit === undefined || messageInit === false){
                if(!state.entities.sessions[sessionId].lastMsgContent && lastMessage){
                    state.entities.sessions[sessionId].lastMsgType = lastMessage.type
                    state.entities.sessions[sessionId].lastMsgContent = lastMessage.content
                    state.entities.sessions[sessionId].lastMsgTime = lastMessage.timestamp
                    state.entities.sessions[sessionId].lastUserName = lastMessage.name
                }
                state.entities.sessions[sessionId].scrollToIndex = messages.length
                state.entities.sessions[sessionId].messageInit = true
            }
        },
        addMessage: (state, action) => {
            const { payload } = action
            const { sessionId, message } = payload
            //添加消息
            state.entities.messages = state.entities.messages || {}
            state.entities.messages[message.id] = { ...message }
            //添加会话关联的消息id
            state.entities.sessions[sessionId].messages = state.entities.sessions[sessionId].messages || []
            state.entities.sessions[sessionId].messages.push(message.id)
            //更新会话最新消息
            state.entities.sessions[sessionId].lastMsgType = message.type
            state.entities.sessions[sessionId].lastMsgContent = message.content
            state.entities.sessions[sessionId].lastMsgTime = message.timestamp
            state.entities.sessions[sessionId].lastUserName = message.name
            //更新滚动索引
            state.entities.sessions[sessionId].scrollToIndex = state.entities.sessions[sessionId].messages.length - 1
            //将会话移动到最前
            state.result = [sessionId, ...state.result.filter(item => item !== sessionId)]
            //未读消息
            if (!message.self && sessionId !== state.selectedSessionId) {
                //会话未读消息
                state.entities.sessions[sessionId].unreadMessageCount = state.entities.sessions[sessionId].unreadMessageCount || 0
                state.entities.sessions[sessionId].unreadMessageCount = state.entities.sessions[sessionId].unreadMessageCount + 1
                //未读消息总计
                state.unreadCount = state.unreadCount || 0
                state.unreadCount = state.unreadCount + 1
            }
        },
        updateMessage: (state, action) => {
            const { payload } = action
            const { message } = payload
            state.entities.messages[message.id] = { ...message }
        },
        updateMessageStatus: (state, action) => {
            const { payload } = action
            const { id, newStatus } = payload
            const message = state.entities.messages[id]
            state.entities.messages[id] = { ...message, status: newStatus }
        },
        deleteMessage: (state, action) => {

        },
        loadUserFriend: (state, action) => {
            const { payload } = action
            state.userFriends = payload
        },
        loadUserGroup: (state, action) => {
            const { payload } = action
            state.userGroups = payload
        },
        addUploadProgress: (state, action) => {
            const { payload } = action
            const { progressId, progressInfo } = payload
            state.uploadProgress[progressId] = progressInfo
        },
        updateUploadProgress: (state, action) => {
            const { payload } = action
            const { progressId, progress } = payload
            const progressInfo = state.uploadProgress[progressId]
            if (progressInfo) {
                const totalSize = progressInfo.totalSize
                const newProgress = progressInfo.progress + progress
                const newProgressInfo = {
                    totalSize: totalSize,
                    progress: newProgress,
                    percentage: (newProgress / totalSize) * 100
                }
                state.uploadProgress[progressId] = { ...newProgressInfo }
            }
        },
        startVoiceCall: (state, action) => {
            const { payload } = action
            const { sessionId, callOperation, callType } = payload
            if(state.voiceCall.visible){
                message.info('正在通话中...')
                return
            }
            state.voiceCall = {
                ...state.voiceCall,
                visible: true,
                sessionId: sessionId,
                callOperation: callOperation,
                callType: callType
            }
        },
        stopVoiceCall: (state, action) => {
            state.voiceCall = {
                ...state.voiceCall,
                visible: false,
                sessionId: null,
                callOperation: null,
                callType: null
            }
        }
    }
})

export const { reset, switchPanel, setUserInfo, loadSession, addSession, selectSession, removeSession, loadMessage, addMessage, updateMessage, updateMessageStatus, deleteMessage, loadUserFriend, loadUserGroup, addUploadProgress, updateUploadProgress, startVoiceCall, stopVoiceCall } = chatSlice.actions

export default chatSlice.reducer