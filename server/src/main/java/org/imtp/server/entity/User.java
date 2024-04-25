package org.imtp.server.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:33
 */
@Getter
@Setter
public class User {

    private Long id;

    private Long account;

    private String password;

    private String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account=" + account +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
