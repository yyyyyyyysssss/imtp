package org.imtp.api.controller;

import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.FileChunkDTO;
import org.imtp.api.domain.dto.FileInfoDTO;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.vo.FileUploadProgressVO;
import org.imtp.api.service.FileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/19 9:55
 */
@RequestMapping("/file")
@RestController
@Slf4j
public class FileController {

    @Resource(name = "minioFileService") // 使用本地文件服务
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

    //根据上传id获取访问文件访问路径
    @GetMapping("/accessUrl")
    public Result<String> accessUrl(@RequestParam("uploadId") String uploadId){
        String accessUrl = fileService.accessUrl(uploadId);
        return ResultGenerator.ok(accessUrl);
    }

    //根据上传id获取访问文件临时访问路径
    @GetMapping("/{uploadId}/temporaryUrl")
    public Result<?> temporaryUrl(@PathVariable("uploadId") String uploadId,@RequestParam(required = false,value = "expiryHours", defaultValue = "1") Integer expiryHours){
        String temporaryUrl = fileService.temporaryUrl(uploadId, Duration.ofHours(expiryHours));
        return ResultGenerator.ok(temporaryUrl);
    }

    //简单上传 只能上传最大不超过 20MB 的文件
    @PostMapping(value = "/upload/simple",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> simpleUpload(@RequestPart("file") MultipartFile file){
        String accessUrl = fileService.simpleUpload(file);
        return ResultGenerator.ok(accessUrl);
    }

    //获取文件信息
    @GetMapping("/{bucketName}/{objectName}/info")
    public Result<?> fileInfo(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) {
        FileInfoDTO fileInfo = fileService.getFileInfo(bucketName, objectName);
        return ResultGenerator.ok(fileInfo);
    }

    //获取文件
    @GetMapping("/{bucketName}/{objectName}")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable("bucketName") String bucketName,
                                                         @PathVariable("objectName") String objectName,
                                                         @RequestParam(required = false,value = "type") String type,
                                                         @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<FileRangeDTO> rangeList = parseRange(range);
        if(rangeList != null && !rangeList.isEmpty()){
            long contentLength = rangeList.stream().mapToLong(FileRangeDTO::getContentLength).sum();
            httpHeaders.setContentLength(contentLength);
        }
        Tuple2<StreamingResponseBody, Map<String, String>> fileStream = fileService.getFileStream(bucketName, objectName,rangeList);
        StreamingResponseBody streamingResponseBody = fileStream.getV1();
        Map<String, String> headerMap = fileStream.getV2();
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


    private List<FileRangeDTO> parseRange(String range) {
        List<FileRangeDTO> rangeList = null;
        if(range != null && !range.isEmpty()) {
            rangeList = new ArrayList<>();
            String[] ranges = range.replace("bytes=", "").split(",");
            for (String rangeStr : ranges) {
                String[] limits = rangeStr.split("-");
                long start = Objects.equals(limits[0], "") ? 0 : Long.parseLong(limits[0]);
                long end = limits.length > 1 ? Long.parseLong(limits[1]) : -1;
                FileRangeDTO fileRangeDTO = new FileRangeDTO(start,end);
                rangeList.add(fileRangeDTO);
            }
        }
        return rangeList;
    }

}
