import React from 'react'
import './index.less'
import { Flex } from 'antd'
import { CallStatus } from '../../../enum'
import { VideoMessageIcon } from '../../customIcon'


const VideoCallMessage = React.memo(({ callStatus, self, duration, durationDesc }) => {

    return (
        <div className={`video-call-message ${self ? 'video-call-message-right' : 'video-call-message-left'}`}>
            <SubVideoCallMessage callStatus={callStatus} self={self} durationDesc={durationDesc}/>
        </div >

    )

})


export const SubVideoCallMessage = ({ callStatus, self, durationDesc, color = 'black' }) => {

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
        <Flex gap={6} justify='center'>
            <VideoMessageIcon size={25} color={color} />
            <div
                style={{
                    whiteSpace: 'nowrap',
                    color: color
                }}
            >
                {content}
            </div>
            {callStatus === CallStatus.COMPLETED && (
                <div
                    style={{
                        whiteSpace: 'nowrap',
                        color: color
                    }}
                >
                    {durationDesc}
                </div>
            )}
        </Flex >
    )
}

export default VideoCallMessage