package org.imtp.server.service;

import org.imtp.server.entity.User;

import java.util.List;

public interface UserService {

    //保存用户
    boolean save(User user);

    //查询用户
    User findByUserId(Long userId);

    //根据用户查询关联的好友
    List<User> findFriendByUserId(Long userId);

    //根据群组id查询用户
    List<User> findUserByGroupId(Long userId);

}
