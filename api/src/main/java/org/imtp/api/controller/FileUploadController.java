package org.imtp.api.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.vo.FileMD5CheckVO;
import org.imtp.api.domain.vo.FileUploadChunkVO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.imtp.api.service.FileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 9:55
 */
@RequestMapping("/api/file")
@RestController
@Slf4j
public class FileUploadController {

    @Resource
    private FileService fileService;


    // md5检查 如果存在直接返回访问的url
    @GetMapping("/check/{md5}")
    public Result<FileMD5CheckVO> checkMD5(@PathVariable("md5")String md5){
        String accessUrl = fileService.checkMD5(md5);
        FileMD5CheckVO fileMD5CheckVO = new FileMD5CheckVO();
        if(StringUtils.isNotEmpty(accessUrl)){
            fileMD5CheckVO.setFound(true);
            fileMD5CheckVO.setAccessUrl(accessUrl);
            return ResultGenerator.ok(fileMD5CheckVO);
        } else {
            fileMD5CheckVO.setFound(false);
        }
        return ResultGenerator.ok(fileMD5CheckVO);
    }

    //分片上传前置获取当前上传id
    @PostMapping("/uploadId")
    public Result<String> uploadId(@RequestBody FileInfoDTO fileInfoDTO){
        String uploadId = fileService.getUploadId(fileInfoDTO);
        return ResultGenerator.ok(uploadId);
    }

    //分片上传
    @PostMapping(value = "/upload/chunk",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FileUploadChunkVO> uploadChunk(FileChunkDTO uploadChunkDTO){
        FileUploadChunkVO fileUploadChunkVO = fileService.uploadChunk(uploadChunkDTO);
        return ResultGenerator.ok(fileUploadChunkVO);
    }

    //获取上传进度
    @GetMapping("/upload/progress")
    public Result<FileUploadProgressVO> uploadProgress(@RequestParam("uploadId") String uploadId){
        FileUploadProgressVO fileUploadProgressVO = fileService.getUploadProgress(uploadId);
        return ResultGenerator.ok(fileUploadProgressVO);
    }

    //根据上传id获取访问文件访问路径
    @GetMapping("/accessUrl")
    public Result<String> accessUrl(@RequestParam("uploadId") String uploadId,@RequestParam(required = false,value = "expiryHours") Integer expiryHours){
        if(expiryHours != null && expiryHours > 0) {
            String temporaryUrl = fileService.generateTemporaryUrl(uploadId, Duration.ofHours(expiryHours));
            return ResultGenerator.ok(temporaryUrl);
        }
        String accessUrl = fileService.getAccessUrl(uploadId);
        return ResultGenerator.ok(accessUrl);
    }

    //简单上传 只能上传最大不超过 20MB 的文件
    @PostMapping(value = "/upload/simple",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadSimple(@RequestPart("file") MultipartFile file){
        String accessUrl = fileService.uploadSingleFile(file);
        return ResultGenerator.ok(accessUrl);
    }

}
