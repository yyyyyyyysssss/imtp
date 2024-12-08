import { createContext,useContext} from 'react'


export const AuthContext = createContext();

export const SignInContext = createContext()

export const useIsSignedIn = () => {
    const isSignedIn = useContext(SignInContext)
    return isSignedIn;
}

export const useIsSignedOut = () => {
    const isSignedIn = useContext(SignInContext)
    return !isSignedIn;
}



export const ImageContext  = createContext();