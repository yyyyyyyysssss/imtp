package org.imtp.common.packet.body;

import lombok.*;

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

    private String nickname;

    private String gender;

    private String avatar;

    @Override
    public String toString() {
        return "UserFriendInfo{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
