import React, { useState, useEffect, useContext,forwardRef,useRef,useImperativeHandle } from 'react'
import { Flex, Dropdown, Tabs, Avatar, Divider } from "antd"
import { HomeContext } from '../../context';
import httpWrapper from '../../api/axiosWrapper';
import femaleImg from '../../assets/img/gender_female.png'
import maleImg from '../../assets/img/gender_male.png'
import moreOpsImg from '../../assets/img/more_ops_icon.png'
import userFGImg from '../../assets/img/user_fg_send_message.png'
import './index.less'

const Group = forwardRef((props,ref) => {

    const {style} = props;

    const {addUserSession} = useContext(HomeContext);

    const [data, setData] = useState([]);

    const dataRef = useRef(new Map());

    const [selectTab, setSelectTab] = useState(null);

    useEffect(() => {
        httpWrapper.get('/social/userGroup/{userId}')
            .then(
                (res) => {
                    setData(res?.data);
                }
            )
    }, [])

    useEffect(() => {
        const map = new Map();
        for(const group of data){
            for(const userInfo of group.groupUserInfos){
                map.set(group.id.toString()  + userInfo.id,userInfo).toString();
            }
        }
        dataRef.current = map;
    },[data])

    useImperativeHandle(ref, () => ({
        findUserInfoByGroup: findUserInfoByGroup,
        findGroupByGroupId: findGroupByGroupId
    }));

    const findUserInfoByGroup = (groupId,id) => {
        return dataRef.current.get(groupId.toString() + id.toString());
    }

    const findGroupByGroupId = (groupId) => {
        return data.find(f => f.id = groupId);
    }

    const handleSendMessage = (item) => {
        item.type = 'GROUP';
        addUserSession(item);
    }

    return (
        <>
            <Flex className='group-root-flex' justify='center' align='center' style={{ height: '100%', width: '100%' }}>
                <Flex gap='middle' justify='center' align='center' vertical>
                    <div className='group-panel-tabs' style={style}>
                        <Tabs
                            activeKey={selectTab}
                            onChange={(key) => setSelectTab(key)}
                            key="group-tabs"
                            className='group-tabs'
                            tabPosition='left'
                            indicator={{ size: 0 }}
                            centered
                            tabBarGutter={0}
                            items={data.map((item, i) => {
                                return {
                                    label: (
                                        <Flex align='center' justify='center' style={{padding:'3px'}}>
                                            <Avatar shape="square" size={50} src={item.avatar} />
                                            <label className='group-name'>{item.groupName}</label>
                                        </Flex>
                                    ),
                                    key: item.id,
                                    children: (
                                        <Flex justify='center' style={{ width: '100%' }}>
                                            <Flex style={{ width: '50%' }} vertical>
                                                <Flex gap="middle" className='group-content' justify='start' style={{ width: '100%' }}>
                                                    <div className='group-content-div'>
                                                        <Avatar shape="square" size={64} src={item.avatar} />
                                                    </div>
                                                    <Flex style={{ width: '100%' }} vertical>
                                                        <Flex gap="small" align='center' style={{ width: '100%' }}>
                                                            <label className='group-content-nickname'>{item.groupName}</label>
                                                            <Flex align='end' justify='end' style={{ flex: 1 }}>
                                                            <Dropdown
                                                                    menu={{
                                                                        items: [
                                                                            {
                                                                                label: '设置备注',
                                                                                key: '0'
                                                                            },
                                                                            {
                                                                                label: '删除群聊',
                                                                                key: '1'
                                                                            }
                                                                        ],
                                                                    }}
                                                                    trigger={['click']}
                                                                >
                                                                    <img className='group-more-ops-img' style={{ width: '16px', height: '16px' }} src={moreOpsImg} alt='' />
                                                                </Dropdown>
                                                            </Flex>
                                                        </Flex>
                                                        <label className='group-content-label'>昵称：{item.groupName}</label>
                                                    </Flex>
                                                </Flex>
                                                <Divider className='group-content-divider' />
                                                <Flex gap="middle" align='center' justify='start'>
                                                    <div className='group-content-div'>
                                                        <label className='group-content-remark-label'>备注</label>
                                                    </div>
                                                    <label className='group-content-remark-value'>{item.groupName}</label>
                                                </Flex>
                                                <Divider />
                                                <Flex align='center' justify='center' style={{ marginTop: '30px' }}>
                                                    <Flex className='group-content-send-flex' align='center' justify='center' onClick={() => handleSendMessage(item)} vertical>
                                                        <img className='group-content-send-img' src={userFGImg} alt='' />
                                                        <label className='group-content-send-label'>发消息</label>
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

export default Group;