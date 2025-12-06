package org.imtp.api.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryItemQueryDTO extends PageQueryDTO{

    private Long dictId;

    private String keyword;

    private Boolean enabled;

}
