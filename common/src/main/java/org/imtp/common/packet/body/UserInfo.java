package org.imtp.common.packet.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 14:10
 */
@Getter
@Setter
public class UserInfo {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String gender;

    private String avatar;

}
