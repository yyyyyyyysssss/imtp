import { Image, Pressable } from 'native-base';
import React, { useContext } from 'react';
import { ImageContext } from '../context';


const ImageMessage = ({ content, contentMetadata }) => {

    const { showOriginalImage } = useContext(ImageContext)

    return (
        <>
            <Pressable
                onPress={() => showOriginalImage(content)}
            >
                <Image
                    size={200}
                    shadow={5}
                    resizeMode='cover'
                    source={{
                        uri: content
                    }}
                    alt=''
                />
            </Pressable>
        </>
    )
}

export default ImageMessage