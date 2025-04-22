package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.api.domain.entity.UserMessageBox;
import org.imtp.api.mapper.UserMessageBoxMapper;
import org.imtp.api.service.UserMessageBoxService;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 20:59
 */
@Service
public class UserMessageBoxServiceImpl extends ServiceImpl<UserMessageBoxMapper, UserMessageBox> implements UserMessageBoxService {
}
