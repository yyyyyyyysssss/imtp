import { Box, Flex, NativeBaseProvider, Text } from 'native-base';
import React from 'react';
import { StyleSheet } from 'react-native';

const Login = () => {

    return (
        <>
            <NativeBaseProvider>
                <Flex style={styles.containerH} alignItems='center' direction="row">
                    <Flex style={styles.containerV} alignItems='center' direction="column">
                        <Text>A</Text>
                        <Text>B</Text>
                        <Text>C</Text>
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