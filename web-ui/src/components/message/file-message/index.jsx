import React, { useMemo } from 'react';
import './index.less'
import { Flex } from "antd"
import Icon, { FileOutlined } from '@ant-design/icons';
import { download } from '../../../utils'
import { MessageStatus } from '../../../enum';

const defaultWidth = 200
const defaultHeight = 80
const defaultIconFontSize = 40

const FileMessage = React.memo(({ content, status, filename, fileSize, direction, height = defaultHeight }) => {


    const handlerFileMessageClick = (url, fileName) => {
        download(url, fileName)
    }

    const measure = useMemo(() => {
        if(height === defaultHeight){
            return {
                width: defaultWidth,
                height: height,
                iconFontSize: defaultIconFontSize
            }
        }else {
            const scale = height / defaultHeight
            return {
                width: defaultWidth * scale,
                height: height,
                iconFontSize: defaultIconFontSize * scale
            }
        }
    },[height])

    return (
        <div style={{ cursor: 'pointer' }} onClick={() => handlerFileMessageClick(content, filename)}>
            <Flex
                align='center'
                className={`other-file-message ${direction === 'RIGHT' ? status && status === MessageStatus.PENDING ? 'other-file-message-right-pending' : 'other-file-message-right' : 'other-file-message-left'}`}
                gap="middle"
                style={{ width: measure.width, height: measure.height }}
            >
                <Flex
                    style={{ overflow: 'hidden', height: '100%' }}
                    gap="small"
                    vertical
                >
                    <label className='other-file-filename-ellipsis' style={{ wordWrap: 'break-word' }}>{filename}</label>
                    {fileSize && (
                        <label style={{ fontSize: '12px', color: 'gray' }}>{fileSize}</label>
                    )}
                </Flex>
                <Icon component={FileOutlined} style={{ color: 'gray', fontSize: measure.iconFontSize }} />
            </Flex>
        </div>
    )
})

export default FileMessage