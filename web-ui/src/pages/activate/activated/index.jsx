import React, { } from 'react';
import { Flex,Image } from "antd"
import deviceImg from '../../../assets/img/devices.png';

const ActivateSuccess = () => {

    return (
        <>
            <Flex gap='middle' justify='center' align='start' style={{ width: '90%' }}>
                <Flex gap='middle' vertical style={{ width: '35%', paddingTop: '30px' }}>
                    <Flex vertical>
                        <h1 style={{ marginBottom: '10px' }}>成功！</h1>
                        <p style={{ marginTop: '0px' }}>您已成功激活您的设备，请返回到您的设备继续。</p>
                    </Flex>
                </Flex>
                <Flex vertical style={{ paddingTop: '50px' }}>
                    <Image src={deviceImg} preview={false} />
                </Flex>
            </Flex>
        </>
    );
}

export default ActivateSuccess;