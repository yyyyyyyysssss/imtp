package org.imtp.api.service;

import org.imtp.api.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    boolean saveOrUpdate(User user);

    User findByUsername(String username);

    User findByUserId(String userId);

    UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException;

}
