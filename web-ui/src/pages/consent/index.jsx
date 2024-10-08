import React, { } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Flex, Card, Button, message } from "antd"
import ychatImag from '../../assets/img/taskbar_icon.png'
import Cookies from 'js-cookie'
import './index.less'
import httpWrapper from '../../api/axiosWrapper'
import {urlParamParse} from '../../utils'

const Consent = () => {
    //路由参数
    const [params] = useSearchParams();


    const consent = () => {
        const pm = urlParamParse(params);
        const token = Cookies.get("accessToken");
        let requestUrl;
        let requestData = new FormData();
        if(pm.type === 'code'){
            requestUrl = httpWrapper.getUri() + '/oauth2/authorize?access_token=' + token;
        }else if(pm.type === 'device'){
            requestUrl = httpWrapper.getUri() + '/oauth2/device_verification?access_token=' + token;
            requestData.append('user_code',pm.user_code);
        }else{
            message.error("未知的类型");
            return;
        }
        const scopes = pm.scope.split('+');
        requestData.append('client_id',pm.client_id);
        requestData.append('state',pm.state);
        scopes.forEach(s => {
            requestData.append('scope',s);
        });
        fetch(requestUrl,{
            method: 'post',
            redirect: "follow",
            body: requestData
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
            <Flex style={{ height: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical style={{ width: '100%' }}>
                    <Card title={<span><img style={{ height: 25, width: 25 }} src={ychatImag} alt='' /><span>使用Ychat账号登录</span></span>} style={{ width: 1000, height: 380, borderRadius: "20px", margin: "20px", overflow: "hidden" }}>
                        <Flex gap='middle' style={{ height: '100%', width: '100%' }}>
                            <Flex style={{ width: '50%' }} vertical>
                                <Flex style={{ width: '100%', height: '70%' }}>
                                    <p>right</p>
                                </Flex>
                                <Flex style={{ width: '100%', height: '30%' }}>
                                    <Button style={{ width: '100%', borderRadius: "20px", borderColor: 'black' }} size="large">
                                        <span style={{ color: '#0b57d0' }}>取消</span>
                                    </Button>
                                </Flex>
                            </Flex>
                            <Flex style={{ width: '50%' }} vertical>
                                <Flex style={{ width: '100%', height: '70%' }}>
                                    <p>right</p>
                                </Flex>
                                <Flex style={{ width: '100%', height: '30%' }}>
                                    <Button style={{ width: '100%', borderRadius: "20px", borderColor: 'black' }} size="large" onClick={consent}>
                                        <span style={{ color: '#0b57d0' }}>同意</span>
                                    </Button>
                                </Flex>
                            </Flex>
                        </Flex>
                    </Card>
                </Flex>
            </Flex>
        </>
    );
}

export default Consent;