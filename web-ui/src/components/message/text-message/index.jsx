import React from 'react';
import './index.less'

const TextMessage = React.memo(({ content, direction }) => {

    return (
        <div className={`text-message ${direction === 'RIGHT' ? 'text-message-right' : 'text-message-left'}`}>
                {content}
        </div>
    )
})

export default TextMessage