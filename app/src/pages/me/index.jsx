import { Text, Button } from 'native-base';
import React, { useContext, useState } from 'react';
import { AuthContext } from '../../context';
import { showToast } from '../../components/Utils';
import { logout } from '../../api/ApiService';

const Me = () => {

    const { signOut } = useContext(AuthContext)

    const quit = () => {
        logout()
            .then(
                (data) => {
                    signOut()
                },
                (error) => {
                    showToast('退出失败')
                }
            )
    }

    return (
        <>
            <Button onPress={quit}>退出登录</Button>
        </>
    )
}

export default Me;