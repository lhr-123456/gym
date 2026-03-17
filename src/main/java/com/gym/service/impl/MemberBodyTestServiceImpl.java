package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.MemberBodyTest;
import com.gym.mapper.MemberBodyTestMapper;
import com.gym.service.MemberBodyTestService;
import org.springframework.stereotype.Service;

@Service
public class MemberBodyTestServiceImpl extends ServiceImpl<MemberBodyTestMapper, MemberBodyTest> implements MemberBodyTestService {
}
