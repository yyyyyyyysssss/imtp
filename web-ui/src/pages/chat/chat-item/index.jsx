import React, { useRef, useEffect, useCallback } from 'react'
import './index.less'
import { Layout } from "antd"
import { List as VirtualizedList, AutoSizer, CellMeasurer, CellMeasurerCache } from 'react-virtualized'
import { useWebSocket } from '../../../context';
import Message from '../../../components/message';
import { useDispatch, useSelector } from 'react-redux';
import { fetchMessageByUserSessionId } from '../../../api/ApiService';
import { loadMessage } from '../../../redux/slices/chatSlice';
import ChatItemFooter from '../../../components/chat-item-footer'

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
    const { messages } = session

    const dispatch = useDispatch()
    //初始加载数据
    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchMessageByUserSessionId(sessionId)
            const messageList = data.list
            const newMessageList = messageList.map(item => {
                item.self = userInfo.id === item.senderUserId
                return item
            })
            dispatch(loadMessage({ sessionId: sessionId, messages: newMessageList }))
        }
        if (session.messageInit === undefined || session.messageInit === false) {
            fetchData()
        }
    }, [])

    //聊天列表ref
    const chatContentRef = useRef(null);

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
            <Message key={item} messageId={item} />
        )
    };
    //列表项渲染回调函数
    const handleRowsRendered = ({ startIndex, stopIndex }) => {

    }
    return (
        <>
            <div style={{ width: '100%', height: '100%' }}>
                <Content style={{ height: '100%' }}>
                    <Layout style={{ height: '100%' }}>
                        {/* 聊天内容展示 */}
                        <Content className='content-chat' style={{ height: '62%' }}>
                            <AutoSizer>
                                {({ height, width }) => {
                                    return (
                                        <div>
                                            <VirtualizedList
                                                ref={chatContentRef}
                                                className='content-chat-list'
                                                width={width}
                                                height={height}
                                                rowCount={messages?.length || 0}
                                                rowHeight={cache.current.rowHeight}
                                                deferredMeasurementCache={cache.current}
                                                rowRenderer={rowRenderer}
                                                onRowsRendered={handleRowsRendered}
                                                scrollToIndex={messages?.length - 1}
                                            />
                                        </div>
                                    );
                                }}
                            </AutoSizer>
                        </Content>
                        <Content style={{ height: '38%' }}>
                            <ChatItemFooter session={session} />
                        </Content>
                    </Layout>
                </Content>
            </div>
        </>
    );
})

export default ChatItem;