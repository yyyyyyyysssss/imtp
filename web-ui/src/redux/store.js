import { configureStore } from '@reduxjs/toolkit'
import chatReducer from './slices/chatSlice'

const reduxStore = configureStore({
    reducer: {
        chat: chatReducer
    }
})

export default reduxStore
