import React from 'react'
import './index.less'
import { Flex } from 'antd'
import { VoiceCallOutlined } from '../../customIcon'
import { CallStatus } from '../../../enum'

const VoiceCallMessage = React.memo(({ callStatus, self, duration, durationDesc }) => {
    let content
    switch (callStatus) {
        case CallStatus.COMPLETED:
            content = '通话时长'
            break
        case CallStatus.CANCELLED:
            content = self ? '已取消' : '对方已取消'
            break
        case CallStatus.REFUSED:
            content = self ? '对方已拒接' : '已拒接'
            break
        case CallStatus.INTERRUPTED:
            content = '通话中断'
            break
    }

    return (
        <div className={`voice-call-message ${self ? 'voice-call-message-right' : 'voice-call-message-left'}`}>
            <Flex gap={6} justify='center'>
                <VoiceCallOutlined />
                <div
                    style={{
                        whiteSpace: 'nowrap'
                    }}
                >
                    {content}
                </div>
                {callStatus === CallStatus.COMPLETED && (
                    <div>
                        {durationDesc}
                    </div>
                )}
            </Flex >
        </div >

    )
})

export default VoiceCallMessage