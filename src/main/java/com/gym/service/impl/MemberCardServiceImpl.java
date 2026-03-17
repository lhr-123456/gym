package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.MemberCard;
import com.gym.mapper.MemberCardMapper;
import com.gym.service.MemberCardService;
import org.springframework.stereotype.Service;

@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements MemberCardService {
}
