import api from "../api/api";
import { showToast } from "./Utils"
import { NativeModules } from 'react-native';


const { UploadModule } = NativeModules

//每块5M
const sliceSize = 1024 * 1024 * 5;

class Uplaod {

    static async #getUploadId(fileInfo) {
        return new Promise((resolve, reject) => {
            api.post('/file/uploadId', fileInfo)
                .then(
                    (res) => {
                        resolve(res.data)
                    },
                    (error) => {
                        reject(error)
                    }
                )
        })

    }

    static #chunkFile(fileSize, chunkSize) {
        const chunks = [];
        if (fileSize <= chunkSize) {
            chunks.push({ start: 0, end: fileSize })
            return chunks
        }
        const totalChunks = Math.ceil(fileSize / chunkSize);
        for (let i = 0; i < totalChunks; i++) {
            const s = i * chunkSize;
            const e = Math.min(fileSize, s + chunkSize);
            chunks.push({
                start: s,
                end: e
            })
        }
        return chunks
    }

    static async uploadChunks(filePath, fileName, fileType, fileSize,progressId = null) {
        const fileInfo = {
            filePath: filePath,
            filename: fileName,
            fileType: fileType,
            fileSize: fileSize,
            progressId: progressId
        }
        return new Promise((resolve, reject) => {
            UploadModule.upload(JSON.stringify(fileInfo))
                .then(
                    (res) => {
                        resolve(res)
                    },
                    (error) => {
                        reject(error)
                    }
                )
        })
    }
}



export default Uplaod;