

import { useState, useEffect, useRef, useMemo } from 'react';

const useTimer = () => {
    const [seconds, setSeconds] = useState(0)
    const [minutes, setMinutes] = useState(0)
    const [hours, setHours] = useState(0)
    const [isRunning, setIsRunning] = useState(false)
    const intervalRef = useRef(null)

    useEffect(() => {
        if (isRunning) {
            intervalRef.current = setInterval(() => {
                setSeconds((prevSeconds) => {
                    if (prevSeconds === 59) {
                        setMinutes((prevMinutes) => {
                            if (prevMinutes === 59) {
                                setHours((prevHours) => prevHours + 1)
                                return 0
                            }
                            return prevMinutes + 1
                        })
                        return 0
                    }
                    return prevSeconds + 1
                })
            }, 1000)
        } else {
            clearInterval(intervalRef.current)
        }
        return () => clearInterval(intervalRef.current)
    }, [isRunning])

    const toggleTimer = () => {
        setIsRunning((prev) => !prev)
    }

    const resetTimer = () => {
        setIsRunning(false)
        setSeconds(0)
        setMinutes(0)
        setHours(0)
    }

    const timer = useMemo(() => {
        return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    },[hours,minutes,seconds])

    return {
        hours,
        minutes,
        seconds,
        isRunning,
        timer,
        toggleTimer,
        resetTimer
    }

}

export default useTimer