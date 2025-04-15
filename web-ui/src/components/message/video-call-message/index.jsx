import React from 'react'
import './index.less'
import { Flex } from 'antd'
import { CallStatus } from '../../../enum'
import { VideoMessageIcon } from '../../customIcon'


const VideoCallMessage = React.memo(({ callStatus, self, duration, durationDesc }) => {

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
            <div className={`video-call-message ${self ? 'video-call-message-right' : 'video-call-message-left'}`}>
                <Flex gap={6} justify='center'>
                    <VideoMessageIcon size={25} />
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

export default VideoCallMessage