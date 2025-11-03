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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService, IService<User> {

    boolean saveOrUpdate(User user);

    User findByUsername(String username);

    User findByUserId(Serializable userId);

    List<UserVO> findByUserId(Collection<Long> userIds);

    List<UserVO> findByRoleId(Long roleId);

    UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException;

    UserCreateVO createUser(UserCreateDTO userCreateDTO);

    Boolean updateUser(UserUpdateDTO userUpdateDTO, Boolean isFullUpdate);

    String resetPassword(Long userId);

    PageInfo<UserVO> queryList(UserQueryDTO queryDTO);

    UserVO details(Long id);

    PageInfo<UserVO> search(Integer pageNum,Integer pageSize,String name,List<Long> ids);

    Boolean bindRoles(Long id, List<Long> roleIds);

    Boolean deleteUser(Long id);

}
