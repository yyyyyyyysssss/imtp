package org.imtp.web.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.UploadChunkDTO;
import org.imtp.web.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 9:55
 */
@RequestMapping("/file")
@RestController
@Slf4j
public class FileController {

    @Resource
    private FileService minioFileService;

    @GetMapping("/uploadId")
    public Result<String> uploadId(@RequestParam("filename") String filename,@RequestParam("totalSize") Long totalSize){
        String uploadId = minioFileService.uploadId(filename,totalSize);
        return ResultGenerator.ok(uploadId);
    }

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(UploadChunkDTO uploadChunkDTO){
        minioFileService.upload(uploadChunkDTO);
    }

    @GetMapping("/accessUrl")
    public Result<String> accessUrl(@RequestParam("uploadId") String uploadId){
        String accessUrl = minioFileService.accessUrl(uploadId);
        return ResultGenerator.ok(accessUrl);
    }

}
