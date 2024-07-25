package org.imtp.web.service;

import org.imtp.web.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    User findByUsername(String username);

    UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException;

}
