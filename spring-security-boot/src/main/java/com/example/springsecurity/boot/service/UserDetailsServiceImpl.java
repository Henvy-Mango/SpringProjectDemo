package com.example.springsecurity.boot.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Naomi
 * @date 2022/1/10 22:38
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("调用 loadUserByUsername 方法");

        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }

        String password = new BCryptPasswordEncoder().encode("admin");

        // 权限角色配置
        List<GrantedAuthority> authorityList =
                AuthorityUtils.commaSeparatedStringToAuthorityList("root,ROLE_ADMIN");

        return new User(username, password, authorityList);
    }
}
