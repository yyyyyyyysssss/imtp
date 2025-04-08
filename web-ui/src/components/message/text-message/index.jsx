import React from 'react';
import './index.less'

const TextMessage = React.memo(({ content, direction,onContextMenu }) => {

    return (
        <div onContextMenu={onContextMenu} className={`text-message ${direction === 'RIGHT' ? 'text-message-right' : 'text-message-left'}`}>
                {content}
        </div>
    )
})

export default TextMessage