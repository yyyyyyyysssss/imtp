import { Flex, Layout } from "antd";
import React, { useCallback, useContext, useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AutoSizer, CellMeasurer, CellMeasurerCache, List as VirtualizedList } from 'react-virtualized';
import { fetchMessageByUserSessionId } from '../../../api/ApiService';
import ChatItemFooter from '../../../components/chat-item-footer';
import Message from '../../../components/message';
import { HomeContext, useWebSocket } from '../../../context';
import { loadMessage, scrollToBottom } from '../../../redux/slices/chatSlice';
import './index.less';
import ChatItemRightClickMenu from "../../../components/chat-item-right-click-menu";
import ChatItemContentFooter from "../../../components/chat-item-content-footer";

const { Content } = Layout;

const ChatItem = React.memo(({ sessionId }) => {

    const { dimensions } = useContext(HomeContext)

    // 当窗口大小改变时 清除消息高度缓存
    useEffect(() => {
        cache.current?.clearAll()
    }, [dimensions])

    const { socket } = useWebSocket();
    const socketRef = useRef();
    useEffect(() => {
        socketRef.current = socket;
    }, [socket]);
    //用户信息
    const userInfo = useSelector(state => state.chat.userInfo) || {}
    //当前会话
    const session = useSelector(state => state.chat.entities.sessions[sessionId])
    //会话关联的消息
    const { prevMsgId, scrollToIndex, messages } = session

    const dispatch = useDispatch()

    const listRef = useRef()

    // 无限滚动开关
    const [infiniteRollSwitch, setInfIniteRollSwitch] = useState(false)

    const [contentFooter, setContentFooter] = useState({
        height: 0,
        attr: null
    })

    //初始加载数据
    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchMessageByUserSessionId(sessionId)
            const messageList = data.list
            const newMessageList = messageList.map(item => {
                item.self = userInfo.id === item.senderUserId
                return item
            })
            dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList, more: false }))
            setInfIniteRollSwitch(true)
        }
        if (session.messageInit === undefined || session.messageInit === false) {
            fetchData()
        } else {
            dispatch(scrollToBottom({ sessionId: sessionId }))
            setInfIniteRollSwitch(true)
        }
        // eslint-disable-next-line
    }, [])

    const cache = React.useRef(
        new CellMeasurerCache({
            fixedWidth: true
        })
    )

    //聊天内容显示
    const rowRenderer = useCallback(({ index, key, parent, style }) => {
        const item = messages[index]
        return (
            <CellMeasurer
                key={key}
                cache={cache.current}
                columnCount={1}
                columnIndex={0}
                rowIndex={index}
                parent={parent}
            >
                {({ registerChild }) => (
                    <div ref={registerChild} key={item} className='chat-item' style={style}>
                        {item ? <Message onContextMenu={(event) => handleContextMenu(event, item, index)} key={item} messageId={item} /> : <></>}
                    </div>
                )}

            </CellMeasurer>
        );
    }, [messages]);


    // 右键菜单
    const [rightMenu, setRightMenu] = useState({
        visible: false,
        x: 0,
        y: 0,
        messageId: null
    })
    const handleContextMenu = (event, messageId, index) => {
        // 阻止默认的右键菜单
        event.preventDefault()
        const { clientX, clientY } = event
        setRightMenu({
            visible: true,
            x: clientX,
            y: clientY,
            messageId: messageId,
            index: index
        })
    }

    const handleRightMenuClose = () => {
        setRightMenu({
            visible: false,
            x: 0,
            y: 0,
            messageId: null,
            index: null
        })
    }

    const handleBeforeRightMenu = () => {
        setInfIniteRollSwitch(false)
        setRightMenu({
            visible: false,
            x: 0,
            y: 0,
            messageId: null,
            index: null
        })
    }

    const handleAfterRightMenu = (cleared = false) => {
        if (cleared) {
            cache.current.clearAll()
        }
        requestAnimationFrame(() => {
            setTimeout(() => {
                setInfIniteRollSwitch(true)
            }, 500)
        })

    }

    // 加载更多数据
    const loadMoreData = () => {
        fetchMessageByUserSessionId(sessionId, prevMsgId)
            .then(
                data => {
                    const messageList = data.list
                    const newMessageList = messageList.map(item => {
                        item.self = userInfo.id === item.senderUserId
                        return item
                    })
                    dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList, more: true }))
                }
            )
    }

    const handleOnScroll = ({ scrollTop }) => {
        if (scrollTop === 0 && infiniteRollSwitch === true) {
            loadMoreData()
        }
    }

    const openContentFooter = useCallback((attr, footerHeight = 64) => {
        setContentFooter({
            height: footerHeight,
            attr: attr
        })
    }, [])

    const closeContentFooter = useCallback(() => {
        setContentFooter({
            height: 0,
            attr: null
        })
    }, [])

    return (
        <>
            <div style={{ width: '100%', height: '100%' }}>
                <Content style={{ height: '100%' }}>
                    <Layout style={{ height: '100%' }}>
                        {/* 聊天内容展示 */}
                        <Content onContextMenu={(event) => event.preventDefault()} className='content-chat' style={{ height: '62%' }}>
                            <Flex flex={1} style={{ height: '100%' }} vertical>
                                <AutoSizer>
                                    {({ height, width }) =>
                                    (
                                        <VirtualizedList
                                            ref={listRef}
                                            className='content-chat-list'
                                            width={width}
                                            height={height - contentFooter.height}
                                            rowCount={messages?.length || 0}
                                            rowHeight={cache.current.rowHeight}
                                            deferredMeasurementCache={cache.current}
                                            rowRenderer={rowRenderer}
                                            scrollToIndex={scrollToIndex}
                                            onScroll={handleOnScroll}
                                        />
                                    )}
                                </AutoSizer>
                                {contentFooter.height > 0 && (
                                    <div
                                        style={{
                                            height: contentFooter.height,
                                            boxShadow: '0 -5px 10px rgba(0, 0, 0, 0.06)',
                                            textAlign: 'center',
                                            marginTop: 'auto',
                                        }}
                                    >
                                        <ChatItemContentFooter
                                            messageId={contentFooter.attr}
                                            closeContentFooter={closeContentFooter}
                                        />
                                    </div>
                                )}
                            </Flex>
                        </Content>
                        <Content style={{ height: '38%' }}>
                            <ChatItemFooter
                                session={session}
                                messageId={contentFooter.attr}
                                closeContentFooter={closeContentFooter}
                            />
                        </Content>
                    </Layout>
                </Content>
                {rightMenu.visible && (
                    <ChatItemRightClickMenu
                        messageId={rightMenu.messageId}
                        index={rightMenu.index}
                        sessionId={sessionId}
                        x={rightMenu.x}
                        y={rightMenu.y}
                        openContentFooter={openContentFooter}
                        onBefore={handleBeforeRightMenu}
                        onAfter={handleAfterRightMenu}
                        close={handleRightMenuClose}
                    />
                )}
            </div>
        </>
    );
})

export default ChatItem;