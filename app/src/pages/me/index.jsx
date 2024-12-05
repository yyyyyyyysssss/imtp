import { Text, Button } from 'native-base';
import React, { useContext, useState } from 'react';
import { AuthContext } from '../../context';
import Storage from '../../storage/storage';
import api from '../../api/api';
import { showToast } from '../../components/Utils';

const Me = () => {

    const { signOut } = useContext(AuthContext)

    const logout = () => {
        api.post('/logout', null)
            .then(
                (res) => {
                    signOut()
                },
                (error) => {
                    showToast('退出失败')
                }
            )
    }

    return (
        <>
            <Button onPress={logout}>退出登录</Button>
        </>
    )
}

export default Me;