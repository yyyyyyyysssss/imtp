import { Box, Text } from 'native-base';
import React, { useEffect, } from 'react';
import { useSelector } from 'react-redux';

const Notification = React.memo(({ children, size = 20 }) => {

    const unreadCount = useSelector(state => state.chat.unreadCount)

    return (
        <Box position='relative'>
            {children}
            {unreadCount > 0 && (
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
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </Text>
                </Box>
            )}

        </Box>
    )
}, (prevProps, nextProps) => prevProps.count === nextProps.count)

export default Notification;