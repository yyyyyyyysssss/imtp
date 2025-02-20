import React from 'react';
import './index.less'
import { Flex } from "antd"
import Icon, { FileOutlined } from '@ant-design/icons';
import { download } from '../../../utils'
import { MessageStatus } from '../../../enum';

const FileMessage = React.memo(({ content, status, filename, fileSize, direction }) => {


    const handlerFileMessageClick = (url, fileName) => {
        download(url, fileName)
    }

    return (
        <div style={{ cursor: 'pointer' }} onClick={() => handlerFileMessageClick(content, filename)}>
            <Flex align='center' className={`other-file-message ${direction === 'RIGHT' ? status && status === MessageStatus.PENDING ? 'other-file-message-right-pending' : 'other-file-message-right' : 'other-file-message-left'}`} gap="middle" 
            style={{ width: '200px', height: '80px'}}
            >
                <Flex style={{ width: '150px', overflow: 'hidden' }} gap="small" vertical>
                    <label className='other-file-filename-ellipsis' style={{ wordWrap: 'break-word' }}>{filename}</label>
                    <label style={{ fontSize: '12px', color: 'gray' }}>{fileSize}</label>
                </Flex>
                <Icon component={FileOutlined} style={{ color: 'gray', fontSize: '40px' }} />
            </Flex>
        </div>
    )
})

export default FileMessage