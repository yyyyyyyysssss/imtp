package org.imtp.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.entity.User;
import org.imtp.server.mapper.UserMapper;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:37
 */
@Slf4j
@Service
public class H2DBChatService implements ChatService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        Wrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public List<User> findFriendByUserId(Long userId) {
        return null;
    }

    @Override
    public List<User> findUserByGroupId(Long groupId) {
        return null;
    }


    @Override
    public boolean saveOfflineMessage(OfflineMessage offlineMessage) {
        return false;
    }

    @Override
    public List<OfflineMessage> findOfflineMessageByUserId(Long userId) {
        return List.of();
    }
}
