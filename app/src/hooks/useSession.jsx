import { useRef } from "react"
import { useSelector } from "react-redux"



const useSession = () => {
    const sessionIds = useSelector(state => state.chat.result)

    const sessionsRef = useRef({})
    useEffect(() => {
        const state = reduxStore.getState()
        for (let sessionId of sessionIds) {
            const session = state.chat.entities.sessions[sessionId]
            sessionsRef.current[session.receiverUserId] = session
        }
    }, [sessionIds])

}