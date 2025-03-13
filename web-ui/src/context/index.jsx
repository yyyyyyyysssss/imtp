import { createContext,useState,useEffect,useContext } from 'react'
import Cookies from 'js-cookie'
import env from '../env';
import { MessageType } from '../enum';

export const HomeContext = createContext();

export const ChatPanelContext  = createContext();

export const FriendPanelContext  = createContext();

export const GroupPanelContext  = createContext();


const AUTHORIZATION_RES = -120;

// websocket
const WebSocketContext = createContext();
export const WebSocketProvider = ({ children }) => {

    const [socket,setSocket] = useState(null);

    const start = () => {
        const ws = new WebSocket(env.wobsocketUrl);
        ws.onopen = () => {
            console.log('WebSocket connection opened');
            const token = Cookies.get("accessToken");
            ws.send(token);
        }
        ws.onmessage = (event) => {
            const obj = JSON.parse(event.data);
            const {header} = obj;
            if(header){
                if(header.cmd === MessageType.AUTHORIZATION_RES){
                    const authenticated = obj.authenticated;
                    if(authenticated){
                        console.log('Websocket Server authenticated');
                    }else{
                        console.log('Websocket Server unauthenticated');
                    }
                } else if(header.cmd === MessageType.HEARTBEAT_PING){
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
            console.log('WebSocket error: ',error);
        }
        let timeoutId;
        ws.onclose = () => {
            console.log('WebSocket connection closed');
            if(timeoutId){
                clearTimeout(timeoutId)
            }
            timeoutId = setTimeout(() => {
                console.log('WebSocket reconnecting');
                start();
            },3000);
        }
        setSocket(ws);
    }

    useEffect(() => {
        start();
        return () => socket?.close();
    },[]);

    return (
        <WebSocketContext.Provider value={{socket}}>
            {children}
        </WebSocketContext.Provider>
    );
}

export const useWebSocket = () => {
    const context = useContext(WebSocketContext);
    if(context  === undefined){
        throw new Error('useWebSocket must be used within a WebSocketProvider');
    }
    return context;
}
