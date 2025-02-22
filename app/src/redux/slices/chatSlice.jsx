import { createSlice } from '@reduxjs/toolkit'

export const chatSlice = createSlice({
    name: 'chat',
    initialState: {
        entities: {
            sessions: {},
            messages: {}
        },
        result: [],
        selectedSessionId: null,
        userFriends: {
            result: [],
            entities: {
                friends: {}
            }
        },
        userGroups: {
            result: [],
            entities: {
                groups: {},
                groupUserInfos: {}
            }
        },
        unreadCount: 0 //未读消息合计
    },
    reducers: {
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
            state.selectedSessionId = sessionId
            if (sessionId === null) {
                return
            }
            //会话未读消息
            const unreadMessageCount = state.entities.sessions[sessionId].unreadMessageCount || 0
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
            const { sessionId, messages } = payload
            const messageInit = state.entities.sessions[sessionId].messageInit
            messages.forEach(message => {
                if (!state.entities.messages) {
                    state.entities.messages = {}
                }
                state.entities.messages[message.id] = { ...message }
                if (!state.entities.sessions[sessionId].messages) {
                    state.entities.sessions[sessionId].messages = []
                }
                if (messageInit === undefined || messageInit === false) {
                    state.entities.sessions[sessionId].messages.push(message.id)
                } else {
                    state.entities.sessions[sessionId].messages.unshift(message.id)
                }

            });
            state.entities.sessions[sessionId].messageInit = true
        },
        addMessage: (state, action) => {
            const { payload } = action
            const { sessionId, message } = payload
            //添加消息
            state.entities.messages = state.entities.messages || {}
            state.entities.messages[message.id] = { ...message }
            //添加会话关联的消息id
            state.entities.sessions[sessionId].messages = state.entities.sessions[sessionId].messages || []
            state.entities.sessions[sessionId].messages.unshift(message.id)
            //更新会话最新消息
            state.entities.sessions[sessionId].lastMsgType = message.type
            state.entities.sessions[sessionId].lastMsgContent = message.content
            state.entities.sessions[sessionId].lastMsgTime = message.timestamp
            state.entities.sessions[sessionId].lastUserName = message.name
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
            const { entities, result } = payload
            state.userFriends.entities = entities
            state.userFriends.result = result.map(id => ({ id: entities.friends[id].id, key: entities.friends[id].key, value: entities.friends[id].value }))
        },
        loadUserGroup: (state, action) => {
            const { payload } = action
            const { entities, result } = payload
            state.userGroups.entities = entities
            state.userGroups.result = result
        }
    }
})

export const { loadSession, addSession, selectSession, removeSession, loadMessage, addMessage, updateMessage, updateMessageStatus, deleteMessage, loadUserFriend, loadUserGroup } = chatSlice.actions

export default chatSlice.reducer