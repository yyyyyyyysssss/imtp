package org.imtp.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/7/4 10:42
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileRangeDTO {

    private Long start;

    private Long end;


}
