import React, { useState, useEffect, useContext,forwardRef,useImperativeHandle, useRef } from 'react'
import { Flex, Tabs, Avatar, Divider, Dropdown } from "antd"
import { HomeContext } from '../../context';
import httpWrapper from '../../api/axiosWrapper';
import femaleImg from '../../assets/img/gender_female.png'
import maleImg from '../../assets/img/gender_male.png'
import moreOpsImg from '../../assets/img/more_ops_icon.png'
import userFGImg from '../../assets/img/user_fg_send_message.png'
import './index.less'

const Friend = forwardRef((props,ref) => {
    const {style} = props;

    const { addUserSession } = useContext(HomeContext);

    const [data, setData] = useState([]);

    const dataRef = useRef(new Map());

    const [selectTab, setSelectTab] = useState(null);

    useEffect(() => {
        httpWrapper.get('/social/userFriend/{userId}')
            .then(
                (res) => {
                    setData(res?.data);
                }
            )
    }, [])

    useEffect(() => {
        const map = new Map();
        data.forEach(item => map.set(item.id, item));
        dataRef.current = map;
    },[data])

    useImperativeHandle(ref, () => ({
        findUserInfoByFriendId: findUserInfoByFriendId
    }));

    const findUserInfoByFriendId = (id) => {
        return dataRef.current.get(id);
    }

    const handleSendMessage = (item) => {
        item.type = 'SINGLE';
        addUserSession(item);
    }

    return (
        <>
            <Flex className='friend-root-flex' justify='center' align='center' style={{ height: '100%', width: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <div className='friend-panel-tabs' style={style}>
                        <Tabs
                            activeKey={selectTab}
                            onChange={(key) => setSelectTab(key)}
                            key="friend-tabs"
                            className='friend-tabs'
                            tabPosition='left'
                            indicator={{ size: 0 }}
                            centered
                            tabBarGutter={0}
                            items={data.map((item, i) => {
                                return {
                                    label: (
                                        <Flex align='center' justify='center' style={{padding:'3px'}}>
                                            <Avatar shape="square" size={50} src={item.avatar} />
                                            <label className='friend-name'>{item.note ? item.note : item.nickname}</label>
                                        </Flex>
                                    ),
                                    key: item.id,
                                    children: (
                                        <Flex justify='center' style={{ width: '100%' }}>
                                            <Flex style={{ width: '50%' }} vertical>
                                                <Flex gap="middle" className='friend-content' justify='start' style={{ width: '100%' }}>
                                                    <div className='friend-content-div'>
                                                        <Avatar shape="square" size={64} src={item.avatar} />
                                                    </div>
                                                    <Flex style={{ width: '100%' }} vertical>
                                                        <Flex gap="small" align='center' style={{ width: '100%' }}>
                                                            <label className='friend-content-nickname'>{item.note ? item.note : item.nickname}</label>
                                                            <img src={item.gender === 'MALE' ? maleImg : femaleImg} style={{ width: '16px', height: '16px' }} alt='' />
                                                            <Flex align='end' justify='end' style={{ flex: 1 }}>
                                                                <Dropdown
                                                                    menu={{
                                                                        items: [
                                                                            {
                                                                                label: '设置备注',
                                                                                key: '0'
                                                                            },
                                                                            {
                                                                                label: '删除联系人',
                                                                                key: '1'
                                                                            }
                                                                        ],
                                                                    }}
                                                                    trigger={['click']}
                                                                >
                                                                    <img onClick={(e) => e.preventDefault()} className='friend-more-ops-img' style={{ width: '16px', height: '16px' }} src={moreOpsImg} alt='' />
                                                                </Dropdown>
                                                            </Flex>
                                                        </Flex>
                                                        <label className='friend-content-label'>昵称：{item.nickname}</label>
                                                        <label className='friend-content-label'>账号：{item.account}</label>
                                                        <label className='friend-content-label'>地区：{item.region}</label>
                                                    </Flex>
                                                </Flex>
                                                <Divider className='friend-content-divider' />
                                                <Flex gap="middle" align='center' justify='start'>
                                                    <div className='friend-content-div'>
                                                        <label className='friend-content-remark-label'>备注</label>
                                                    </div>
                                                    <label className='friend-content-remark-value'>{item.note}</label>
                                                </Flex>
                                                <Divider />
                                                <Flex gap="middle" align='center' justify='start'>
                                                    <div className='friend-content-div'>
                                                        <label className='friend-content-remark-label'>个性签名</label>
                                                    </div>
                                                    <label className='friend-content-remark-value'>{item.tagline}</label>
                                                </Flex>
                                                <Divider />
                                                <Flex align='center' justify='center' style={{ marginTop: '30px' }}>
                                                    <Flex className='friend-content-send-flex' align='center' justify='center' onClick={() => handleSendMessage(item)} vertical>
                                                        <img className='friend-content-send-img' src={userFGImg} alt='' />
                                                        <label className='friend-content-send-label'>发消息</label>
                                                    </Flex>
                                                </Flex>
                                            </Flex>
                                        </Flex>
                                    )
                                };
                            })}
                        />
                    </div>
                </Flex>
            </Flex>
        </>
    );
})

export default Friend;