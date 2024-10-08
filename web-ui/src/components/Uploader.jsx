import React,{useImperativeHandle,forwardRef} from 'react'
import { Upload,message } from "antd"
import httpWrapper from '../api/axiosWrapper'
import styled from 'styled-components'
import fileImg from '../assets/img/file_icon.png'

//每块5M
const sliceSize = 1024 * 1024 * 5;

const StyledUpload = styled(Upload)`
    &.ant-upload-wrapper {
        display: flex;
    }
`;

const Uploader = forwardRef((props,ref) => {
    useImperativeHandle(ref,() => ({
        uploadFile: uploadFile
    }))
    //上传文件
    const uploadFile = async (file) => {
        let uploadId = file.key;
        if (!uploadId) {
            await httpWrapper
                .get('/file/uploadId', {
                    params: {
                        filename: file.name,
                        totalSize: file.size
                    }
                })
                .then(
                    (res) => {
                        uploadId = res.data;
                    },
                    (error) => {
                        message.error(error);
                        return;
                    }
                );
        }
        const totalSize = file.size;
        const filename = file.name;
        const chunks = splitFile(file, sliceSize);
        const totalChunk = chunks.length;
        console.log(`文件名称: ${filename}; 文件总大小: ${(totalSize / (1024 * 1024)).toFixed(2)}MB; 总块数: ${totalChunk}`);
        // 存储所有分片上传的 Promise
        const uploadPromises = [];
        let index = 0;
        for (const chunk of chunks) {
            console.log(`第${index + 1}块正在上传,当前块大小:${(chunk.size / (1024 * 1024)).toFixed(2)}MB,起始偏移量: ${index * sliceSize} 结束偏移量: ${index * sliceSize + chunk.size}`);
            const uploadFormData = new FormData();
            uploadFormData.append("uploadId", uploadId);
            uploadFormData.append("totalSize", totalSize);
            uploadFormData.append("totalChunk", totalChunk);
            uploadFormData.append("chunkSize", sliceSize);
            uploadFormData.append("chunkIndex", index);
            uploadFormData.append("filename", filename);
            uploadFormData.append("file", chunk);
            const uploadPromise = uploadByFormData(uploadFormData);
            uploadPromises.push(uploadPromise);
            index++;
        }
        await Promise.all(uploadPromises);
        return new Promise((resolve) => {
            //上传完成获取访问url
            httpWrapper
                .get('/file/accessUrl', {
                    params: {
                        uploadId: uploadId
                    }
                }).then(
                    (res) => {
                        resolve(res.data);
                    }
                );
        })
    }
    //上传分片
    const uploadByFormData = (uploadFormData) => {
        return new Promise((resolve) => {
            httpWrapper
                .post("/file/upload", uploadFormData, {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    }
                }).then(
                    (res) => {
                        resolve();
                    }
                );
        });
    }
    //文件分片
    const splitFile = (file, chunkSize) => {
        const chunks = [];
        if (file.size <= chunkSize) {
            chunks.push(file);
            return chunks;
        }
        const totalChunks = Math.ceil(file.size / chunkSize);
        for (let i = 0; i < totalChunks; i++) {
            const s = i * chunkSize;
            const e = Math.min(file.size, s + chunkSize);
            const chunk = file.slice(s, e);
            chunks.push(chunk);
        }
        return chunks;
    }

    return (
        <>
            <StyledUpload {...props}>
                <img src={fileImg} alt='文件' style={{ width: '20px', height: '20px', display: 'flex' }} />
            </StyledUpload>
        </>
    );
})

export default Uploader;