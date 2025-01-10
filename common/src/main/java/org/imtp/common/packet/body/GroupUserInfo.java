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
public class GroupUserInfo {

    private Long id;

    private Long groupId;

    private String nickname;

    private String note;

    private String avatar;

    @Override
    public String toString() {
        return "GroupUserInfo{" +
                "groupId=" + groupId +
                ", id=" + id +
                ", nickname='" + nickname + '\'' +
                ", note='" + note + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
