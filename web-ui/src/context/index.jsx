import { createContext, useState, useEffect, useContext, useRef } from 'react'
import env from '../env';
import { MessageType } from '../enum';
import { getToken } from '../router/AuthProvider';

export const HomeContext = createContext();

export const ChatPanelContext = createContext();

export const FriendPanelContext = createContext();

export const GroupPanelContext = createContext();

// websocket
const WebSocketContext = createContext();
export const WebSocketProvider = ({ children }) => {

    const [socket, setSocket] = useState(null);

    const reconnectTimeout = useRef(null);
    const wsRef = useRef(null);

    const start = () => {
        // 清理旧连接
        if (wsRef.current) {
            wsRef.current.close();
            wsRef.current = null;
        }
        if (reconnectTimeout.current) {
            clearTimeout(reconnectTimeout.current)
            reconnectTimeout.current = null
        }
        // 创建新的websocket
        const ws = new WebSocket(env.wobsocketUrl);
        wsRef.current = ws;
        ws.onopen = () => {
            console.log('websocket connected');
            if (reconnectTimeout.current) {
                clearTimeout(reconnectTimeout.current)
                reconnectTimeout.current = null
            }
            const token = getToken()
            ws.send(token);
        }
        ws.onmessage = (event) => {
            const obj = JSON.parse(event.data);
            const { header } = obj;
            if (header) {
                if (header.cmd === MessageType.AUTHORIZATION_RES) {
                    const authenticated = obj.authenticated;
                    if (authenticated) {
                        console.log('websocket authenticated');
                    } else {
                        console.log('websocket unauthenticated');
                    }
                } else if (header.cmd === MessageType.HEARTBEAT_PING) {
                    const pongMsg = {
                        type: MessageType.HEARTBEAT_PONG,
                        sender: 0,
                        receiver: 0
                    }
                    ws.send(JSON.stringify(pongMsg))
                }
            }

        }
        ws.onerror = (error) => {
            console.error('websocket error: ', error);
        }
        ws.onclose = () => {
            console.log('websocket closed');
            if (reconnectTimeout.current) {
                clearTimeout(reconnectTimeout.current)
            }
            reconnectTimeout.current = setTimeout(() => {
                console.log('websocket reconnecting');
                start();
            }, 3000);
        }
        setSocket(ws);
    }

    useEffect(() => {
        start();
        return () => {
            if (reconnectTimeout.current) {
                clearTimeout(reconnectTimeout.current);
            }
            if (wsRef.current) {
                wsRef.current.close();
            }
        }
    }, []);

    return (
        <WebSocketContext.Provider value={{ socket }}>
            {children}
        </WebSocketContext.Provider>
    );
}

export const useWebSocket = () => {
    const context = useContext(WebSocketContext);
    if (context === undefined) {
        throw new Error('useWebSocket must be used within a WebSocketProvider');
    }
    return context;
}
