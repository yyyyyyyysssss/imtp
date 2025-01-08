import { useMemo, useRef, useState } from "react"
import { useSelector } from "react-redux"



const useSession = (receiverUserId) => {

    const sessions = useSelector(state => state.chat.entities.sessions)

    const session = useMemo(() => {
        return Object.values(sessions).find(s => s.receiverUserId === receiverUserId);
    },[receiverUserId,sessions])

    return session;
}

export default useSession