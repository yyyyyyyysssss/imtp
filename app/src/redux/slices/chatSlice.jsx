import { createSlice } from '@reduxjs/toolkit'

export const chatSlice = createSlice({
    name: 'chat',
    initialState: {
        userSessions: [],
        messages: {}
    },
    reducers: {
        initSession: (state, action) => {
            state.userSessions = action.payload
        },
        addSession: (state, action) => {
            state.userSessions.push(action.payload)
        },
        updateSession: (state, action) => {

        },
        selectSession: (state, action) => {

        },
        removeSession: (state, action) => {

        },

    }
})

export const { initSession, addSession, updateSession, selectSession, removeSession } = chatSlice.actions

export default chatSlice.reducer