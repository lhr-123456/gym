package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CourseCategory;
import com.gym.mapper.CourseCategoryMapper;
import com.gym.service.CourseCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    private final CourseCategoryMapper courseCategoryMapper;

    public CourseCategoryServiceImpl(CourseCategoryMapper courseCategoryMapper) {
        this.courseCategoryMapper = courseCategoryMapper;
    }

    @Override
    public Page<CourseCategory> getPage(int pageNum, int pageSize, CourseCategory courseCategory) {
        Page<CourseCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(courseCategory.getCategoryName()), CourseCategory::getCategoryName, courseCategory.getCategoryName())
               .eq(courseCategory.getStatus() != null, CourseCategory::getStatus, courseCategory.getStatus())
               .orderByAsc(CourseCategory::getSortOrder)
               .orderByDesc(CourseCategory::getCreateTime);
        return courseCategoryMapper.selectPage(page, wrapper);
    }

    @Override
    public List<CourseCategory> getList(CourseCategory courseCategory) {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(courseCategory.getCategoryName()), CourseCategory::getCategoryName, courseCategory.getCategoryName())
               .eq(courseCategory.getStatus() != null, CourseCategory::getStatus, courseCategory.getStatus())
               .orderByAsc(CourseCategory::getSortOrder)
               .orderByDesc(CourseCategory::getCreateTime);
        return courseCategoryMapper.selectList(wrapper);
    }

    @Override
    public CourseCategory getById(Long id) {
        return courseCategoryMapper.selectById(id);
    }

    @Override
    public boolean save(CourseCategory courseCategory) {
        return courseCategoryMapper.insert(courseCategory) > 0;
    }

    @Override
    public boolean updateById(CourseCategory courseCategory) {
        return courseCategoryMapper.updateById(courseCategory) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return courseCategoryMapper.deleteById(id) > 0;
    }

    @Override
    public List<CourseCategory> getAll() {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCategory::getStatus, 1)
               .orderByAsc(CourseCategory::getSortOrder);
        return courseCategoryMapper.selectList(wrapper);
    }
}
