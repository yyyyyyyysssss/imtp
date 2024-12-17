package org.imtp.web.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.FileChunkDTO;
import org.imtp.web.domain.dto.FileInfoDTO;
import org.imtp.web.domain.vo.FileUploadProgressVO;
import org.imtp.web.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 9:55
 */
@RequestMapping("/file")
@RestController
@Slf4j
public class FileController {

    @Resource(name = "minioFileService")
    private FileService fileService;

    //分片上传前置获取当前上传id
    @PostMapping("/uploadId")
    public Result<String> uploadId(@RequestBody FileInfoDTO fileInfoDTO){
        String uploadId = fileService.uploadId(fileInfoDTO);
        return ResultGenerator.ok(uploadId);
    }

    //分片上传
    @PostMapping(value = "/upload/chunk",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadChunk(FileChunkDTO uploadChunkDTO){
        fileService.uploadChunk(uploadChunkDTO);
    }

    //获取上传进度
    @GetMapping("/upload/progress")
    public Result<?> uploadProgress(@RequestParam("uploadId") String uploadId){
        FileUploadProgressVO fileUploadProgressVO = fileService.uploadProgress(uploadId);
        return ResultGenerator.ok(fileUploadProgressVO);
    }

    //根据上传id获取访问文件的路径
    @GetMapping("/accessUrl")
    public Result<String> accessUrl(@RequestParam("uploadId") String uploadId){
        String accessUrl = fileService.accessUrl(uploadId);
        return ResultGenerator.ok(accessUrl);
    }

    @GetMapping("/temporaryUrl")
    public Result<?> temporaryUrl(@RequestParam("uploadId") String uploadId){
        String temporaryUrl = fileService.temporaryUrl(uploadId);
        return ResultGenerator.ok(temporaryUrl);
    }

    //简单上传 只能上传最大不超过 20MB 的文件
    @PostMapping(value = "/upload/simple",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> simpleUpload(@RequestPart("file") MultipartFile file){
        String accessUrl = fileService.simpleUpload(file);
        return ResultGenerator.ok(accessUrl);
    }

}
