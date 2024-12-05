import { createSlice } from '@reduxjs/toolkit'


export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        userToken: null,
        userInfo: null,
        isLoading: true,
    },
    reducers: {
        restoreToken: (state, action) => {
            state.userToken = action.payload.token
            state.userInfo = action.payload.userInfo
            state.isLoading = false
        },
        signIn: (state, action) => {
            state.userToken = action.payload.token
            state.userInfo = action.payload.userInfo
        },
        signOut: (state, action) => {
            state.userToken = null
            state.userInfo = null
        }
    }
})

export const { restoreToken, signIn, signOut } = authSlice.actions

export default authSlice.reducer