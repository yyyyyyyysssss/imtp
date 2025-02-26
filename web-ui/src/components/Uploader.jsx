import React, { useImperativeHandle, forwardRef } from 'react'
import { Upload } from "antd"
import httpWrapper from '../api/axiosWrapper'
import styled from 'styled-components'
import fileImg from '../assets/img/file_icon.png'
import { useDispatch } from 'react-redux';
import { addUploadProgress, updateUploadProgress } from '../redux/slices/chatSlice';
import { fetchUploadId } from '../api/ApiService'

//每块5M
const sliceSize = 1024 * 1024 * 5;

const StyledUpload = styled(Upload)`
    &.ant-upload-wrapper {
        display: flex;
    }
`;

const Uploader = forwardRef((props, ref) => {
    useImperativeHandle(ref, () => ({
        uploadFile: uploadFile
    }))

    const dispatch = useDispatch()

    //上传文件
    const uploadFile = async (file, progressId = null) => {
        const totalSize = file.size;
        const filename = file.name;
        const chunks = splitFile(file, sliceSize);
        const totalChunk = chunks.length;
        if (progressId) {
            const progressInfo = {
                totalSize: totalSize,
                progress: 0,
                percentage: 0
            }
            dispatch(addUploadProgress({ progressId: progressId, progressInfo: progressInfo }))
        }
        const fileInfo = {
            filename: file.name,
            fileType: file.type,
            totalSize: file.size,
            totalChunk: totalChunk,
            chunkSize: sliceSize
        }
        const uploadId = await fetchUploadId(fileInfo)
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
            const uploadPromise = uploadByFormData(uploadFormData, progressId);
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
    const uploadByFormData = (uploadFormData, progressId = null) => {
        return new Promise((resolve) => {
            let latestUploadSize = 0
            httpWrapper
                .post("/file/upload/chunk", uploadFormData, {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    },
                    onUploadProgress: (progressEvent) => {
                        if (progressId) {
                            const progress = progressEvent.loaded - latestUploadSize
                            dispatch(updateUploadProgress({ progressId: progressId, progress: progress }))
                            latestUploadSize = progressEvent.loaded
                        }
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
                <img src={fileImg} title='文件' alt='文件' style={{ width: '20px', height: '20px', display: 'flex',cursor: 'pointer' }} />
            </StyledUpload>
        </>
    );
})

export default Uploader;