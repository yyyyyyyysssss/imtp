import { createSlice } from '@reduxjs/toolkit'


export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        userToken: null,
        userInfo: null,
        isLoading: true,
    },
    reducers: {
        signIn: (state, action) => {
            state.userToken = action.payload.token
            state.userInfo = action.payload.userInfo
            state.isLoading = false
        },
        signOut: (state, action) => {
            state.userToken = null
            state.userInfo = null
            state.isLoading = false
        }
    }
})

export const { signIn, signOut } = authSlice.actions

export default authSlice.reducer