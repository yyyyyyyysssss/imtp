import { Input, Flex, Pressable, NativeBaseProvider, Text, VStack, Button, Checkbox, HStack } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';

const Login = () => {
    const [show, setShow] = useState(false);
    return (
        <>
            <NativeBaseProvider>
                <Flex style={styles.containerH} alignItems='center' direction="row">
                    <Flex gap={12} style={styles.containerV} alignItems='center' direction="column">
                        <VStack space={5}>
                            <Input borderRadius={12} style={styles.username} shadow={5} _light={{
                                bg: "blueGray.200",
                            }} height={50} w={{
                                base: "75%",
                                md: "25%"
                            }}
                                placeholder="用户名" />

                            <Input borderRadius={12} style={styles.password} shadow={5} _light={{
                                bg: "blueGray.200",
                            }} height={50} w={{
                                base: "75%",
                                md: "25%"
                            }} InputRightElement={
                                <Pressable onPress={() => setShow(!show)}>
                                    <MaterialIcon name={show ? "visibility" : "visibility-off"} size={28} mr="2" color="gray" />
                                </Pressable>
                            }
                                type={show ? "text" : "password"}
                                placeholder="密码" />
                            <HStack justifyContent="flex-end">
                                <Text style={styles.forgotPassword}>忘记密码?</Text>
                            </HStack>
                            <Button style={styles.loginBtn} borderRadius={12} height={50} _text={{ fontSize: 20 }} backgroundColor="tertiary.500">登录</Button>
                        </VStack>
                        <VStack space={5} width="75%">
                            <HStack space={10} alignItems="center" style={styles.threePartyLogin}>
                                <AntDesignIcon name="google" size={28} mr="2" color="red" />
                                <Text style={styles.threePartyLoginText}>Sing in with Google</Text>
                            </HStack>
                            <HStack space={10} alignItems="center" style={styles.threePartyLogin}>
                                <AntDesignIcon name="github" size={28} mr="2" />
                                <Text style={styles.threePartyLoginText}>Sing in with Githuib</Text>
                            </HStack>
                        </VStack>
                    </Flex>
                </Flex>
            </NativeBaseProvider>
        </>
    )
}


const styles = StyleSheet.create({
    containerH: {
        flex: 1
    },
    containerV: {
        flex: 1
    },
    username: {
        fontSize: 18
    },
    password: {
        fontSize: 18
    },
    forgotPassword: {
        color: '#55ab70',
        fontSize: 15
    },
    loginBtn: {

    },
    threePartyLogin: {
        borderWidth: 1,
        borderColor: '#eff0f1',
        borderRadius: 12,
        height: 50,
        padding: 10
    },
    threePartyLoginText: {
        fontSize: 20,
        textAlign: 'center'
    }
})


export default Login;