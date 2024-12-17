import api from "../api/api";
import { showToast } from "./Utils"
import RNFS from 'react-native-fs'
import { NativeModules } from 'react-native';


const { UploadChunkModule } = NativeModules

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

    static async uploadChunks(filePath, fileName, fileType, fileSize) {
        //分块
        const chunks = this.#chunkFile(fileSize, sliceSize)
        const totalChunk = chunks.length
        const fileInfo = {
            filename: fileName,
            fileType: fileType,
            totalSize: fileSize,
            totalChunk: totalChunk,
            chunkSize: sliceSize
        }
        console.log(`文件名称: ${fileName}; 文件总大小: ${(fileSize / (1024 * 1024)).toFixed(2)}MB; 总块数: ${totalChunk};`);
        //获取uploadId
        const uploadId = await this.#getUploadId(fileInfo);
        console.log('uploadId:', uploadId)
        for (let i = 0; i < totalChunk; i++) {
            const { start, end } = chunks[i]
            const readChunkSize = end - start
            console.log(`第${i + 1}块正在上传,当前块大小:${(readChunkSize / (1024 * 1024)).toFixed(2)}MB,起始偏移量: ${start} 结束偏移量: ${end}`);
            // const chunk = await RNFS.read(filePath, readChunkSize, start, 'base64')
        }
        UploadChunkModule.upload(filePath)
        .then(
            (res) => {
                console.log('UploadChunkModule',res)
            }
        )
    }


}



export default Uplaod;