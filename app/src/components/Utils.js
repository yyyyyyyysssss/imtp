import { Toast } from 'native-base';


export const showToast = (message,position = 'top',duration = 3000) => {
    Toast.show({
        title: message,
        placement: position,
        duration: duration,
        style: {
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderRadius: 6,
            padding: 10
        },
        zIndex: 1000,
        textStyle: {
            color: 'white',
            fontSize: 16,
        }
    })
}
