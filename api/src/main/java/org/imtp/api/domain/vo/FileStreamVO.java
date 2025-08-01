package org.imtp.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2025/8/1 17:13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileStreamVO {

    private StreamingResponseBody streamingResponseBody;

    private Map<String, String> headers;

}
