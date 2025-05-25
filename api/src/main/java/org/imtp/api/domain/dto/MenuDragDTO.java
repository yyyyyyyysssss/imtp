package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 10:45
 */
@Getter
@Setter
public class MenuDragDTO {

    //被拖动的节点
    @NotBlank(message = "被拖动的节点id不能为空")
    private String dragId;

    //放下的目标节点
    @NotBlank(message = "放下的目标节点id不能为空")
    private String targetId;

    // "BEFORE" | "AFTER" | "INSIDE"
    @NotNull(message = "拖动位置类型不能为空")
    private Position position;

    public enum Position {
        BEFORE,
        AFTER,
        INSIDE
    }


}
