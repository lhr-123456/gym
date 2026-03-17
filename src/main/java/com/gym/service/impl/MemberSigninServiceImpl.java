package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.MemberSignin;
import com.gym.mapper.MemberSigninMapper;
import com.gym.service.MemberSigninService;
import org.springframework.stereotype.Service;

@Service
public class MemberSigninServiceImpl extends ServiceImpl<MemberSigninMapper, MemberSignin> implements MemberSigninService {
}
