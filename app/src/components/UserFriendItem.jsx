import { VStack, Text, HStack, Avatar, Divider } from 'native-base';
import { StyleSheet } from 'react-native';

const UserFriendItem = ({ avatar, name, isPressed = false }) => {

    return (
        <HStack flex={1} space={5} alignItems='center' style={{ backgroundColor: isPressed ? '#C8C6C5' : '#F5F5F5', padding: 10, paddingRight: 0 }}>
            <VStack flex={1} justifyContent='center'>
                <Avatar
                    size="60"
                    _image={{
                        borderRadius: 8
                    }}
                    source={{ uri: avatar }}
                />
            </VStack>
            <VStack flex={6} justifyContent='center' style={styles.customItemVStack}>
                <Text style={styles.customItemVStackText}>
                    {name}
                </Text>
            </VStack>
        </HStack>
    )
}

const styles = StyleSheet.create({
    customItemVStack: {
        width: '100%',
        height: '100%'
    },
    customItemVStackText: {
        fontSize: 16
    },
})

export default UserFriendItem


export const UserFriendItemSeparator = () => {

    return (
        <HStack flex={1} space={5} alignItems='flex-end' justifyContent='center'>
            <VStack flex={1}>

            </VStack>
            <VStack flex={6}>
                <Divider style={{
                    height: 1,
                    backgroundColor: '#D3D3D3',
                    shadowColor: '#000',
                    shadowOffset: { width: 0, height: 1 },
                    shadowOpacity: 0.1,
                    shadowRadius: 2,
                }} />
            </VStack>
        </HStack>
    )
}

export const UserFriendItemFooter = () => {

    return (
        <HStack style={{
            paddingLeft: 10
        }}>
            <Divider style={{
                height: 1,
                backgroundColor: '#D3D3D3'
            }} />
        </HStack>
    )
}