import { Input, Flex, Pressable, NativeBaseProvider, Text, VStack, Button, Checkbox, HStack, Center, Box, Image, FormControl, KeyboardAvoidingView, ScrollView } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet, useWindowDimensions } from 'react-native';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { GoogleIcon, MicrosoftIcon } from '../../component/CustomIcon';
import { useForm, Controller } from 'react-hook-form';
import api from '../../api/api';
import { showToast } from '../../component/Utils';

const Login = () => {
    const [show, setShow] = useState(false);

    const layout = useWindowDimensions();

    const { control, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            username: "",
            password: ""
        }
    });

    const onSubmit = (data) => {
        console.log('submiting with ', data);
        let loginRequest = {
            username: data.username,
            credential: data.password,
            loginType: 'NORMAL',
        }
        fetch('http://10.0.2.2:9090/login',{
            method: 'post',
            headers: {'content-type': 'application/json'},
            body: JSON.stringify(loginRequest)
        }).then(
            (res) => {
                res.json().then(r => {
                    console.log(r)
                })
            }
        )
        // api
        // .post('/login',loginRequest)
        // .then(
        //     (res) => {
        //         console.log('login result: ',res.data)
        //     },
        //     (error) => {
        //         if (error.response && error.response.status === 401) {
        //             showToast('用户名或密码错误')
        //         }
        //     }
        // )
    };

    return (
        <>
            <Flex style={styles.containerH} alignItems='center' direction="row">
                <ScrollView>
                    <Flex gap={20} style={styles.containerV} alignItems='center' direction="column">
                        <VStack alignItems="center" justifyContent="flex-end" style={styles.loginHead}>
                            <Image alt='' source={require('../../assets/img/icon.png')} />
                        </VStack>
                        <VStack style={{ flex: 1 }} space={5}>
                            <VStack style={{ flex: 1 }} space={8}>
                                <FormControl isRequired isInvalid={errors?.username}>
                                    <Controller
                                        control={control}
                                        render={({ field: { onChange, onBlur, value } }) => (
                                            <Input
                                                onBlur={onBlur}
                                                value={value}
                                                onChangeText={onChange}
                                                borderRadius={14}
                                                style={styles.usernameInput}
                                                height={55}
                                                w={{
                                                    base: "100%",
                                                    md: "25%"
                                                }}
                                                placeholder="用户名" />
                                        )}
                                        name="username"
                                        rules={{ required: '请输入用户名' }}
                                    />
                                    <FormControl.ErrorMessage style={styles.errorMessage}>
                                        {errors?.username?.message}
                                    </FormControl.ErrorMessage>
                                </FormControl>

                                <FormControl isRequired isInvalid={errors?.password}>
                                    <Controller
                                        control={control}
                                        render={({ field: { onChange, onBlur, value } }) => (
                                            <Input
                                                onBlur={onBlur}
                                                value={value}
                                                onChangeText={onChange}
                                                borderRadius={14}
                                                style={styles.passwordInput}
                                                height={55}
                                                w={{
                                                    base: "100%",
                                                    md: "25%"
                                                }}
                                                InputRightElement={
                                                    <Pressable style={{ marginRight: 8 }} onPress={() => setShow(!show)}>
                                                        <MaterialIcon name={show ? "visibility" : "visibility-off"} size={28} mr="2" color="gray" />
                                                    </Pressable>
                                                }
                                                type={show ? "text" : "password"}
                                                placeholder="密码" />
                                        )}
                                        name="password"
                                        rules={{ required: '请输入密码' }}
                                    />
                                    <FormControl.ErrorMessage style={styles.errorMessage}>
                                        {errors?.password?.message}
                                    </FormControl.ErrorMessage>
                                </FormControl>
                            </VStack>
                            <HStack justifyContent="space-between">
                                <Text style={styles.forgotPassword}>使用短信验证码</Text>
                                <Text style={styles.forgotPassword}>忘记密码?</Text>
                            </HStack>
                            <Button style={styles.loginBtn} onPress={handleSubmit(onSubmit)} borderRadius={12} height={55} _text={{ fontSize: 20 }}>登录</Button>
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
                </ScrollView>
            </Flex>
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
    },
    loginHead: {
        flex: 1,
        width: '100%',
    },
    usernameInput: {
        fontSize: 18
    },
    passwordInput: {
        fontSize: 18
    },
    errorMessage: {
        position: 'absolute',
        top: '100%',
        left: 0,
        fontSize: 12
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
    loginBottom: {
        flex: 1,
    },
    loginBottomText: {
        fontSize: 16
    },
    loginBottomCreateAccount: {
        color: "#70BFFF"
    }
})


export default Login;