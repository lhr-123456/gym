package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.MemberConsumption;
import com.gym.mapper.MemberConsumptionMapper;
import com.gym.service.MemberConsumptionService;
import org.springframework.stereotype.Service;

@Service
public class MemberConsumptionServiceImpl extends ServiceImpl<MemberConsumptionMapper, MemberConsumption> implements MemberConsumptionService {
}
