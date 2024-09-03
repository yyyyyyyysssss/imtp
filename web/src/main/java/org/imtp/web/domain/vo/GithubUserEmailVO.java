package org.imtp.web.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/16 11:44
 */
@Getter
@Setter
public class GithubUserEmailVO {

    private String email;

    private Boolean primary;

    private Boolean verified;

    private String visibility;

}
