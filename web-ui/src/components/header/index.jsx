import React from 'react';
import { useSelector } from 'react-redux';
import { SearchOutlined } from '@ant-design/icons';
import { Flex, Input } from "antd";
import './index.less';
import closeImg from '../../assets/img/close_16.png';
import { logout } from '../../api/ApiService';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';

const Header = React.memo(({ panel }) => {

    //组件跳转
    const navigate = useNavigate();

    const selectedHeadName = useSelector(state => state.chat.selectedHeadName) || ''

    //退出登录
    const logoutHandler = () => {
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

    return (
        <Flex flex={1}>
            <Flex className='search-head-flex' justify='center' align='end' style={{ width: '25%' }}>
                <Input className='chat-search' size="small" placeholder="搜索" prefix={<SearchOutlined />} />
            </Flex>
            <Flex style={{ width: '75%', borderLeft: '1px solid lightgray' }}>
                <Flex style={{ width: '60%', height: '100%' }} align='center'>
                    <div>
                        <label className='header-right-name'>{panel === 'CHAT_PANEL' ? selectedHeadName : ''}</label>
                    </div>
                </Flex>
                <Flex align='end' justify='start' style={{ width: '40%' }} vertical>
                    <div className='close-chat' onClick={logoutHandler}>
                        <img src={closeImg} alt='关闭' style={{ width: '20px', height: '20px' }} />
                    </div>
                </Flex>
            </Flex>
        </Flex>
    )
})

export default Header