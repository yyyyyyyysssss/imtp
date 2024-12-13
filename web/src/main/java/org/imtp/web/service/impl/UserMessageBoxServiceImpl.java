package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.web.domain.entity.UserMessageBox;
import org.imtp.web.mapper.UserMessageBoxMapper;
import org.imtp.web.service.UserMessageBoxService;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 20:59
 */
@Service
public class UserMessageBoxServiceImpl extends ServiceImpl<UserMessageBoxMapper, UserMessageBox> implements UserMessageBoxService {
}
