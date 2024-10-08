import React, { } from 'react'
import { Flex, Button, Image, Input, Form,message } from "antd"
import deviceImg from '../../assets/img/devices.png'
import Cookies from 'js-cookie'
import httpWrapper from '../../api/axiosWrapper'

const Activate = () => {
    const [form] = Form.useForm();

    const active = (values) => {
        const token = Cookies.get("accessToken");
        const activeCode = values.activeCode;
        const requestUrl = httpWrapper.getUri() + '/oauth2/device_verification?user_code=' + activeCode + '&access_token=' + token;
        fetch(requestUrl,{
            method: 'get',
            redirect: "follow"
        }).then(
            res => {
                if(res.redirected){
                    window.location.href = res.url;
                }
            }
        ).catch(
            err => {
                console.info(err + " url: " + requestUrl);
                message.error(err.message);
            }
        );
    }

    return (
        <>
            <Flex gap='middle' justify='center' align='start' style={{ width: '90%' }}>
                <Flex gap='middle' vertical style={{ width: '35%', paddingTop: '30px' }}>
                    <Flex vertical>
                        <h1 style={{ marginBottom: '10px' }}>设备激活</h1>
                        <p style={{ marginTop: '0px' }}>输入激活码对设备进行授权</p>
                    </Flex>
                    <Flex gap='middle' vertical>
                        <label>激活码</label>
                        <Form form={form} name="normal_login" onFinish={active}>
                            <Form.Item name="activeCode" rules={[
                                {
                                    required: true,
                                    message: '请填写激活码'
                                }
                            ]}>
                                <Input size="large" style={{ width: '100%' }} />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit" style={{ width: '20%' }} size="large">
                                    提交
                                </Button>
                            </Form.Item>
                        </Form>

                    </Flex>
                </Flex>
                <Flex vertical style={{ paddingTop: '50px' }}>
                    <Image src={deviceImg} preview={false} />
                </Flex>
            </Flex>
        </>
    )
}

export default Activate;