import React, { useEffect, useState } from 'react'
import { LoadingOutlined } from '@ant-design/icons';
import './index.less'
import { Flex, Progress, Spin } from 'antd'
import { SystemIcon } from '../customIcon';


const LoggingIn = () => {

    const [percent, setPercent] = useState(0)

    useEffect(() => {
        const interval = setInterval(() => {
            setPercent((prevPercent) => {
                if (prevPercent >= 90) {
                    clearInterval(interval); // 停止进度条
                    return 90;
                }
                return prevPercent + 1; // 每次增加1%
            });
        }, 100); // 每100ms增加1%

        return () => clearInterval(interval); // 清理定时器
    }, [])

    return (
        <Flex
            gap={20}
            style={{
                width: '350px',
                height: '600px',
            }}
            justify='center'
            align='center'
            vertical
        >
            <SystemIcon size={100} />
            <div
                style={{
                    color: 'black',
                    fontSize: 20,
                    fontWeight: 'bold'
                }}
            >
                正在登录
            </div>
            <Flex>
                <Progress percent={percent} status="active" showInfo={false} size={{ width: 240, height: 5 }} strokeColor='#60A4FF' />
            </Flex>
        </Flex>
    )
}

export default LoggingIn

