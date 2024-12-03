import { Box, useToast , Flex, Menu, Pressable, Text } from 'native-base';
import React, { useState } from 'react';
import { StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { showToast } from './Utils';

const ChatTools = () => {
    const toast = useToast()

    const addFriend = () => {
        showToast(global.apiUrl)
    }

    const sweep = () => {
        showToast('扫一扫')
    }

    return (
        <Box alignItems="center">
            <Menu style={styles.toolsMenu} w="150" trigger={triggerProps => {
                return <Pressable {...triggerProps}>
                    <Ionicons style={{ marginRight: 12 }} name="add-circle-outline" size={28} />
                </Pressable>
            }}>
                <Menu.Item onPress={addFriend} style={{ alignItems: 'center' }}>
                    <Flex gap={3} direction="row">
                        <Box style={{flex: 1}}>
                            <Ionicons name="person-add" color="white" size={22} />
                        </Box>
                        <Box style={{flex: 3}}>
                            <Text style={styles.toolsMenuText}>添加好友</Text>
                        </Box>
                    </Flex>
                </Menu.Item>
                <Menu.Item onPress={sweep} style={{ alignItems: 'center' }}>
                    <Flex gap={3} direction="row">
                        <Box style={{flex: 1}}>
                            <Ionicons name="scan" color="white" size={22} />
                        </Box>
                        <Box style={{flex: 3}}>
                            <Text style={styles.toolsMenuText}>扫一扫</Text>
                        </Box>
                    </Flex>
                </Menu.Item>
            </Menu>
        </Box>
    )
}


const styles = StyleSheet.create({
    toolsMenu: {
        backgroundColor: '#2E2E2E',
        marginRight: 8, 
        marginTop: 5,
        borderRadius: 6,
    },
    toolsMenuText: {
        color: 'white',
        fontSize: 16
    }
})

export default ChatTools;