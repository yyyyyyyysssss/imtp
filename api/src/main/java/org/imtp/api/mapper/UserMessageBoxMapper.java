package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.api.domain.entity.UserMessageBox;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 13:14
 */
@Mapper
public interface UserMessageBoxMapper extends BaseMapper<UserMessageBox> {

    List<UserMessageBox> findLatestMessage(@Param("sessionIds") List<Long> sessionIds);

}
