import { Box, Text } from 'native-base';
import React, { useEffect, } from 'react';

const Notification = React.memo(({ children, size = 20, count }) => {
    
    return (
        <Box position='relative'>
            {children}
            {count > 0 && (
                <Box
                    position='absolute'
                    style={{
                        width: size,
                        height: size,
                        borderRadius: size / 2,
                        backgroundColor: 'red',
                        top: -4,
                        right: -16
                    }}
                >
                    <Text
                        style={{
                            fontSize: 10,
                            color: 'white',
                            fontWeight: 'bold',
                            textAlign: 'center',
                            lineHeight: size,
                        }}
                    >
                        {count > 99 ? '99+' : count}
                    </Text>
                </Box>
            )}

        </Box>
    )
})

export default Notification;