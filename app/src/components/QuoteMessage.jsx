import React from 'react';
import { Box, Divider, HStack, Text, VStack } from "native-base"
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Pressable } from 'react-native';


const QuoteMessage = React.memo(({ messageId, close }) => {

    const closeQuote = () => {
        close()
    }

    return (
        <HStack maxHeight={30} space={10} style={{backgroundColor: '#EAEAEA', padding: 5}}>
            <Box>
                <Text>卡卡罗特：在吗</Text>
            </Box>
            <Pressable onPress={closeQuote}>
                <Ionicons name='close-circle-outline' size={20} />
            </Pressable>
        </HStack>
    )
})

export default QuoteMessage