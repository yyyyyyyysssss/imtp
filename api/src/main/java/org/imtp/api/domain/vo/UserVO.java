package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Gender;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 23:38
 */
@Getter
@Setter
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private Boolean enabled;

    private Gender gender;

    private String avatar;

    private String email;

    private String phone;

    private String region;

    private String createTime;

    private String updateTime;

    private List<Long> roleIds;

}
