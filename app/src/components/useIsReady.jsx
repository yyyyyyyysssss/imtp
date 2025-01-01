import { useCallback, useEffect, useState } from "react"
import { InteractionManager } from "react-native"



const useIsReady = () => {
    const [isReady, setIsReady] = useState(false)

    useEffect(() => {
        InteractionManager.runAfterInteractions(() => {
            setTimeout(() => {
                setIsReady(true)
            }, 100);
        })
        return () => {
            setIsReady(false)
        }
    },[])

    return isReady
}

export default useIsReady