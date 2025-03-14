import { Button, Text, VStack } from "native-base"
import { useCallback, useRef, useState } from "react";
import { UIManager, findNodeHandle, View, requireNativeComponent, PermissionsAndroid, StyleSheet } from 'react-native';
import { requestCameraPermission } from "../../utils/PermissionRequest";

const CallView = requireNativeComponent('CallView');


const FRONT_CAMERA = '0'
const BACK_CAMERA = '1'

const Call = () => {

    const callRef = useRef(null)

    const [lensFacing, setLensFacing] = useState(BACK_CAMERA)

    const initCamere = async () => {
        const permissionChecked = await requestCameraPermission()
        if (permissionChecked) {
            const viewId = findNodeHandle(callRef.current)
            UIManager.dispatchViewManagerCommand(
                viewId,
                'INIT_CAMERA',
                [BACK_CAMERA]
            )
        }
    }

    const switchCamera = useCallback(() => {
        let lf;
        if (lensFacing === FRONT_CAMERA) {
            setLensFacing(BACK_CAMERA)
            lf = BACK_CAMERA
        } else {
            setLensFacing(FRONT_CAMERA)
            lf = FRONT_CAMERA
        }
        const viewId = findNodeHandle(callRef.current)
        UIManager.dispatchViewManagerCommand(
            viewId,
            'SWITCH_CAMERA',
            [lf]
        )
    }, [lensFacing])

    return (
        <VStack>
            <CallView ref={callRef} style={styles.helloWorldView} />
            <Button onPress={initCamere}>
                init camere
            </Button>
            <Button onPress={switchCamera}>
                switch camere
            </Button>
        </VStack>
    )
}


const styles = StyleSheet.create({
    helloWorldView: {
        width: '100%',
        height: 400
    },
})

export default Call