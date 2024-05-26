package org.imtp.common.packet.body;

import lombok.*;
import org.imtp.common.enums.Gender;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 12:37
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFriendInfo {

    private Long id;

    private String account;

    private String nickname;

    private Gender gender;

    private String avatar;

    @Override
    public String toString() {
        return "UserFriendInfo{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
