package org.imtp.api.domain.vo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryVO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean enabled;

    private Integer sort;

    private String extJson;

    private String createTime;

    private String creatorName;

    private String updateTime;

    private String updaterName;

}
