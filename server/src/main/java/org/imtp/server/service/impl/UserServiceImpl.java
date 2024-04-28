package org.imtp.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.server.entity.User;
import org.imtp.server.mapper.UserMapper;
import org.imtp.server.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:40
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
