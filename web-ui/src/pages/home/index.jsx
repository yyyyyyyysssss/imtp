import React, { useEffect, useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { Flex, Tabs, Input } from "antd"
import { SearchOutlined } from '@ant-design/icons';
import homeChatIcon from '../../assets/img/home_chat_icon.png'
import homeChatIconSelected from '../../assets/img/home_chat_icon_selected.png'
import homeFriendIcon from '../../assets/img/home_friend_icon.png'
import homeFriendIconSelected from '../../assets/img/home_friend_icon_selected.png'
import homeGroupIcon from '../../assets/img/home_group_icon.png'
import homeGroupIconSelected from '../../assets/img/home_group_icon_selected.png'
import Chat from '../chat'
import Friend from '../friend'
import Group from '../group'
import { HomeContext} from '../../context'
import Cookies from 'js-cookie'
import httpWrapper from '../../api/axiosWrapper'
import closeImg from '../../assets/img/close_16.png'
import './index.less'

const { TabPane } = Tabs;

const CHAT_PANEL = "CHAT_PANEL";
const FRIEND_PANEL = "FRIEND_PANEL";
const GROUP_PANEL = "GROUP_PANEL";

const Home = () => {
    const [userInfo,setUserInfo] = useState();
    const [userAvatar,setUserAvatar] = useState();

    useEffect(() => {
        httpWrapper
        .get('/social/userInfo')
        .then(
            (res) => {
                setUserInfo(res.data);
                setUserAvatar(res.data.avatar);
            },
            (error) => {
                console.log(error)
            }
        );
    },[])
    //聊天组件引用
    const chatRef = useRef();
    //好友组件引用
    const friendRef = useRef();
    //群组组件引用
    const groupRef = useRef();
    //设置头部名称
    const [headName, setHeadName] = useState('');
    //组件跳转
    const navigate = useNavigate();
    //当前面板 1 聊天面板 2 好友面板 3 群组面板
    const [panel, setPanel] = useState(CHAT_PANEL);
    //切换面板
    const switchPanel = (panelKey) => {
        setPanel(panelKey)
    }
    //添加会话
    const addUserSession = (user) => {
        switchPanel(CHAT_PANEL);
        chatRef.current.addUserSession(user);
    }
    
    const findUserInfoByGroup = (groupId,id) => {
        return groupRef.current.findUserInfoByGroup(groupId,id);
    }

    const findGroupByGroupId = (groupId) => {
        return groupRef.current.findGroupByGroupId(groupId);
    }

    const findUserInfoByFriendId = (id) => {
        return friendRef.current.findUserInfoByFriendId(id);
    }
    //退出登录
    const logoutHandler = () => {
        httpWrapper
            .post('/logout', {})
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
        <>
            <Flex className='chat-root-flex' justify='center' align='center' style={{ height: '100%', width: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <Flex className='root-panel' style={{ height: '750px', width: '1000px' }} vertical>
                        <Flex className='root-header' style={{ height: '65px' }}>
                            <Flex>
                                <Flex className='header-sider' justify='center' align='end' style={{ width: '50px' }}>
                                    <img style={{ height: '35px', width: '35px' }} src={userAvatar} alt='' />
                                </Flex>
                                <Flex style={{ width: '950px' }}>
                                    <Flex className='search-head-flex' justify='center' align='end' style={{ width: '25%' }}>
                                        <Input className='chat-search' size="small" placeholder="搜索" prefix={<SearchOutlined />} />
                                    </Flex>
                                    <Flex style={{ width: '75%' }}>
                                        <Flex style={{ width: '60%', height: '100%' }} align='center'>
                                            <div>
                                                <label className='header-right-name'>{panel === CHAT_PANEL ? headName : ''}</label>
                                            </div>
                                        </Flex>
                                        <Flex align='end' justify='start' style={{ width: '40%' }} vertical>
                                            <div className='close-chat' onClick={logoutHandler}>
                                                <img src={closeImg} alt='关闭' style={{ width: '20px', height: '20px' }} />
                                            </div>
                                        </Flex>
                                    </Flex>
                                </Flex>

                            </Flex>
                        </Flex>
                        <Flex>
                            <HomeContext.Provider value={{ setHeadName,userInfo, addUserSession,findUserInfoByGroup,findGroupByGroupId,findUserInfoByFriendId }}>
                                {/* <WebSocketProvider> */}
                                    <div className='home-panel-tabs'>
                                        <Tabs
                                            key="home-tabs"
                                            activeKey={panel}
                                            tabPosition='left'
                                            indicator={{ size: 0 }}
                                            centered
                                            size='large'
                                            onChange={(key) => switchPanel(key)}
                                            
                                        >
                                            <TabPane forceRender={true} key={CHAT_PANEL} tab={<img className='panel-img-icon' src={panel === CHAT_PANEL ? homeChatIconSelected : homeChatIcon} alt='' />}>
                                                <Chat ref={chatRef} style={{ height: '685px', width: '950px' }} />
                                            </TabPane>
                                            <TabPane forceRender={true} key={FRIEND_PANEL} tab={<img className='panel-img-icon' src={panel === FRIEND_PANEL ? homeFriendIconSelected : homeFriendIcon} alt='' />} >
                                                <Friend ref={friendRef} style={{ height: '685px', width: '950px' }} />
                                            </TabPane>
                                            <TabPane forceRender={true} key={GROUP_PANEL} tab={<img className='panel-img-icon' src={panel === GROUP_PANEL ? homeGroupIconSelected : homeGroupIcon} alt='' />} >
                                                <Group ref={groupRef} style={{ height: '685px', width: '950px' }} />
                                            </TabPane>
                                        </Tabs>
                                    </div>
                                {/* </WebSocketProvider> */}
                            </HomeContext.Provider>
                        </Flex>
                    </Flex>
                </Flex>
            </Flex>
        </>
    );
}

export default Home;