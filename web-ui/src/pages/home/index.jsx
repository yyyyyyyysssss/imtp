import { Flex, Tabs } from "antd";
import React, { useEffect, useLayoutEffect, useRef } from 'react';
import homeChatIcon from '../../assets/img/home_chat_icon.png';
import homeChatIconSelected from '../../assets/img/home_chat_icon_selected.png';
import homeFriendIcon from '../../assets/img/home_friend_icon.png';
import homeFriendIconSelected from '../../assets/img/home_friend_icon_selected.png';
import homeGroupIcon from '../../assets/img/home_group_icon.png';
import homeGroupIconSelected from '../../assets/img/home_group_icon_selected.png';
import { HomeContext } from '../../context';
import Chat from '../chat';
import Friend from '../friend';
import Group from '../group';
import './index.less';
import { useDispatch, useSelector } from 'react-redux';
import { setUserInfo,switchPanel } from '../../redux/slices/chatSlice';
import { fetchUserInfo } from '../../api/ApiService';
import Header from '../../components/header';

const { TabPane } = Tabs;

const CHAT_PANEL = "CHAT_PANEL";
const FRIEND_PANEL = "FRIEND_PANEL";
const GROUP_PANEL = "GROUP_PANEL";

const Home = () => {
    const dispatch = useDispatch()

    const userInfo = useSelector(state => state.chat.userInfo) || {}

    const panel = useSelector(state => state.chat.panel)

    useLayoutEffect(() => {
        const fetchData = async () => {
            const userInfo = await fetchUserInfo()
            dispatch(setUserInfo({userInfo: userInfo}))
        }
        fetchData()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    },[])
    //好友组件引用
    const friendRef = useRef();
    //群组组件引用
    const groupRef = useRef();

    //切换面板
    const handleSwitchPanel = (panelKey) => {
        dispatch(switchPanel({panel: panelKey}))
    }

    const findUserInfoByGroup = (groupId, id) => {
        return groupRef.current.findUserInfoByGroup(groupId, id);
    }

    const findGroupByGroupId = (groupId) => {
        return groupRef.current.findGroupByGroupId(groupId);
    }

    const findUserInfoByFriendId = (id) => {
        return friendRef.current.findUserInfoByFriendId(id);
    }

    return (
        <>
            <Flex className='chat-root-flex' justify='center' align='center' style={{ height: '100%', width: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <Flex className='root-panel' style={{ height: '750px', width: '1000px' }} vertical>
                        <Flex className='root-header' style={{ height: '65px',width: '100%' }}>
                            <Flex flex={1}>
                                <Flex className='header-sider' justify='center' align='end' style={{ width: '50px' }}>
                                    <img style={{ height: '35px', width: '35px' }} src={userInfo.avatar} alt='' />
                                </Flex>
                                <Flex style={{ width: '950px' }}>
                                    <Header panel={panel}/>
                                </Flex>

                            </Flex>
                        </Flex>
                        <Flex style={{ height: '685px',width: '100%' }}>
                            <HomeContext.Provider value={{ findUserInfoByGroup, findGroupByGroupId, findUserInfoByFriendId }}>
                                <div className='home-panel-tabs'>
                                    <Tabs
                                        key="home-tabs"
                                        activeKey={panel}
                                        tabPosition='left'
                                        indicator={{ size: 0 }}
                                        centered
                                        size='large'
                                        onChange={(key) => handleSwitchPanel(key)}

                                    >
                                        <TabPane forceRender={true} key={CHAT_PANEL} tab={<img className='panel-img-icon' src={panel === CHAT_PANEL ? homeChatIconSelected : homeChatIcon} alt='' />}>
                                            <Chat style={{ height: '685px', width: '950px' }} />
                                        </TabPane>
                                        <TabPane forceRender={true} key={FRIEND_PANEL} tab={<img className='panel-img-icon' src={panel === FRIEND_PANEL ? homeFriendIconSelected : homeFriendIcon} alt='' />} >
                                            <Friend ref={friendRef} style={{ height: '685px', width: '950px' }} />
                                        </TabPane>
                                        <TabPane forceRender={true} key={GROUP_PANEL} tab={<img className='panel-img-icon' src={panel === GROUP_PANEL ? homeGroupIconSelected : homeGroupIcon} alt='' />} >
                                            <Group ref={groupRef} style={{ height: '685px', width: '950px' }} />
                                        </TabPane>
                                    </Tabs>
                                </div>
                            </HomeContext.Provider>
                        </Flex>
                    </Flex>
                </Flex>
            </Flex>
        </>
    );
}

export default Home;