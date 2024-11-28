import { Input, Flex, Pressable, NativeBaseProvider, Text, VStack, Button, Checkbox, HStack, Center, Box, Image } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet, useWindowDimensions, View } from 'react-native';
import { TabView, SceneMap } from 'react-native-tab-view';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { GoogleIcon, MicrosoftIcon } from '../../component/CustomIcon';

const Login = () => {
    const [show, setShow] = useState(false);

    const layout = useWindowDimensions();
    const [index, setIndex] = useState(0);

    const [routes] = useState([
        { key: 'account', title: 'Account Login' },
        { key: 'phone', title: 'Phone Login' },
    ]);

    const renderScene = ({ route }) => {
        switch (route.key) {
            case 'account':
                return (
                    <Button>A</Button>
                )
            case 'phone':
                return (
                    <Button>B</Button>
                )
        }
    }
    return (
        <>
            <NativeBaseProvider>
                <Flex style={styles.containerH} alignItems='center' direction="row">
                    <Flex gap={20} style={styles.containerV} alignItems='center' direction="column">
                        <VStack  alignItems="center" justifyContent="flex-end" style={styles.loginHead}>
                            <Image source={require('../../assets/img/icon.png')}/>
                        </VStack>
                        <VStack style={{flex:1}} space={5}>
                            <Input borderRadius={14} style={styles.username} _light={{
                                // bg: "#f2f2f2",
                            }} height={55} w={{
                                base: "75%",
                                md: "25%"
                            }}
                                placeholder="用户名" />

                            <Input borderRadius={14} style={styles.password} _light={{
                                // bg: "#f2f2f2",
                            }} height={55} w={{
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
                            <Button style={styles.loginBtn} borderRadius={12} height={55} _text={{ fontSize: 20 }}>登录</Button>
                            <HStack space={10} justifyContent='center' alignItems="center" style={styles.threePartyLogin}>
                                <Box style={styles.threePartyLoginBox} p="2">
                                    <GoogleIcon size={35} />
                                </Box>
                                <Box style={styles.threePartyLoginBox} p="2">
                                    <AntDesignIcon name="github" size={35} />
                                </Box>
                                <Box style={styles.threePartyLoginBox} p="2">
                                    <MicrosoftIcon size={35} />
                                </Box>
                            </HStack>
                        </VStack>
                        <VStack style={styles.loginBottom} space={5} justifyContent='flex-end' alignItems="center">
                            <Text style={styles.loginBottomText}>没有账号?<Text style={styles.loginBottomCreateAccount}>创建一个</Text></Text>
                        </VStack>
                    </Flex>
                </Flex>
            </NativeBaseProvider>
        </>
    )
}


const styles = StyleSheet.create({
    tabs: {
        backgroundColor: 'red',
    },
    containerH: {
        flex: 1,
        backgroundColor: '#ffffff'
    },
    containerV: {
        flex: 1,
        marginTop: 50
    },
    loginHead:{
        flex: 1,
        width: '100%',
    },
    username: {
        fontSize: 18
    },
    password: {
        fontSize: 18
    },
    forgotPassword: {
        color: '#958d99',
        fontSize: 15
    },
    loginBtn: {
        backgroundColor: "#70BFFF"
    },
    threePartyLogin: {
        padding: 10
    },
    threePartyLoginBox: {
        borderColor: '#f3f0f3',
        borderWidth: 2,
        borderRadius: 100
    },
    loginBottom:{
        flex:1,
        paddingBottom: 20,
    },
    loginBottomText: {
        fontSize: 16
    },
    loginBottomCreateAccount: {
        color: "#70BFFF"
    }
})


export default Login;