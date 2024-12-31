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

    private String note;

    private String notePinyin;

    private String tagline;

    private Gender gender;

    private String avatar;

    private String region;

    @Override
    public String toString() {
        return "UserFriendInfo{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", nickname='" + nickname + '\'' +
                ", note='" + note + '\'' +
                ", tagline='" + tagline + '\'' +
                ", gender=" + gender +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
