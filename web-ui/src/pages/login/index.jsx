import React, { useState, useEffect, useLayoutEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Button, Flex, Tabs, Form, Input, Checkbox, Avatar, message } from "antd"
import Icon, { UserOutlined, LockOutlined, MobileOutlined, MailOutlined, GithubOutlined, GoogleOutlined } from '@ant-design/icons'
import { MicrosoftOutlined, SelfOutlined } from '../../components/customIcon'
import Cookies from 'js-cookie'
import './index.less'
import 'antd/dist/reset.css'
import tmpImg from '../../assets/img/tmp.jpg'
import TabPane from 'antd/es/tabs/TabPane'
import httpWrapper from '../../api/axiosWrapper'
import { urlParamParse } from '../../utils'


const Login = () => {
    const [form] = Form.useForm();
    //组件跳转
    const navigate = useNavigate();
    //登录按钮加载状态
    const [loading, setLoading] = useState(false);
    //路由参数
    const [params] = useSearchParams();
    //登录方式
    const [loginMethod, setLoginMethod] = useState("1");
    //三方登录配置
    const [otherLoginConfig, setOtherLoginConfig] = useState({
        Google: '',
        Github: '',
        Microsoft: '',
        Self: ''
    });
    //验证码设置
    const [verificationCode, setVerificationCode] = useState({
        disabled: false,
        tips: '获取验证码',
        time: 60
    });

    //其它登录方式回调处理
    useLayoutEffect(() => {
        const code = params.get('code');
        const state = params.get('state');
        if (code && state) {
            setLoading(true);
            httpWrapper
                .get('/oauth/other/login', {
                    params: {
                        code: code,
                        state: state
                    }
                })
                .then(
                    (res) => {
                        loginSuccessHandler(res.data, null);
                    }
                );
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        //获取三方登录的配置信息
        httpWrapper
            .get('/oauth/other/config')
            .then(
                (res) => {
                    setOtherLoginConfig({
                        Google: res.data?.Google,
                        Github: res.data?.Github,
                        Microsoft: res.data?.Microsoft,
                        Self: res.data?.Self
                    })
                },
                (err) => {
                    console.log(err)
                }
            );
    }, []);

    useEffect(() => {
        const rememberMeToken = localStorage.getItem('rememberMeToken');
        if (rememberMeToken) {
            const rememberMeStr = atob(rememberMeToken);
            const loginInfoArray = rememberMeStr.split(":");
            form.setFieldsValue({
                username: loginInfoArray[0]
            })
        }
    }, [form])

    const emailVerification = (_, val) => {
        if (loginMethod === "1") {
            return Promise.resolve();
        }
        // const phoneReg = /^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[235-8]\d{2}|4(?:0\d|1[0-2]|9\d))|9[0-35-9]\d{2}|66\d{2})\d{6}$/;
        const emailReg = /^\w+(-+.\w+)*@\w+(-.\w+)*.\w+(-.\w+)*$/;
        const validateResult = emailReg.test(val)
        if (!validateResult) {
            return Promise.reject("邮箱不合法");
        }
        return Promise.resolve();
    }
    // 发送验证码
    const handleWithVerificationCode = async () => {
        const email = form.getFieldValue('email');
        if(!email){
            return message.error("邮箱不可为空");
        }
        httpWrapper
            .get('/sendEmailVerificationCode', {
                params: {
                    email: email
                }
            });
        let ti = verificationCode.time;
        setVerificationCode({
            disabled: true,
            tips: `${ti} 秒后重新获取`,
        });
        const timer = setInterval(() => {
            if (--ti > 0) {
                setVerificationCode({
                    disabled: true,
                    tips: `${ti} 秒后重新获取`,
                });
            } else {
                clearInterval(timer);
                setVerificationCode({
                    disabled: false,
                    tips: '获取验证码',
                    time: 5
                });
            }
        }, 1000);
    }
    //github登录
    const githubLogin = () => {
        console.log("github登录", otherLoginConfig.Github)
        if (otherLoginConfig.Github) {
            window.location.href = otherLoginConfig.Github.url;
        } else {
            message.error('获取Github登录配置失败,请刷新页面后重试');
        }
    }
    //google登录
    const googleLogin = () => {
        console.log("google登录", otherLoginConfig.Google)
        if (otherLoginConfig.Google) {
            window.location.href = otherLoginConfig.Google.url;
        } else {
            message.error('获取Google登录配置失败,请刷新页面后重试');
        }
    }
    //microsoft登录
    const microsoftLogin = () => {
        console.log("microsoft登录", otherLoginConfig.Microsoft)
        if (otherLoginConfig.Microsoft) {
            window.location.href = otherLoginConfig.Microsoft.url;
        } else {
            message.error('获取Microsoft登录配置失败,请刷新页面后重试');
        }
    }
    //self登录
    const selfLogin = () => {
        console.log("self登录", otherLoginConfig.Self)
        const authCodeLogin = () => {
            window.location.href = otherLoginConfig.Self.url;
        }
        const deviceCodeLogin = () => {
            let url = otherLoginConfig.Self.url.toString();
            const params = urlParamParse(url);
            let requestUrl = url.substring(0, url.indexOf('?'));
            let requestData = new FormData();
            requestData.append('client_id', params.client_id);
            requestData.append('client_secret', params.client_secret);
            requestData.append('scope', params.scope);
            fetch(requestUrl, {
                method: 'post',
                redirect: "follow",
                body: requestData
            })
                .then(
                    (res) => {
                        res.json().then(r => {
                            window.location.href = r.verification_uri;
                        });

                    }
                )
        }
        if (otherLoginConfig.Self.type === 'auth_code') {
            authCodeLogin();
        } else {
            deviceCodeLogin();
        }
    }

    //表单提交
    const onFinish = (values) => {
        let loginRequest;
        if (loginMethod === "1") {
            loginRequest = {
                username: values.username,
                credential: values.password,
                loginType: 'NORMAL',
                rememberMe: values.rememberMe ? 1 : null
            }
        } else {
            loginRequest = {
                username: values.email,
                credential: values.verificationCode,
                loginType: 'EMAIL',
                rememberMe: values.rememberMe ? 1 : null
            }
        }
        setLoading(true);
        httpWrapper
            .post("/login", loginRequest)
            .then(
                (res) => {
                    loginSuccessHandler(res.data, values.rememberMe);
                },
                (error) => {
                    setLoading(false)
                    if (error.response && error.response.status === 401) {
                        message.error('身份认证失败');
                    }
                }
            )

    }

    const loginSuccessHandler = (data, rememberMe) => {
        setLoading(false)
        if (rememberMe) {
            localStorage.setItem('rememberMeToken', data.rememberMeToken)
        }
        Cookies.set('accessToken', data.accessToken)
        Cookies.set('refreshToken', data.refreshToken)
        //oauth授权码登录跳转
        if (params.get('target')) {
            const urlObj = new URL(params.get('target'));
            urlObj.searchParams.set('access_token', data.accessToken);
            const target = urlObj.toString();
            window.location.href = target;
        } else {
            navigate('/home')
        }
    }

    return (
        <>
            <Flex style={{ height: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical style={{ width: '100%' }}>
                    <Avatar size={100} src={tmpImg} draggable={false} />
                    <Form form={form} name="normal_login" className="login-form" style={{ width: '350px' }} onFinish={onFinish}>
                        <Tabs defaultActiveKey="1" centered onChange={(e) => setLoginMethod(e)}>
                            <TabPane tab="账号密码登录" key="1">
                                <Form.Item name="username" rules={[
                                    {
                                        required: loginMethod === '1',
                                        message: '用户名不可为空'
                                    }
                                ]}>
                                    <Input allowClear size="large" placeholder="用户名" prefix={<UserOutlined />} />
                                </Form.Item>
                                <Form.Item name="password" rules={[
                                    {
                                        required: loginMethod === '1',
                                        message: '密码不可为空'
                                    }
                                ]}>
                                    <Input.Password size="large" placeholder="密码" prefix={<LockOutlined />} />
                                </Form.Item>
                            </TabPane>
                            <TabPane tab="邮箱登录" key="2">
                                <Form.Item name="email" validateTrigger="onBlur" rules={[
                                    {
                                        validator: emailVerification
                                    }
                                ]}>
                                    <Input allowClear size="large" placeholder="邮箱" prefix={<MobileOutlined />} />
                                </Form.Item>
                                <Flex gap='small'>
                                    <Form.Item name="verificationCode" rules={[
                                        {
                                            required: loginMethod === '2',
                                            message: '验证码不可为空'
                                        }
                                    ]}>
                                        <Input allowClear size="large" placeholder="请输入验证码!" prefix={<MailOutlined />} />
                                    </Form.Item>
                                    <Button disabled={verificationCode.disabled} size="large" onClick={handleWithVerificationCode}>{verificationCode.tips}</Button>
                                </Flex>
                            </TabPane>
                        </Tabs>
                        <Form.Item>
                            <Form.Item name="rememberMe" valuePropName="checked" initialValue={true} noStyle>
                                <Checkbox>记住密码</Checkbox>
                            </Form.Item>
                            <a className="login-form-forgot" style={{ float: 'right' }} href="/">
                                忘记密码 ？
                            </a>
                        </Form.Item>
                        <Form.Item>
                            <Button type="primary" htmlType="submit" style={{ width: '100%' }} size="large" loading={loading}>
                                登录
                            </Button>
                        </Form.Item>
                        <Flex gap='large' align='center'>
                            其他登录方式
                            <GithubOutlined style={{ fontSize: '24px', color: 'gray' }} onClick={githubLogin} />
                            <GoogleOutlined style={{ fontSize: '24px', color: 'gray' }} onClick={googleLogin} />
                            <Icon component={MicrosoftOutlined} style={{ fontSize: '24px', color: 'gray' }} onClick={microsoftLogin} />
                            <Icon component={SelfOutlined} style={{ fontSize: '24px', color: 'gray' }} onClick={selfLogin} />
                        </Flex>
                    </Form>
                </Flex>
            </Flex>
        </>
    );
}

export default Login;