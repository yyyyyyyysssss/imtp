package org.imtp.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.server.entity.UserFriend;
import org.imtp.server.mapper.UserFriendMapper;
import org.imtp.server.service.UserFriendService;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:45
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {
}
