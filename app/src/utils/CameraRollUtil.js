import { PermissionsAndroid, Platform } from "react-native";
import { CameraRoll } from "@react-native-camera-roll/camera-roll";
import RNFS from 'react-native-fs';
import { showToast } from "../components/Utils";

const hasAndroidPermission = async () => {
    const getCheckPermissionPromise = async () => {
        if (Platform.Version >= 33) {
            return Promise.all([
                PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES),
                PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_MEDIA_VIDEO),
            ]).then(
                ([hasReadMediaImagesPermission, hasReadMediaVideoPermission]) =>
                    hasReadMediaImagesPermission && hasReadMediaVideoPermission,
            );
        } else {
            return PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE);
        }
    };

    const hasPermission = await getCheckPermissionPromise();
    if (hasPermission) {
        return true;
    }
    const getRequestPermissionPromise = async () => {
        if (Platform.Version >= 33) {
            return PermissionsAndroid.requestMultiple([
                PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES,
                PermissionsAndroid.PERMISSIONS.READ_MEDIA_VIDEO,
            ]).then(
                (statuses) =>
                    statuses[PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES] ===
                    PermissionsAndroid.RESULTS.GRANTED &&
                    statuses[PermissionsAndroid.PERMISSIONS.READ_MEDIA_VIDEO] ===
                    PermissionsAndroid.RESULTS.GRANTED,
            );
        } else {
            return PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE).then((status) => status === PermissionsAndroid.RESULTS.GRANTED);
        }
    };

    return await getRequestPermissionPromise();
}


export const savePicture = async (url, type = 'auto') => {
    if (Platform.OS === "android" && !(await hasAndroidPermission())) {
        console.log('not permission')
        return;
    }
    const fileName = url.split('/').pop().split('?')[0];
    const localFilePath = `${RNFS.DocumentDirectoryPath}/${fileName}`;
    const downloadResult = await RNFS.downloadFile({
        fromUrl: url,
        toFile: localFilePath
    }).promise
    if (downloadResult.statusCode == 200) {
        CameraRoll.saveAsset(`file://${localFilePath}`, { type: type })
            .then(
                (res) => {
                    showToast('已保存到系统相册')
                }
            )
            .catch(error => showToast('保存失败'))
    } else {
        showToast('保存失败')
    }
}