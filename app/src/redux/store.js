import { configureStore } from '@reduxjs/toolkit'
import authReducer from './slices/authSlice'
import chatReducer from './slices/chatSlice'

const reduxStore = configureStore({
    reducer: {
        auth: authReducer,
        chat: chatReducer
    }
})

export default reduxStore
