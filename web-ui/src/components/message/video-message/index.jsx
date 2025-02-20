import React, { useEffect, useState } from 'react';
import './index.less'
import { Modal, Image as AntdImage } from "antd"
import { MessageStatus } from '../../../enum'
import VideoPlay from '../../../components/VideoPlay';
import videoPlayIcon from '../../../assets/img/video-play-48.png'


const VideoMessage = React.memo(({ content, status, contentMetadata }) => {
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

    const mediaHeight = 120 / width * height;
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
                    width: '120px',
                    height: mediaHeight
                }}
                onClick={videoPlay}
            >
                {thumbnailUrl && (
                    <AntdImage
                        className='video-message'
                        style={{
                            width: '120px',
                            height: mediaHeight
                        }}
                        height={mediaHeight}
                        preview={false}
                        src={thumbnailUrl}
                    />
                )}
                <div className='video-gradient' />
                {((status && status !== MessageStatus.PENDING) || !status) && (
                    <div className='video-icon'>
                        <img src={videoPlayIcon} alt='icon' />
                    </div>
                )}
                <div className='video-duration'>
                    <label>{durationDesc}</label>
                </div>
            </div>
            <Modal
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