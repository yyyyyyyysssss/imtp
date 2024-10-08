import { useRef } from 'react';

const GlobalCache = () => {
    const cacheRef = useRef(new Map());

    const setCache = (key, value) => {
        cacheRef.current.set(key, value);
    };

    const getCache = (key) => {
        return cacheRef.current.get(key);
    };

    return { setCache, getCache };
}

export default GlobalCache;