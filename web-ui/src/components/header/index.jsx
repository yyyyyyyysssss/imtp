import React, { useCallback, useState } from 'react';
import { useSelector } from 'react-redux';
import { CloseOutlined, MinusOutlined, SearchOutlined } from '@ant-design/icons';
import { Flex, Input } from "antd";
import './index.less';
import { logout } from '../../api/ApiService';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import { WindowMaximizeOutlined, WindowRecoveryOutlined } from '../customIcon';

const Header = React.memo(({ panel, windowMaximize, windowRecovery }) => {

    //组件跳转
    const navigate = useNavigate();

    const selectedHeadName = useSelector(state => state.chat.selectedHeadName) || ''

    const [windowSizeFlag, setWindowSizeFlag] = useState(false)

    //窗口伸缩
    const resizeWindow = useCallback(() => {
        if (windowSizeFlag) {
            windowRecovery()
        } else {
            windowMaximize()
        }
        setWindowSizeFlag(!windowSizeFlag)
        // eslint-disable-next-line
    }, [windowSizeFlag])

    // 最小化
    const minimize = () => {
        if (window.electronAPI) {
            window.electronAPI.minimizedWindow()
        }
    }

    //退出登录
    const logoutHandler = () => {
        if (window.electronAPI) {
            window.electronAPI.closeWindow()
        } else {
            logout()
                .then(
                    (res) => {
                        Cookies.remove('accessToken');
                        Cookies.remove('refreshToken');
                        navigate('/login')
                    },
                    (error) => {
                        console.log(error)
                    }
                )
        }
    }

    return (
        <Flex flex={1}>
            <Flex className='search-head-flex' justify='center' align='end' style={{ width: '25%',minWidth: '200px', borderRight: '1px solid lightgray' }}>
                <Input className='chat-search' size="small" placeholder="搜索" prefix={<SearchOutlined />} />
            </Flex>
            <Flex flex={1}>
                <Flex style={{ width: '60%', height: '100%' }} align='center'>
                    <div>
                        <label className='header-right-name'>{panel === 'CHAT_PANEL' ? selectedHeadName : ''}</label>
                    </div>
                </Flex>
                <Flex gap={8} align='start' justify='end' style={{ width: '40%' }}>
                    {window.electronAPI && (
                        <div className='window-ops-btn' onClick={minimize}>
                            <MinusOutlined size={20} />
                        </div>
                    )}

                    <div className='window-ops-btn' onClick={resizeWindow}>
                        {windowSizeFlag === true ? (<WindowRecoveryOutlined size={18} />) : (<WindowMaximizeOutlined size={18} />)}
                    </div>
                    <div className='window-ops-btn' onClick={logoutHandler}>
                        <CloseOutlined size={20} />
                    </div>
                </Flex>
            </Flex>
        </Flex>
    )
})

export default Header