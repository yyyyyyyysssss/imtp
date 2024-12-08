import { HStack, VStack, Text, Center, Input, Pressable } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import AntDesignIcon from 'react-native-vector-icons/AntDesign';

const Search = ({ }) => {

    return (
        <Center
            style={{
                paddingLeft: 10,
                paddingRight: 10,
                paddingTop: 1,
            }}
        >
            <Input
                focusOutlineColor='none'
                borderWidth={0}
                backgroundColor='white'
                shadow={1}
                size='lg'
                InputLeftElement={
                    <Pressable style={{ marginLeft: 8 }}>
                        <AntDesignIcon name='search1' size={18} color="gray" />
                    </Pressable>
                }
                placeholder='搜索'
            />
        </Center>
    )
}

export default Search;