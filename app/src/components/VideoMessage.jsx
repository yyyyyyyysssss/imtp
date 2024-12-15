import React from 'react';
import { Box, Image, Pressable, Text } from 'native-base';
import FontAwesome from 'react-native-vector-icons/FontAwesome';
import { StyleSheet } from 'react-native';
import Svg, { Defs, LinearGradient, Stop, Rect } from 'react-native-svg';
import { useNavigation, } from '@react-navigation/native';


const VideoMessage = ({ content, contentMetadata }) => {

    const navigation = useNavigation();

    const { width, height, thumbnailUrl, durationDesc } = contentMetadata

    const mediaHeight = 120 / width * height

    const playVideo = () => {
        navigation.navigate('VideoPlay',{
            url: content,
        })
    }

    return (
        <>
            <Pressable
                onPress={playVideo}
            >
                <Box style={styles.videoBox}>
                    <Image
                        rounded={8}
                        width={120}
                        height={mediaHeight}
                        shadow={3}
                        resizeMode='cover'
                        source={{
                            uri: thumbnailUrl
                        }}
                        alt=''
                    />
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
                    <Box style={styles.videoPlayIconBox}>
                        <FontAwesome name="play-circle" size={50} color="gray" />
                    </Box>
                    <Box style={styles.videoDurationDescBox}>
                        <Text style={styles.videoDurationDescText}>{durationDesc}</Text>
                    </Box>
                </Box>
            </Pressable>
        </>

    )
}


const styles = StyleSheet.create({
    videoBox: {
        position: 'relative'
    },
    videoGradientBox: {
        position: 'absolute',
        top: '90%',
        left: '50%',
        width: '100%',
        height: 50,
        transform: 'translate(-50%, -50%)',
        overflow: 'hidden'
    },
    videoPlayIconBox: {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)'
    },
    videoDurationDescBox: {
        position: 'absolute',
        left: '85%',
        bottom: -5,
        transform: 'translate(-50%, -50%)',
    },
    videoDurationDescText: {
        color: 'white'
    }
})

export default VideoMessage