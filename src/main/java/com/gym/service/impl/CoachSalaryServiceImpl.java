package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachSalary;
import com.gym.mapper.CoachSalaryMapper;
import com.gym.service.CoachSalaryService;
import org.springframework.stereotype.Service;

@Service
public class CoachSalaryServiceImpl extends ServiceImpl<CoachSalaryMapper, CoachSalary> implements CoachSalaryService {
}
