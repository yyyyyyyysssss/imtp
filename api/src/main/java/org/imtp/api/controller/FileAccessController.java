package org.imtp.api.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.vo.FileInfoVO;
import org.imtp.api.domain.vo.FileStreamVO;
import org.imtp.api.service.FileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;
import java.util.Objects;

@RequestMapping("/file")
@RestController
@Slf4j
public class FileAccessController {

    @Resource
    private FileService fileService;

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
        if(fileRangeDTO == null && type != null && (type.equalsIgnoreCase("download") || type.equalsIgnoreCase("d"))) {
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
            try {
                String[] ranges = range.replace("bytes=", "").split(",");
                if(ranges.length > 1){
                    throw new BusinessException("暂不支持多范围请求");
                }
                String[] limits = ranges[0].split("-");
                long start = Objects.equals(limits[0], "") ? 0 : Long.parseLong(limits[0]);
                long end = limits.length > 1 ? Long.parseLong(limits[1]) : -1;
                return new FileRangeDTO(start, end);
            }catch (Exception e){
                throw new BusinessException("范围解析失败: " + e.getMessage());
            }
        }
        return null;
    }

}
