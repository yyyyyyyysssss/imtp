import { HStack, Pressable, Text, VStack } from 'native-base';
import React, { useState } from 'react';
import { showToast } from './Utils';

const SwipeItemOperation = ({ data, rowMap }) => {

    const [delFlag, setDelFlag] = useState(false)

    const handleDel = (data, rowMap) => {
        console.log('rowMap',rowMap)
        if (delFlag) {
            showToast('删除')
        } else {
            setDelFlag(true)
        }
    }

    return (
        <HStack flex="1" pl="2" justifyContent='flex-end'>
            <Pressable
                style={{ width: delFlag ? 120 : 90 }}
                cursor="pointer"
                bg="red.500"
                justifyContent="center"
                onPress={() => handleDel(data, rowMap)}
                _pressed={{
                    opacity: 0.5
                }}>
                <VStack alignItems="center">
                    <Text color="white" fontSize="17" fontWeight="medium">
                        {delFlag ? '删除该聊天' : '删除'}
                    </Text>
                </VStack>
            </Pressable>
        </HStack>
    )
}

export default SwipeItemOperation
