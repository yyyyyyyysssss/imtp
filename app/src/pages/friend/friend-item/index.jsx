import { Avatar, Center, Divider, HStack, Image, Pressable, Text, VStack } from 'native-base';
import React from 'react';
import { StyleSheet } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import genderFemaleImg from './../../../assets/img/gender_female.png'
import genderMaleImg from './../../../assets/img/gender_male.png'
import { useSelector, useDispatch } from 'react-redux';
import { addSession } from '../../../redux/slices/chatSlice';
import { useNavigation, } from '@react-navigation/native';
import { DeliveryMethod } from '../../../enum';
import { createUserSession } from '../../../api/ApiService';
import NotFound from '../../NotFound';

const FriendItem = ({ route }) => {

    const { friendId } = route.params;

    if(!friendId){
        return <NotFound/>
    }

    const friendItem = useSelector(state => state.chat.userFriends.entities.friends[friendId])

    const navigation = useNavigation();

    const sessions = useSelector(state => state.chat.entities.sessions)

    const dispatch = useDispatch()

    const userInfo = useSelector(state => state.auth.userInfo)


    const toSend = async () => {
        const friendId = friendItem.id
        let sessionId;
        const session = Object.values(sessions).find(s => s.receiverUserId === friendId);
        if (session) {
            sessionId = session.id
        } else {
            sessionId = await createUserSession(friendId,DeliveryMethod.SINGLE)
            const userSessionItem = {
                id: sessionId,
                userId: userInfo.id,
                name: friendItem.note,
                receiverUserId: friendId,
                avatar: friendItem.avatar,
                deliveryMethod: DeliveryMethod.SINGLE
            }
            dispatch(addSession({ session: userSessionItem }))
        }
        navigation.navigate('ChatItem', {
            sessionId: sessionId,
        })
    }

    return (
        <>
            <Center style={styles.rootContainer}>
                <VStack space={2} width='100%'>
                    <VStack style={styles.rootContainerTop}>
                        <HStack width='100%' style={styles.friendBase} space={5} alignItems='center'>
                            <Avatar
                                size={75}
                                _image={{
                                    borderRadius: 8
                                }}
                                source={{ uri: friendItem.avatar }}
                            />
                            <VStack height='100%' justifyContent='flex-start'>
                                <HStack space={2}>
                                    <Text style={{
                                        fontSize: 18,
                                        fontWeight: 'bold'
                                    }}>
                                        {friendItem.note}
                                    </Text>
                                    <Image alt='' size={5} source={friendItem.gender === 'MALE' ? genderMaleImg : genderFemaleImg} />
                                </HStack>
                                {friendItem.note !== friendItem.nickname && (
                                    <HStack space={2}>
                                        <Text style={styles.friendItemText}>昵称:</Text>
                                        <Text style={styles.friendItemText}>{friendItem.nickname}</Text>
                                    </HStack>
                                )}
                                <HStack space={2}>
                                    <Text style={styles.friendItemText}>账号:</Text>
                                    <Text style={styles.friendItemText}>{friendItem.account}</Text>
                                </HStack>
                                {friendItem.region && (
                                    <HStack space={2}>
                                        <Text style={styles.friendItemText}>地区:</Text>
                                        <Text style={styles.friendItemText}>{friendItem.region}</Text>
                                    </HStack>
                                )}
                            </VStack>
                        </HStack>
                        <Divider style={styles.divider} />
                        <HStack width='100%' style={{ padding: 15, paddingLeft: 0, paddingRight: 5 }} space={1} alignItems='center' justifyContent='space-between'>
                            <Text style={{ fontSize: 18 }}>备注</Text>
                            <MaterialIcons name="keyboard-arrow-right" color="gray" size={30} />
                        </HStack>
                    </VStack>

                    <VStack style={styles.rootContainerBottom}>
                        <Pressable onPress={toSend}>
                            <HStack width='100%' style={{ padding: 15 }} space={1} justifyContent='center' alignItems='center'>
                                <Ionicons name='chatbubble-outline' size={28} color="#9AA5BE" />
                                <Text style={{ color: '#9AA5BE', fontSize: 15 }}>发消息</Text>
                            </HStack>
                        </Pressable>
                        <Divider style={styles.divider} />
                        <Pressable>
                            <HStack width='100%' style={{ padding: 15 }} space={1} justifyContent='center' alignItems='center'>
                                <Ionicons name='videocam-outline' size={28} color="#9AA5BE" />
                                <Text style={{ color: '#9AA5BE', fontSize: 15 }}>音视频通话</Text>
                            </HStack>
                        </Pressable>
                    </VStack>

                </VStack>
            </Center>
        </>
    )
}

const styles = StyleSheet.create({
    rootContainer: {

    },
    rootContainerTop: {
        backgroundColor: 'white',
        paddingLeft: 15
    },
    rootContainerBottom: {
        backgroundColor: 'white',
    },
    friendBase: {
        paddingTop: 30,
        paddingBottom: 30
    },
    friendItemText: {
        color: 'gray',
        fontSize: 14
    },
    divider: {
        height: 1,
        backgroundColor: '#D3D3D3',
    }
})

export default FriendItem;