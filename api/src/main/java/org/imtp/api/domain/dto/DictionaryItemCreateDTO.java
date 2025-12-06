package org.imtp.api.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryItemCreateDTO {

    @NotNull(message = "字典id不能为空")
    private Long dictId;

    private Long parentId;

    @NotBlank(message = "字典项名称不能为空")
    private String label;

    @NotBlank(message = "字典项值不能为空")
    private String value;

    private String alias;

    private Boolean status;

    private String imgUrl;

    private Integer sort;

    private String extJson;

}
