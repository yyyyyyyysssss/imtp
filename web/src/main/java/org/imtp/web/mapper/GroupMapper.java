package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.common.packet.body.GroupUserInfo;
import org.imtp.web.domain.entity.Group;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    List<Group> findGroupByUserId(@Param("userId") String userId);

    List<GroupUserInfo> findGroupUserInfoByGroupIdsAndUserId(@Param("groupIds") Collection<Long> groupIds, @Param("userId") Long userId);

}
