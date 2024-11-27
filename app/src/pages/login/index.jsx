import { Box, Flex, NativeBaseProvider, Text } from 'native-base';
import React from 'react';
import { StyleSheet } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

const Login = () => {

    return (
        <>
            <NativeBaseProvider>
                <Flex style={styles.containerH} alignItems='center' direction="row">
                    <Flex style={styles.containerV} alignItems='center' direction="column">
                        <Icon name="person" size={30}/>
                    </Flex>
                </Flex>
            </NativeBaseProvider>
        </>
    )
}


const styles = StyleSheet.create({
    containerH: {
        backgroundColor: 'red',
        flex: 1
    },
    containerV: {
        flex: 1
    }
})


export default Login;