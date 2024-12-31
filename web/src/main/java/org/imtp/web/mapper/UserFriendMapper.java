package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.web.domain.entity.UserFriend;

import java.util.List;

@Mapper
public interface UserFriendMapper extends BaseMapper<UserFriend> {

    List<UserFriendInfo> findUserFriendByUserId(@Param("userId") String userId);

}
