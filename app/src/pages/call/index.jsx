import { Text } from "native-base"
import { useRef } from "react";
import { View, requireNativeComponent, StyleSheet } from 'react-native';

const CallView = requireNativeComponent('CallView');

const Call = () => {

    const callRef = useRef(null)

    return (
        <View>
            <CallView ref={callRef} style={styles.helloWorldView}/>
        </View>
    )
}


const styles = StyleSheet.create({
    helloWorldView: {
        width: '100%',
        height: '100%'
    },
})

export default Call