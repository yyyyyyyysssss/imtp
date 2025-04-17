import React, { useEffect, useRef } from 'react';
import './index.less'
import { Divider, Flex, Modal } from 'antd';
import { useSelector, useDispatch } from 'react-redux';
import { deleteUserMessageById } from '../../api/ApiService';
import { deleteMessage } from '../../redux/slices/chatSlice';


const ChatItemRightClickMenu = ({ messageId, index, sessionId, x, y, openContentFooter, onBefore, onAfter, close }) => {

    const menuRef = useRef()

    const dispatch = useDispatch()

    const message = useSelector(state => state.chat.entities.messages[messageId])

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                close()
            }
        }
        // 绑定事件监听器
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            // 组件卸载时移除事件监听器
            document.removeEventListener('mousedown', handleClickOutside);
        }
    }, [])

    const copy = () => {
        onBefore()
        navigator.clipboard.writeText(message.content)
        onAfter()
    }

    const quote = () => {
        onBefore()
        openContentFooter(messageId, 70)
        onAfter()
    }

    const del = () => {
        onBefore()
        Modal.confirm({
            title: '删除消息',
            content: '删除该条消息？',
            closable: true,
            centered: true,
            okText: '确认',
            cancelText: '取消',
            onOk: () => {
                
                dispatch(deleteMessage({ id: message.id, sessionId: sessionId }))
                // deleteUserMessageById(message.id)
                onAfter(true)
            },
            onCancel: () => {
                onAfter()
            }
        })
    }

    return (
        <Flex
            ref={menuRef}
            align='center'
            style={{
                position: 'fixed',
                top: y,
                left: x,
                zIndex: 1000,
                backgroundColor: 'white',
                border: '1px solid #ccc',
                boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.1)',
            }}
            vertical
        >
            <Flex
                gap={6}
                flex={1}
                vertical
            >
                <div className='menu-btn' onClick={copy}>复制</div>
                <div className='menu-btn' onClick={quote}>引用</div>
            </Flex>
            <Divider />
            <Flex
                gap={6}
                flex={1}
                vertical
            >
                <div className='menu-btn' onClick={del}>删除</div>
            </Flex>
        </Flex>
    )
}

export default ChatItemRightClickMenu