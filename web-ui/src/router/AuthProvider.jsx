import { Navigate } from 'react-router-dom';
import Cookies from 'js-cookie'
import { jwtDecode } from 'jwt-decode'

export const isLoginIn = () => {
    let isLoginIn = false;
    const token = Cookies.get("accessToken");
    if(token){
        const tokenInfo = jwtDecode(token);
        const expiration = tokenInfo.expiration;
        if(Date.now() < expiration){
            isLoginIn = true;
        }
    }
    return isLoginIn;
}

const AuthProvider = ({children }) => {
    
    return isLoginIn() ? children : <Navigate to='/login' replace={true} />;
}


export default AuthProvider;