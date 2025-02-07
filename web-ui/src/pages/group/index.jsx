import React, { useState, useEffect, forwardRef, useRef, useImperativeHandle } from 'react'
import { Flex, Dropdown, Tabs, Avatar, Divider } from "antd"
import moreOpsImg from '../../assets/img/more_ops_icon.png'
import userFGImg from '../../assets/img/user_fg_send_message.png'
import './index.less'
import { fetchUserGroups, createUserSession } from '../../api/ApiService';
import { useSelector, useDispatch } from 'react-redux';
import { switchPanel, addSession, loadUserGroup, selectSession } from '../../redux/slices/chatSlice';
import { DeliveryMethod } from '../../enum';

const Group = forwardRef((props, ref) => {

    const { style } = props;

    const dispatch = useDispatch()

    const userGroups = useSelector(state => state.chat.userGroups)

    const sessions = useSelector(state => state.chat.entities.sessions)

    const userInfo = useSelector(state => state.chat.userInfo)

    const userGroupMapRef = useRef(new Map());

    const [selectTab, setSelectTab] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            const userGroupList = await fetchUserGroups() || []
            dispatch(loadUserGroup(userGroupList))
        }
        fetchData()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    useEffect(() => {
        userGroupMapRef.current = new Map()
        for (let userGroup of userGroups) {
            const groupId = userGroup.id
            userGroupMapRef.current.set(groupId, { id: groupId, note: userGroup.note, avatar: userGroup.avatar })
            const { groupUserInfos } = userGroup
            groupUserInfos.forEach(item => userGroupMapRef.current.set(groupId + '-' + item.id, item))
        }
    }, [userGroups])

    useImperativeHandle(ref, () => ({
        findUserInfoByGroup: findUserInfoByGroup,
        findGroupByGroupId: findGroupByGroupId
    }));

    const findUserInfoByGroup = (groupId, friendId) => {
        const key = groupId + '-' + friendId
        return userGroupMapRef.current.get(key)
    }

    const findGroupByGroupId = (groupId) => {

        return userGroupMapRef.current.get(groupId)
    }

    const handleSendMessage = async (item) => {
        const groupId = item.id
        let sessionId;
        const session = Object.values(sessions).find(s => s.receiverUserId === groupId);
        if (session) {
            sessionId = session.id
        } else {
            sessionId = await createUserSession(groupId, DeliveryMethod.GROUP)
            const userSessionItem = {
                id: sessionId,
                userId: userInfo.id,
                name: item.groupName,
                receiverUserId: groupId,
                avatar: item.avatar,
                deliveryMethod: DeliveryMethod.GROUP
            }
            dispatch(addSession({ session: userSessionItem }))
        }
        dispatch(switchPanel({ panel: 'CHAT_PANEL', sessionId: sessionId }))
        dispatch(selectSession({ sessionId: sessionId }))
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
                            items={userGroups.map((item, i) => {
                                return {
                                    label: (
                                        <Flex align='center' justify='center' style={{ padding: '3px' }}>
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