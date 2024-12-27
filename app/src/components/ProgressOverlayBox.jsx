import * as Progress from 'react-native-progress';
import {  Box } from 'native-base';

const ProgressOverlayBox = ({ enabled, progress, children }) => {

    return (
        <Box
            style={{
                position: 'relative',
                justifyContent: 'center',
                alignItems: 'center'
            }}
        >
            {children}
            {enabled && (
                <>
                    <Box
                        style={{
                            position: 'absolute',
                            height: '100%',
                            width: '100%',
                            borderRadius: 6,
                            backgroundColor: 'rgba(0, 0, 0, 0.3)'
                        }}
                    />
                    <Progress.Pie
                        style={{
                            position: 'absolute'
                        }}
                        progress={progress}
                        color='white'
                        borderWidth={0}
                        size={35}
                    />
                </>

            )}
        </Box>
    )
}

export default ProgressOverlayBox