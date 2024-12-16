import api from "../api/api";
import { showToast } from "./Utils";
import RNFS from 'react-native-fs';

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

    static #chunkFile(filePath, fileSize, chunkSize) {
        const chunks = [];
        if(fileSize <= chunkSize){
            chunks.push({start: 0,end: fileSize})
            return chunks
        }
        const totalChunks = Math.ceil(fileSize / chunkSize);
        for(let i = 0; i < totalChunks; i++){
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
        const chunks = this.#chunkFile(filePath,fileSize,sliceSize)
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
        console.log('uploadId', uploadId)
        let index = 0;
        for(const chunk of chunks){
            console.log(`第${index + 1}块正在上传,当前块大小:${(chunk.end - chunk.start).toFixed(2)}MB,起始偏移量: ${chunk.start} 结束偏移量: ${chunk.end}`);
            index++;
        }
    }


}



export default Uplaod;