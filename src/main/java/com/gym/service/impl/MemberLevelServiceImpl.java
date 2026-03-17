package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.MemberLevel;
import com.gym.mapper.MemberLevelMapper;
import com.gym.service.MemberLevelService;
import org.springframework.stereotype.Service;

@Service
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {
}
