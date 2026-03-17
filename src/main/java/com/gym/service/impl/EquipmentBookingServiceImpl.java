package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.EquipmentBooking;
import com.gym.mapper.EquipmentBookingMapper;
import com.gym.service.EquipmentBookingService;
import org.springframework.stereotype.Service;

@Service
public class EquipmentBookingServiceImpl extends ServiceImpl<EquipmentBookingMapper, EquipmentBooking> implements EquipmentBookingService {
}
