package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberInfo;

import java.util.List;

public interface MemberInfoService {

    Page<MemberInfo> getMemberPage(int pageNum, int pageSize, MemberInfo memberInfo);

    MemberInfo getById(Long memberId);

    boolean save(MemberInfo memberInfo);

    boolean updateById(MemberInfo memberInfo);

    boolean deleteById(Long memberId);

    List<MemberInfo> list(MemberInfo memberInfo);
}
