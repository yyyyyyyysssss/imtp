package org.imtp.api.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryUpdateDTO {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "名称不能为空",groups = UpdateGroup.class)
    private String name;

    private Boolean enabled;

    private String description;

    private Integer sort;

    private String extJson;

    public interface UpdateGroup{}

}
