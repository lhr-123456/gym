package com.gym.service.impl;

import com.gym.entity.UserInfo;
import com.gym.mapper.UserInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoMapper userInfoMapper;

    public UserDetailsServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername, username);
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        if (userInfo.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用：" + username);
        }

        String role = getUserRole(userInfo.getUserType());
        return new User(userInfo.getUsername(), userInfo.getPassword(), 
                       Collections.singletonList(new SimpleGrantedAuthority(role)));
    }

    private String getUserRole(Integer userType) {
        if (userType == null) {
            return "ROLE_MEMBER";
        }
        switch (userType) {
            case 1:
                return "ROLE_ADMIN";
            case 2:
                return "ROLE_COACH";
            case 3:
            default:
                return "ROLE_MEMBER";
        }
    }
}
