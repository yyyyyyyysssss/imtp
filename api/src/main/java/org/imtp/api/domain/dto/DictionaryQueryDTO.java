package org.imtp.api.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryQueryDTO extends PageQueryDTO{

    private String keyword;

    private Boolean enabled;

}
