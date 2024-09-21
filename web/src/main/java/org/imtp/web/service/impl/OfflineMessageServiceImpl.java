package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.web.domain.entity.OfflineMessage;
import org.imtp.web.mapper.OfflineMessageMapper;
import org.imtp.web.service.OfflineMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:47
 */
@Service
public class OfflineMessageServiceImpl extends ServiceImpl<OfflineMessageMapper, OfflineMessage> implements OfflineMessageService {
}
