import { useEffect, useMemo, useRef, useState } from "react"
import { useSelector } from "react-redux"
import { fetchUserFriends } from "../api/ApiService"



const useFriend = () => {

    const userFriends = useSelector(state => state.chat.userFriends)

    const userGroups = useSelector(state => state.chat.userGroups)

    useEffect(() => {
        const fetchUserFriendList = async () => {
            const userFriendList = await fetchUserFriends()
        }
        if(!userFriends || !userFriends.length){

        }
    },[])

    return null;
}

export default useFriend