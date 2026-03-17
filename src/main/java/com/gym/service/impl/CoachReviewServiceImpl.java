package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachReview;
import com.gym.mapper.CoachReviewMapper;
import com.gym.service.CoachReviewService;
import org.springframework.stereotype.Service;

@Service
public class CoachReviewServiceImpl extends ServiceImpl<CoachReviewMapper, CoachReview> implements CoachReviewService {
}
