import { Divider, Flex } from 'antd';
import React from 'react';
import { useSelector } from 'react-redux';
import { CloseCircleOutlined } from '@ant-design/icons';
import './index.less'
import MessageQuote from '../message-quote';

const ChatItemContentFooter = React.memo(({ messageId, closeContentFooter }) => {

    const message = useSelector(state => state.chat.entities.messages[messageId])

    return (
        <Flex
            flex={1}
            justify='space-between'
            style={{
                paddingTop: 5,
                paddingLeft: 10,
                paddingRight: 10,
                height: '100%',
            }}
        >
            <Flex gap={5} flex={9} justify='flex-start' align='center' style={{ padding: 10, overflow: 'hidden' }}>
                <Divider style={{ height: '100%', borderWidth: '3px', borderColor: 'lightgray' }} type='vertical' />
                <MessageQuote message= {message}/>
            </Flex>
            <Flex
                flex={1}
                justify='flex-end'
                align='flex-start'
            >
                <div className='close-div' onClick={closeContentFooter}>
                    <CloseCircleOutlined
                        style={{
                            fontSize: 16,
                        }}
                    />
                </div>
            </Flex>
        </Flex>
    )
})

export default ChatItemContentFooter