package org.imtp.api.mapping;


import org.imtp.api.domain.dto.UserCreateDTO;
import org.imtp.api.domain.dto.UserUpdateDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.UserVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @org.mapstruct.Builder(disableBuilder = true),uses = {LocalDateTimeMapper.class,LocalDateMapper.class})
public interface UserMapping {

    UserMapping INSTANCE = Mappers.getMapper(UserMapping.class);


    User toUser(UserCreateDTO userCreateDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    UserVO toUserVO(User user);

    @IterableMapping(elementTargetType = UserVO.class)
    List<UserVO> toUserVO(List<User> users);

}
