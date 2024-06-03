package org.imtp.common.packet.body;

import lombok.*;

import java.util.List;

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
public class UserGroupInfo {

    private Long id;

    private String groupName;

    private String avatar;

    private List<UserFriendInfo> groupUserInfos;

    @Override
    public String toString() {
        return "UserGroupInfo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", groupUserInfos=" + groupUserInfos +
                '}';
    }
}
