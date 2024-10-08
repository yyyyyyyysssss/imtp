import React, {useEffect, useRef } from 'react'
import videojs from 'video.js';
import 'video.js/dist/video-js.css';


const VideoPlay = (props) => {

    const videoRef = useRef(null);
    const playerRef = useRef(null);
    const { options, onReady } = props;

    useEffect(() => {
        if (!playerRef.current) {
            const player = playerRef.current = videojs(videoRef.current, options, () => {
                console.log('Player is ready');
                onReady && onReady(player);
            })
        } else {
            const player = playerRef.current;
            player.autoplay(options.autoplay);
            player.src(options.sources);
            
        }

    }, [options, videoRef]);

    //销毁播放器
    useEffect(() => {
        const player = playerRef.current;
        return () => {
            if (player && !player.isDisposed()) {
                player.dispose();
                playerRef.current = null;
            }
        }
    }, [playerRef])

    return (
        <>
            <div data-vjs-player>
                <video
                    className='video-js vjs-default-skin vjs-big-play-centered'
                    ref={videoRef}
                />
            </div>
        </>
    );
}


export default VideoPlay;