import { Input, Flex, Pressable, NativeBaseProvider, Text, VStack, Button, Checkbox, HStack, Box, Divider } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { GoogleIcon, MicrosoftIcon } from '../../component/CustomIcon';

const Login = () => {
    const [show, setShow] = useState(false);
    return (
        <>
            <NativeBaseProvider>
                <Flex style={styles.containerH} alignItems='center' direction="row">
                    <Flex gap={12} style={styles.containerV} alignItems='center' direction="column">
                        <VStack space={5}>
                            <Input borderRadius={14} style={styles.username} shadow={5} _light={{
                                bg: "#ffffff",
                            }} height={60} w={{
                                base: "75%",
                                md: "25%"
                            }}
                                placeholder="用户名" />

                            <Input borderRadius={14} style={styles.password} shadow={5} _light={{
                                bg: "#ffffff",
                            }} height={60} w={{
                                base: "75%",
                                md: "25%"
                            }} InputRightElement={
                                <Pressable style={{ marginRight: 8 }} onPress={() => setShow(!show)}>
                                    <MaterialIcon name={show ? "visibility" : "visibility-off"} size={28} mr="2" color="gray" />
                                </Pressable>
                            }
                                type={show ? "text" : "password"}
                                placeholder="密码" />
                            <HStack justifyContent="flex-end">
                                <Text style={styles.forgotPassword}>忘记密码?</Text>
                            </HStack>
                            <Button style={styles.loginBtn} borderRadius={12} height={60} _text={{ fontSize: 20 }} backgroundColor="#fd6b68">登录</Button>
                        </VStack>

                        <HStack space={10} alignItems="center" style={styles.threePartyLogin}>
                            <GoogleIcon size={35} />
                            <AntDesignIcon name="github" size={35} />
                            <MicrosoftIcon size={35} />
                        </HStack>
                    </Flex>
                </Flex>
            </NativeBaseProvider>
        </>
    )
}


const styles = StyleSheet.create({
    containerH: {
        flex: 1,
        backgroundColor: '#edf1f7'
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
        color: '#81808f',
        fontSize: 15
    },
    loginBtn: {

    },
    threePartyLogin: {
        padding: 10
    }
})


export default Login;