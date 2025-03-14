import { Platform } from 'react-native';
import { check, request, PERMISSIONS, RESULTS } from 'react-native-permissions';


export const requestCameraPermission = async () => {
    if (Platform.OS === 'android') {
        try {
            const checkd = await check(PERMISSIONS.ANDROID.CAMERA)
            if(checkd === RESULTS.GRANTED){
                return true
            }
            const result = await request(PERMISSIONS.ANDROID.CAMERA)
            if (result === RESULTS.GRANTED) {
                return true
            } else {
                return false
            }
        } catch (err) {
            return false
        }
    }else if(Platform.OS === 'ios'){
        return true
    }
}