import { createContext,useState,useEffect,useContext } from 'react'
import Cookies from 'js-cookie'

export const HomeContext = createContext();

export const ChatPanelContext  = createContext();

export const FriendPanelContext  = createContext();

export const GroupPanelContext  = createContext();


const AUTHORIZATION_RES = -120;

// websocket
const WEB_SEOCKET_SERVER_URL = "ws://localhost:8080/im";
const WebSocketContext = createContext();
export const WebSocketProvider = ({ children }) => {

    const [socket,setSocket] = useState(null);

    const [userInfo,setUserInfo] = useState(null);

    const start = () => {
        const ws = new WebSocket(WEB_SEOCKET_SERVER_URL);
        ws.onopen = () => {
            console.log('WebSocket connection opened');
            const token = Cookies.get("accessToken");
            ws.send(token);
        }
        ws.onmessage = (event) => {
            const obj = JSON.parse(event.data);
            const {header} = obj;
            if(header){
                if(header.cmd === AUTHORIZATION_RES){
                    const authenticated = obj.authenticated;
                    const userInfo = obj.userInfo;
                    setUserInfo(userInfo);
                    if(authenticated){
                        console.log('Websocket Server 身份认证成功');
                    }else{
                        console.log('Websocket Server 身份认证失败');
                    }
                }
            }
            
        }
        ws.onerror = (error) => {
            console.log('WebSocket error: ',error);
        }
        ws.onclose = () => {
            console.log('WebSocket connection closed');
            setTimeout(() => {
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
        <WebSocketContext.Provider value={{socket,userInfo}}>
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
