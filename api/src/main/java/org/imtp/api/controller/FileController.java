package org.imtp.api.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.vo.FileInfoVO;
import org.imtp.api.domain.vo.FileStreamVO;
import org.imtp.api.domain.vo.FileUploadChunkVO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.imtp.api.service.FileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 9:55
 */
@RequestMapping("/api/file")
@RestController
@Slf4j
public class FileController {

    @Resource(name = "minioFileService") // 使用本地文件服务
    private FileService fileService;

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

    //获取文件信息
    @GetMapping("/{bucketName}/{objectName}/info")
    public Result<FileInfoVO> fileInfo(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) {
        FileInfoVO fileInfo = fileService.getFileInfo(bucketName, objectName);
        return ResultGenerator.ok(fileInfo);
    }

    //获取文件
    @GetMapping("/{bucketName}/{objectName}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable("bucketName") String bucketName,
                                                         @PathVariable("objectName") String objectName,
                                                         @RequestParam(required = false,value = "type") String type,
                                                         @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        HttpHeaders httpHeaders = new HttpHeaders();
        FileRangeDTO fileRangeDTO = parseRange(range);
        FileStreamVO fileStream = fileService.getFileStream(bucketName, objectName, fileRangeDTO);
        StreamingResponseBody streamingResponseBody = fileStream.getStreamingResponseBody();
        Map<String, String> headerMap = fileStream.getHeaders();
        if(headerMap != null && !headerMap.isEmpty()) {
            // 将文件头信息添加到响应头中
            headerMap.forEach(httpHeaders::add);
        }
        if(type != null && (type.equalsIgnoreCase("download") || type.equalsIgnoreCase("d"))) {
            // 设置响应头以指示下载
            httpHeaders.setContentDispositionFormData("attachment", objectName);
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(streamingResponseBody);
    }


    private FileRangeDTO parseRange(String range) {
        if(range != null && !range.isEmpty()) {
            String[] ranges = range.replace("bytes=", "").split(",");
            if(ranges.length > 1){
                throw new BusinessException("暂不支持多范围请求");
            }
            String[] limits = ranges[0].split("-");
            long start = Objects.equals(limits[0], "") ? 0 : Long.parseLong(limits[0]);
            long end = limits.length > 1 ? Long.parseLong(limits[1]) : -1;
            return new FileRangeDTO(start, end);
        }
        return null;
    }

}
