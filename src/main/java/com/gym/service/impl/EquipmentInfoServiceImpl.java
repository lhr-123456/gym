package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.EquipmentInfo;
import com.gym.mapper.EquipmentInfoMapper;
import com.gym.service.EquipmentInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EquipmentInfoServiceImpl implements EquipmentInfoService {

    private final EquipmentInfoMapper equipmentInfoMapper;

    public EquipmentInfoServiceImpl(EquipmentInfoMapper equipmentInfoMapper) {
        this.equipmentInfoMapper = equipmentInfoMapper;
    }

    @Override
    public Page<EquipmentInfo> getEquipmentPage(int pageNum, int pageSize, EquipmentInfo equipmentInfo) {
        Page<EquipmentInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EquipmentInfo> wrapper = buildQueryWrapper(equipmentInfo);
        return equipmentInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public EquipmentInfo getById(Long equipmentId) {
        return equipmentInfoMapper.selectById(equipmentId);
    }

    @Override
    public boolean save(EquipmentInfo equipmentInfo) {
        if (equipmentInfo.getStatus() == null) {
            equipmentInfo.setStatus("可用");
        }
        return equipmentInfoMapper.insert(equipmentInfo) > 0;
    }

    @Override
    public boolean updateById(EquipmentInfo equipmentInfo) {
        return equipmentInfoMapper.updateById(equipmentInfo) > 0;
    }

    @Override
    public boolean deleteById(Long equipmentId) {
        return equipmentInfoMapper.deleteById(equipmentId) > 0;
    }

    @Override
    public List<EquipmentInfo> list(EquipmentInfo equipmentInfo) {
        LambdaQueryWrapper<EquipmentInfo> wrapper = buildQueryWrapper(equipmentInfo);
        return equipmentInfoMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<EquipmentInfo> buildQueryWrapper(EquipmentInfo equipmentInfo) {
        LambdaQueryWrapper<EquipmentInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (equipmentInfo != null) {
            wrapper.eq(equipmentInfo.getEquipmentId() != null, EquipmentInfo::getEquipmentId, equipmentInfo.getEquipmentId())
                   .like(StringUtils.hasText(equipmentInfo.getEquipmentName()), EquipmentInfo::getEquipmentName, equipmentInfo.getEquipmentName())
                   .eq(StringUtils.hasText(equipmentInfo.getType()), EquipmentInfo::getType, equipmentInfo.getType())
                   .eq(StringUtils.hasText(equipmentInfo.getStatus()), EquipmentInfo::getStatus, equipmentInfo.getStatus())
                   .eq(StringUtils.hasText(equipmentInfo.getBrand()), EquipmentInfo::getBrand, equipmentInfo.getBrand());
        }
        
        wrapper.orderByDesc(EquipmentInfo::getPurchaseDate);
        return wrapper;
    }
}
