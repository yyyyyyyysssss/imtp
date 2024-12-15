import React,{useRef} from 'react';
import { useNavigation, } from '@react-navigation/native';
import { StyleSheet } from 'react-native';
import VideoPlayer from 'react-native-video-controls';



const VideoPlay = ({ route }) => {

    const { url } = route.params;

    const navigation = useNavigation();

    const onEnd = () => {
        console.log('play end')
    }


    return (
        <VideoPlayer
            style={[styles.videoPlayer]}
            source={{ uri: url }}
            resizeMode="contain"
            disableVolume={true}
            navigator={navigation}
            controlTimeout={5000}
            onEnd={onEnd}
        />
    )
}


const styles = StyleSheet.create({
    videoPlayer: {
        
    },
})

export default VideoPlay