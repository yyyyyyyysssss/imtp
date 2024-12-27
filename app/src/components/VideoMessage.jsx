import React from 'react';
import { Box, Image, Pressable, Text, Spinner } from 'native-base';
import FontAwesome from 'react-native-vector-icons/FontAwesome';
import { StyleSheet } from 'react-native';
import Svg, { Defs, LinearGradient, Stop, Rect } from 'react-native-svg';
import { useNavigation, } from '@react-navigation/native';
import { MessageStatus } from '../enum';


const VideoMessage = React.memo(({ content, status, contentMetadata }) => {

    const navigation = useNavigation();

    const { width, height, thumbnailUrl, durationDesc } = contentMetadata

    const mediaHeight = 120 / width * height

    const playVideo = () => {
        if (status && status === MessageStatus.PENDING) {
            return
        }
        navigation.navigate('VideoPlay', {
            url: content,
        })
    }

    return (
        <>
            <Pressable
                onPress={playVideo}
            >
                <Box
                    style={styles.videoBox}
                    backgroundColor={status && status === MessageStatus.PENDING ? 'black' : ''}
                    width={120}
                    height={mediaHeight}
                >
                    {thumbnailUrl && (
                        <Image
                            width={120}
                            height={mediaHeight}
                            shadow={3}
                            resizeMode='cover'
                            source={{
                                uri: thumbnailUrl
                            }}
                            blurRadius={status && status === MessageStatus.PENDING ? 10 : 0}
                            alt=''
                        />
                    )}

                    {/* 底部渐变 */}
                    <Box
                        style={styles.videoGradientBox}
                        rounded={8}
                    >
                        <Svg height="100%" width="100%" viewBox="0 0 100 100" preserveAspectRatio="none">
                            <Defs>
                                <LinearGradient id="grad1" x1="0%" y1="100%" x2="0%" y2="0%">
                                    <Stop offset="0%" stopColor="black" stopOpacity="1" />
                                    <Stop offset="100%" stopColor="rgba(0, 0, 0, 0)" stopOpacity="0" />
                                </LinearGradient>
                            </Defs>
                            <Rect width="100" height="100" fill="url(#grad1)" />
                        </Svg>
                    </Box>
                    {/* 播放图标 */}
                    {((status && status !== MessageStatus.PENDING) || !status) && (
                        <Box style={styles.videoPlayIconBox}>
                            <FontAwesome name="play-circle" size={50} color="gray" />
                        </Box>
                    )}
                    {/* 时长 */}
                    <Box style={styles.videoDurationDescBox}>
                        <Text style={styles.videoDurationDescText}>{durationDesc}</Text>
                    </Box>
                </Box>
            </Pressable>
        </>

    )
})


const styles = StyleSheet.create({
    videoBox: {
        position: 'relative',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        borderRadius: 6,
        justifyContent: 'center',
        alignItems: 'center',
        overflow: 'hidden'
    },
    videoGradientBox: {
        position: 'absolute',
        bottom: '0%',
        width: '100%',
        height: 50,
        overflow: 'hidden'
    },
    videoPlayIconBox: {
        position: 'absolute',
    },
    videoDurationDescBox: {
        position: 'absolute',
        left: 10,
        bottom: 5,
    },
    videoDurationDescText: {
        color: 'white'
    }
})

export default VideoMessage