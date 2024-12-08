import { createSlice } from '@reduxjs/toolkit'

export const chatSlice = createSlice({
    name: 'chat',
    initialState: {
        userSessions: [], //会话
        selectedUserSession: null, //当前选中的会话
        messages: {}, //会话关联的消息
        unreadCount: 0 //未读消息合计
    },
    reducers: {
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

export const { initSession, addSession, updateSession, selectSession, removeSession,incrUnreadCount,decrUnreadCount } = chatSlice.actions

export default chatSlice.reducer