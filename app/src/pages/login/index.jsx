import { Input, Flex, Pressable, NativeBaseProvider, Text, VStack, Button, Checkbox, HStack, Center, Box } from 'native-base';
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
                        {/* <VStack height="75%">
                            <TabView
                                style={styles.tabs}
                                navigationState={{ index, routes }}
                                renderScene={renderScene}
                                onIndexChange={setIndex}
                                initialLayout={{ width: layout.width / 3 * 2,height: 0 }}
                            />
                        </VStack> */}
                        <VStack>
                            
                        </VStack>
                        <VStack space={5}>
                            <Input borderRadius={14} style={styles.username} shadow={5} _light={{
                                bg: "#f2f2f2",
                            }} height={55} w={{
                                base: "75%",
                                md: "25%"
                            }}
                                placeholder="用户名" />

                            <Input borderRadius={14} style={styles.password} shadow={5} _light={{
                                bg: "#f2f2f2",
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
                        </VStack>
                        <HStack space={10} alignItems="center" style={styles.threePartyLogin}>
                            <Box style={styles.threePartyLoginBox} p="3">
                                <GoogleIcon size={35} />
                            </Box>
                            <Box style={styles.threePartyLoginBox} p="3">
                                <AntDesignIcon name="github" size={35} />
                            </Box>
                            <Box style={styles.threePartyLoginBox} p="3">
                                <MicrosoftIcon size={35} />
                            </Box>
                        </HStack>
                        <HStack>
                            <Text>没有账号?创建一个</Text>
                        </HStack>
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
        flex: 1
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
        backgroundColor: "#6c56f2"
    },
    threePartyLogin: {
        padding: 10,
    },
    threePartyLoginBox: {
        borderColor: '#f3f0f3',
        borderWidth: 2,
        borderRadius: 12
    }
})


export default Login;