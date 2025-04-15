import React from 'react';
import './index.less'
import { Image as AntdImage } from "antd"
import { MessageStatus } from '../../../enum';

const ImageMessage = React.memo(({ content, contentMetadata, status, maxHeight = 200 }) => {
    const mediaHeight = maxHeight / contentMetadata.width * contentMetadata.height;
    let preview;
    let blur;
    if (status === MessageStatus.PENDING) {
        preview = false;
        blur = 'blur(5px)';
    } else {
        preview = true;
        blur = 'blur(0px)';
    }
    return (
        <div>
            <AntdImage
                className='image-message'
                height={mediaHeight}
                preview={preview}
                src={content}
                style={{ filter: blur }}
            />
        </div>
    )
})

export default ImageMessage