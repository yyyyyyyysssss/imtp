import { createSlice } from '@reduxjs/toolkit'
import Storage from '../../storage/storage';
import { NativeModules } from 'react-native';

const { MessageModule } = NativeModules

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        userToken: null,
        userInfo: null,
        isLoading: true,
    },
    reducers: {
        signIn: (state, action) => {
            const userToken = action.payload.token
            const userInfo = action.payload.userInfo
            state.userToken = userToken
            state.userInfo = userInfo
            state.isLoading = false
            Storage.batchSave({
                userInfo: userInfo,
                userToken: userToken
            })
            MessageModule.init(JSON.stringify(userToken))
                .then(
                    (res) => {
                        console.log('MessageModule init succeed')
                    },
                    (error) => {
                        console.log('MessageModule init error:', error.message)
                    }
                )
        },
        signOut: (state, action) => {
            Storage.multiRemove(['userToken', 'userInfo'])
            state.userToken = null
            state.userInfo = null
            state.isLoading = false
            MessageModule.destroy()
                .then(
                    (res) => {
                        console.log('MessageModule destroy', res ? 'succeed' : 'failed')
                    },
                    (error) => {
                        console.log('MessageModule destroy', 'failed', error.message)
                    }
                )
        }
    }
})

export const { signIn, signOut } = authSlice.actions

export default authSlice.reducer