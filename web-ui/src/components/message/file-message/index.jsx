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

    const measure = useMemo(() => {
        if (height === defaultHeight) {
            return {
                width: defaultWidth,
                height: height,
                iconFontSize: defaultIconFontSize
            }
        } else {
            const scale = height / defaultHeight
            return {
                width: defaultWidth * scale,
                height: height,
                iconFontSize: defaultIconFontSize * scale
            }
        }
    }, [height])

    return (
        <div style={{ cursor: 'pointer' }}>
            <Flex
                className={`other-file-message ${direction === 'RIGHT' ? status && status === MessageStatus.PENDING ? 'other-file-message-right-pending' : 'other-file-message-right' : 'other-file-message-left'}`}
                style={{ width: measure.width, height: measure.height }}
            >
                <SubFileMessage content={content} filename={filename} fileSize={fileSize} iconFontSize={measure.iconFontSize} />
            </Flex>
        </div>
    )
})

export const SubFileMessage = ({ content,filename, fileSize, iconFontSize, color='black', maxLine = 2 }) => {

    const handlerFileMessageClick = (url, fileName) => {
        download(url, fileName)
    }

    return (
        <Flex style={{height: '100%',cursor: 'pointer'}} justify='center' align='center' gap="middle" onClick={() => handlerFileMessageClick(content, filename)}>
            <Flex
                style={{ overflow: 'hidden' }}
                justify='center'
                gap="small"
                vertical
            >
                <label className='other-file-filename-ellipsis' style={{ wordWrap: 'break-word',color: color, WebkitLineClamp: maxLine }}>{filename}</label>
                {fileSize && (
                    <label style={{ fontSize: '12px', color: 'gray' }}>{fileSize}</label>
                )}
            </Flex>
            <Icon component={FileOutlined} style={{ color: 'gray', fontSize: iconFontSize }} />
        </Flex>

    )
}

export default FileMessage