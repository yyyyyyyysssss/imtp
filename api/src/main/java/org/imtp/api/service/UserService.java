package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.UserCreateDTO;
import org.imtp.api.domain.dto.UserQueryDTO;
import org.imtp.api.domain.dto.UserUpdateDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.UserCreateVO;
import org.imtp.api.domain.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService, IService<User> {

    boolean saveOrUpdate(User user);

    User findByUsername(String username);

    User findByUserId(String userId);

    UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException;

    UserCreateVO create(UserCreateDTO userCreateDTO);

    Integer update(UserUpdateDTO userUpdateDTO);

    Integer updatePatch(UserUpdateDTO userUpdateDTO);

    String resetPassword(Long userId);

    PageInfo<UserVO> queryList(UserQueryDTO queryDTO);

    List<UserVO> listUserOptions();

    Boolean bindRoles(Long id, List<Long> roleIds);

    Integer delete(String id);

}
