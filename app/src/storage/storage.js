import AsyncStorage from '@react-native-async-storage/async-storage';

class Storage {

    static async save(key,value){
        try{
            await AsyncStorage.setItem(key,JSON.stringify(value))
        }catch(error){
            console.error('error saving data',error);
        }
    }

    static async update(key,value){
        try{
            await AsyncStorage.mergeItem(key,JSON.stringify(value))
        }catch(error){
            console.error('error update data',error);
        }
    }

    static async get(key){
        try{
            const obj = await AsyncStorage.getItem(key)
            return obj !== null ? JSON.parse(obj) : null
        }catch(error){
            console.error('error get data',error);
        }
    }

    static async multiGet(keys){
        try{
            const result = await AsyncStorage.multiGet(keys);
            return result.map((key,value) => [key,value ? JSON.parse(value) : null])
        }catch(error){
            console.error('error multiGet data',error);
        }
    }

    static async remove(key){
        try{
            await AsyncStorage.removeItem(key);
        }catch(error){
            console.error('error remove data',error);
        }
    }

    static async multiRemove(keys){
        try{
            await AsyncStorage.multiRemove(keys);
        }catch(error){
            console.error('error multiRemove data',error);
        }
    }

    static async clear(){
        try{
            await AsyncStorage.clear();
        }catch(error){
            console.error('error clear data',error);
        }
    }

}

export default Storage;