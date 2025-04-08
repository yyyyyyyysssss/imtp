import { Layout } from "antd";
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AutoSizer, CellMeasurer, CellMeasurerCache, List as VirtualizedList } from 'react-virtualized';
import { fetchMessageByUserSessionId } from '../../../api/ApiService';
import ChatItemFooter from '../../../components/chat-item-footer';
import Message from '../../../components/message';
import { useWebSocket } from '../../../context';
import { loadMessage } from '../../../redux/slices/chatSlice';
import './index.less';
import ChatItemRightClickMenu from "../../../components/chat-item-right-click-menu";

const { Content } = Layout;

const ChatItem = React.memo(({ sessionId }) => {

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
        }
        if (session.messageInit === undefined || session.messageInit === false) {
            fetchData()
        }
    }, [])

    const cache = React.useRef(
        new CellMeasurerCache({
            fixedWidth: true
        })
    );
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
                        {renderItem(index, item)}
                    </div>
                )}

            </CellMeasurer>
        );
    }, [messages]);
    //聊天项渲染函数
    const renderItem = (index, item) => {
        if (!item) {
            return (<></>);
        }
        return (
            <Message onContextMenu={(event) => handleContextMenu(event, item)} key={item} messageId={item} />
        )
    }

    // 右键菜单
    const [rightMenu, setRightMenu] = useState({
        visible: false,
        x: 0,
        y: 0,
        messageId: null
    })
    const handleContextMenu = (event, messageId) => {
        // 阻止默认的右键菜单
        event.preventDefault()
        const { clientX, clientY } = event
        setRightMenu({
            visible: true,
            x: clientX,
            y: clientY,
            messageId: messageId
        })
    }

    const rightMenuClose = (cleared = false) => {
        setRightMenu({
            visible: false,
            x: 0,
            y: 0,
            messageId: null
        })
        if (cleared) {
            //清除高度缓存避免列表项位置错乱
            cache.current.clearAll()
            // listRef.current.forceUpdateGrid()
        }
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
        if (scrollTop === 0 && session.messageInit && session.messageInit === true) {
            loadMoreData()
        }
    }

    return (
        <>
            <div style={{ width: '100%', height: '100%' }}>
                <Content style={{ height: '100%' }}>
                    <Layout style={{ height: '100%' }}>
                        {/* 聊天内容展示 */}
                        <Content onContextMenu={(event) => event.preventDefault()} className='content-chat' style={{ height: '62%' }}>
                            <AutoSizer>
                                {({ height, width }) =>
                                (
                                    <VirtualizedList
                                        ref={listRef}
                                        className='content-chat-list'
                                        width={width}
                                        height={height}
                                        rowCount={messages?.length || 0}
                                        rowHeight={cache.current.rowHeight}
                                        deferredMeasurementCache={cache.current}
                                        rowRenderer={rowRenderer}
                                        scrollToIndex={scrollToIndex}
                                        onScroll={handleOnScroll}
                                    />

                                )
                                }
                            </AutoSizer>
                        </Content>
                        <Content style={{ height: '38%' }}>
                            <ChatItemFooter session={session} />
                        </Content>
                    </Layout>
                </Content>
                {rightMenu.visible && (
                    <ChatItemRightClickMenu messageId={rightMenu.messageId} sessionId={sessionId} x={rightMenu.x} y={rightMenu.y} close={rightMenuClose} />
                )}
            </div>
        </>
    );
})

export default ChatItem;