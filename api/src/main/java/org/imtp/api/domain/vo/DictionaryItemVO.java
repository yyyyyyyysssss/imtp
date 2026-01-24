package org.imtp.api.domain.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DictionaryItemVO {

    private Long id;

    private Long dictId;

    private Long parentId;

    private String label;

    private String value;

    private String alias;

    private Boolean enabled;

    private String imgUrl;

    private Integer sort;

    private String extJson;

    private String createTime;

    private String updateTime;

    private List<DictionaryItemVO> children;

}
