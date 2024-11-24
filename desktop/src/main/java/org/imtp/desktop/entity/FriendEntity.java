package org.imtp.desktop.entity;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.Gender;

import java.util.Objects;

@Getter
@Setter
public class FriendEntity {

    private Long id;

    private String name;

    private String avatar;

    private Gender gender;

    private String account;

    @Override
    public String toString() {
        return "FriendEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", account='" + account + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FriendEntity that = (FriendEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
