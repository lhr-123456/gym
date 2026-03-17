package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachCertificate;
import com.gym.mapper.CoachCertificateMapper;
import com.gym.service.CoachCertificateService;
import org.springframework.stereotype.Service;

@Service
public class CoachCertificateServiceImpl extends ServiceImpl<CoachCertificateMapper, CoachCertificate> implements CoachCertificateService {
}
