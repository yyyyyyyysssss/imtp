import { createSlice } from '@reduxjs/toolkit'

export const chatSlice = createSlice({
    name: 'chat',
    initialState: {
        entities: {
            sessions: {},
            messages: {}
        },
        result: [],
        userSessions: [], //会话
        selectedUserSession: null, //当前选中的会话
        messages: {}, //会话关联的消息
        unreadCount: 0 //未读消息合计
    },
    reducers: {
        loadSession: (state,action) => {
            const { payload } = action
            state.entities = payload.entities
            state.result = payload.result
        },
        loadMessage: (state,action) => {
            const { payload } = action
            const { sessionId, messages } = payload
            messages.forEach(message => {
                chatSlice.reducer(state,chatSlice.actions.addMessage({sessionId: sessionId,message: message}))
            });
            const session = state.entities.sessions[sessionId]
            state.entities.sessions[sessionId] = {...session,messageInit: true}
        },
        addMessage: (state,action) => {
            const { payload } = action
            const { sessionId, message } = payload
            if(!state.entities.messages){
                state.entities.messages = {}
            }
            state.entities.messages[message.id] = {...message}
            if(!state.entities.sessions[sessionId].messages){
                state.entities.sessions[sessionId].messages = []
            }
            state.entities.sessions[sessionId].messages.push(message.id)
        },
        updateMessage: (state,action) => {
            const { payload } = action
            const { message } = payload
            state.entities.messages[message.id] = {...message}
        },
        initSession: (state, action) => {
            console.log('initSession')
            state.userSessions = action.payload
            state.unreadCount = action.payload.reduce((acc,session) => acc + session.unreadMessageCount,0)
        },
        addSession: (state, action) => {
            console.log('addSession')
            state.userSessions.push(action.payload)
        },
        updateSession: (state, action) => {

        },
        selectSession: (state, action) => {
            state.selectedUserSession = action.payload
        },
        removeSession: (state, action) => {
            console.log('removeSession')
            state.userSessions = state.userSessions.filter(s => s.id != action.payload)
        },
        incrUnreadCount: (state, action) => {
            state.unreadCount += action.payload
        },
        decrUnreadCount: (state, action) => {
            state.unreadCount -= action.payload
        }


    }
})

export const { initSession,loadSession,loadMessage, addSession, updateSession, selectSession, removeSession,incrUnreadCount,decrUnreadCount,addMessage,updateMessage } = chatSlice.actions

export default chatSlice.reducer