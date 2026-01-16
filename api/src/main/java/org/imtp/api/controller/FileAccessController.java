package org.imtp.api.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.dto.FileRangeDTO;
import org.imtp.api.domain.vo.FileInfoVO;
import org.imtp.api.domain.vo.FileStreamVO;
import org.imtp.api.service.FileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequestMapping("/file")
@RestController
@Slf4j
public class FileAccessController {

    @Resource
    private FileService fileService;

    //获取文件信息
    @GetMapping("/{bucketName}/**/info")
    public Result<FileInfoVO> fileInfo(@PathVariable("bucketName") String bucketName, HttpServletRequest request) {
        String objectName = getObjectName(request,bucketName);
        FileInfoVO fileInfo = fileService.getFileInfo(bucketName, objectName);
        return ResultGenerator.ok(fileInfo);
    }

    //获取文件
    @GetMapping("/{bucketName}/**")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable("bucketName") String bucketName,
                                                         @RequestParam(required = false,value = "type") String type,
                                                         HttpServletRequest request,
                                                         @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
                                                         @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String etag,
                                                         @RequestHeader(value = HttpHeaders.IF_MODIFIED_SINCE, required = false) String ifModifiedSince
    ) {
        String objectName = getObjectName(request,bucketName);
        boolean isDownload = type != null && (type.equalsIgnoreCase("download") || type.equalsIgnoreCase("d"));
        HttpStatus status = HttpStatus.OK;
        // 处理 ETag，避免重复加载 url生成后不在变所以不需要检查etag直接返回304
        if(etag != null && !etag.isEmpty()){
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ETAG, etag);
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .headers(headers)
                    .build();
        }
        // 处理基于最后修改时间的 304 响应
        if (ifModifiedSince != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.LAST_MODIFIED, ifModifiedSince);
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .headers(headers)
                    .build();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        FileRangeDTO fileRangeDTO = parseRange(range);
        FileStreamVO fileStream = fileService.getFileStream(bucketName, objectName, fileRangeDTO);
        StreamingResponseBody streamingResponseBody = fileStream.getStreamingResponseBody();
        Map<String, String> headerMap = fileStream.getHeaders();
        if(headerMap != null && !headerMap.isEmpty()) {
            // 将文件头信息添加到响应头中
            headerMap.forEach(httpHeaders::add);
        }
        if(isDownload){
            // 设置响应头以指示下载
            httpHeaders.setContentDispositionFormData("attachment", new File(objectName).getName());
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } else {
            // 如果不是下载设置合适的缓存策略
            httpHeaders.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic().immutable());
        }
        if(fileRangeDTO != null){
            status = HttpStatus.PARTIAL_CONTENT;
        }
        return ResponseEntity.status(status)
                .headers(httpHeaders)
                .body(streamingResponseBody);
    }

    private String getObjectName(HttpServletRequest request,String bucketName){
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String objectName = StringUtils.removeStart(fullPath, "/file/" + bucketName + "/");
        objectName = StringUtils.removeEnd(objectName, "/info");
        String pathSeparator = fileService.pathSeparator();
        return objectName.replace("/", pathSeparator);
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
