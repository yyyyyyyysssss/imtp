import { Input, Flex, Pressable, Text, VStack, Button, HStack, Box, Image, FormControl, ScrollView, KeyboardAvoidingView, View } from 'native-base';
import React, { useContext, useEffect, useState } from 'react';
import { StyleSheet, Linking } from 'react-native';
import MaterialIcon from 'react-native-vector-icons/MaterialIcons';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';
import { GoogleIcon, MicrosoftIcon } from '../../components/CustomIcon';
import { useForm, Controller } from 'react-hook-form';
import { showToast } from '../../components/Utils';
import { AuthContext } from '../../context';
import { fetchOAuth2ClientConfig, login, loginByGoogle } from '../../api/ApiService';
import {
    GoogleSignin,
    GoogleSigninButton
} from '@react-native-google-signin/google-signin';

const Login = () => {

    const { signIn } = useContext(AuthContext)

    const [show, setShow] = useState(false);

    const [loading, setLoading] = useState(false)

    //三方登录配置
    const [otherLoginConfig, setOtherLoginConfig] = useState({
        Google: null,
        Github: null,
        Microsoft: null
    });

    const { control, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            username: "",
            password: ""
        }
    });

    useEffect(() => {
        //获取三方登录的配置信息
        const otherLoginConfig = async () => {
            const config = await fetchOAuth2ClientConfig()
            setOtherLoginConfig({
                Google: config?.Google,
                Github: config?.Github,
                Microsoft: config?.Microsoft
            })
        }
        otherLoginConfig()

        // 获取应用启动时的深度链接
        const getInitialURL = async () => {
            const url = await Linking.getInitialURL();
            handleUrl({ url: url })
        }
        getInitialURL()

        // 监听深度链接事件（应用在后台启动时）
        const handleUrl = ({ url }) => {
            if (url) {
                console.log(url)
            }
        };

        const linkingSubscription = Linking.addEventListener('url', handleUrl)
        return () => {
            linkingSubscription.remove()
        }
    }, [])

    useEffect(() => {
        if(otherLoginConfig.Google){
            GoogleSignin.configure({
                webClientId: otherLoginConfig.Google.clientId,
                offlineAccess: true
            })
        }
    },[otherLoginConfig])

    const createAccount = () => {
        showToast('Create Account')
    }

    const googleLogin = async () => {
        try {
            setLoading(true)
            await GoogleSignin.hasPlayServices()
            const response = await GoogleSignin.signIn()
            const { serverAuthCode } = response.data
            const userToken = await loginByGoogle(serverAuthCode)
            await signIn(userToken)
        } catch (error) {
            showToast(error.message)
            setLoading(false)
        }

    }

    const githubLogin = () => {
        showToast('Github Login')
    }

    const MicrosoftLogin = () => {
        showToast('Microsoft Login')
    }

    const phoneLogin = () => {
        showToast('Phone Login')
    }

    const forgotPassword = () => {
        showToast('Forgot Password')
    }

    const onSubmit = (data) => {
        let loginRequest = {
            username: data.username,
            credential: data.password,
            loginType: 'NORMAL',
            clientType: 'APP'
        }
        setLoading(true)
        login(loginRequest)
            .then(
                async (data) => {
                    const userToken = data
                    await signIn(userToken)
                },
                (error) => {
                    if (error.response && error.response.status === 401) {
                        showToast('用户名或密码错误')
                    } else {
                        showToast(error.message)
                    }
                    setLoading(false)
                }
            )
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
                                                width={280}
                                                isReadOnly={loading}
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
                                                width={280}
                                                InputRightElement={
                                                    <Pressable style={{ marginRight: 8 }} onPress={() => setShow(!show)}>
                                                        <MaterialIcon name={show ? "visibility" : "visibility-off"} size={28} mr="2" color="gray" />
                                                    </Pressable>
                                                }
                                                type={show ? "text" : "password"}
                                                isReadOnly={loading}
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
                                <Pressable onPress={phoneLogin} isDisabled={loading}>
                                    <Text style={styles.forgotPassword}>使用短信验证码</Text>
                                </Pressable>
                                <Pressable onPress={forgotPassword} isDisabled={loading}>
                                    <Text style={styles.forgotPassword}>忘记密码?</Text>
                                </Pressable>
                            </HStack>
                            <Button isLoading={loading} isLoadingText='登录中' style={styles.loginBtn} onPress={handleSubmit(onSubmit)} borderRadius={12} height={55} _text={{ fontSize: 20 }}>
                                登录
                            </Button>
                            <HStack space={10} justifyContent='center' alignItems="center" style={styles.threePartyLogin}>
                                <Pressable onPress={googleLogin} isDisabled={loading}>
                                    <Box style={styles.threePartyLoginBox} p="2">
                                        <GoogleIcon size={35} />
                                    </Box>
                                </Pressable>
                                <Pressable onPress={githubLogin} isDisabled={loading}>
                                    <Box style={styles.threePartyLoginBox} p="2">
                                        <AntDesignIcon name="github" size={35} />
                                    </Box>
                                </Pressable>

                                <Pressable onPress={MicrosoftLogin} isDisabled={loading}>
                                    <Box onPress={MicrosoftLogin} style={styles.threePartyLoginBox} p="2">
                                        <MicrosoftIcon size={35} />
                                    </Box>
                                </Pressable>
                            </HStack>
                        </VStack>
                        <HStack style={styles.loginBottom} justifyContent='flex-end' alignItems="center">
                            <Text style={styles.loginBottomText}>没有账号?</Text>
                            <Pressable onPress={createAccount} isDisabled={loading}>
                                <Text style={styles.loginBottomCreateAccount}>创建一个</Text>
                            </Pressable>
                        </HStack>
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
        padding: 10,
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