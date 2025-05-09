import React, { useEffect, useMemo, useState } from 'react';
import './index.less'
import { Modal, Image as AntdImage } from "antd"
import { MessageStatus } from '../../../enum'
import VideoPlay from '../../../components/VideoPlay';
import videoPlayIcon from '../../../assets/img/video-play-48.png'

const defaultMaxHeight = 200

const defaultPlayIconMaxHeight = 48

const VideoMessage = React.memo(({ content, status, contentMetadata, maxHeight = defaultMaxHeight }) => {
    const { width, height, mediaType, thumbnailUrl, durationDesc } = contentMetadata
    //视频弹出框
    const [videoOpen, setVideoOpen] = useState(false);
    //视频播放选项
    const [videoOption, setVideoOption] = useState(null);

    useEffect(() => {
        const videoJsOptions = {
            autoplay: true,
            controls: true,
            responsive: true,
            fluid: true,
            sources: [{
                src: content,
                type: mediaType
            }]
        };
        setVideoOption(videoJsOptions)
    }, [content, mediaType])

    const measure = useMemo(() => {
        let maxWidth
        if (width > height) {
            maxWidth = maxHeight * 1.618
        } else {
            maxWidth = maxHeight * 0.618
        }
        const scaleW = maxWidth / width
        const scaleH = maxHeight / height
        const scale = Math.min(scaleW, scaleH)
        const playIconMaxHeight = defaultPlayIconMaxHeight / (defaultMaxHeight / maxHeight)
        return {
            width: width * scale,
            height: height * scale,
            playIconMaxHeight: playIconMaxHeight
        }
    }, [maxHeight, width, height])

    //视频播放
    const videoPlay = () => {
        setVideoOpen(true);
    }
    //视频关闭
    const videoClose = () => {
        setVideoOpen(false);
    }

    return (
        <>
            <div
                className='video-div'
                style={{
                    backgroundColor: status && status === MessageStatus.PENDING ? 'black' : '',
                    width: measure.width,
                    height: measure.height,
                    borderRadius: maxHeight >= defaultMaxHeight ? '6px' : '1px'
                }}
                onClick={videoPlay}
            >
                {thumbnailUrl && (
                    <AntdImage
                        className='video-message'
                        style={{
                            height: measure.height
                        }}
                        height={measure.height}
                        preview={false}
                        src={thumbnailUrl}
                    />
                )}
                <div className='video-gradient' />
                {((status && status !== MessageStatus.PENDING) || !status) && (
                    <div className='video-icon'>
                        <img height={measure.playIconMaxHeight} src={videoPlayIcon} alt='icon' />
                    </div>
                )}
                {maxHeight >= defaultMaxHeight && (
                    <div className='video-duration'>
                        <label>{durationDesc}</label>
                    </div>
                )}
            </div>
            <Modal
                className='video-pay-modal'
                centered
                destroyOnClose={true}
                maskClosable={false}
                width={400}
                open={videoOpen}
                onCancel={videoClose}
                footer={null}
            >
                <div>
                    <VideoPlay options={videoOption} />
                </div>
            </Modal>
        </>
    )
})

export default VideoMessage