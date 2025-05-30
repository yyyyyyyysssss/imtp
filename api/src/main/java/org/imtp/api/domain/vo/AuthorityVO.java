package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.api.domain.entity.AuthorityUrl;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 23:38
 */
@Getter
@Setter
public class AuthorityVO {

    private Long id;

    private Long parentId;

    private String parentName;

    private Long rootId;

    private String code;

    private String name;

    private String type;

    private String routePath;

    private List<AuthorityUrl> urls;

    private String icon;

    private Integer sort;

    private String createTime;

    private String updateTime;

    private List<AuthorityVO> children;

}
