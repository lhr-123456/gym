package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.EquipmentInfo;

import java.util.List;

public interface EquipmentInfoService {

    Page<EquipmentInfo> getEquipmentPage(int pageNum, int pageSize, EquipmentInfo equipmentInfo);

    EquipmentInfo getById(Long equipmentId);

    boolean save(EquipmentInfo equipmentInfo);

    boolean updateById(EquipmentInfo equipmentInfo);

    boolean deleteById(Long equipmentId);

    List<EquipmentInfo> list(EquipmentInfo equipmentInfo);
}
