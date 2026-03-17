package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CourseCategory;

import java.util.List;

public interface CourseCategoryService {

    Page<CourseCategory> getPage(int pageNum, int pageSize, CourseCategory courseCategory);

    List<CourseCategory> getList(CourseCategory courseCategory);

    CourseCategory getById(Long id);

    boolean save(CourseCategory courseCategory);

    boolean updateById(CourseCategory courseCategory);

    boolean deleteById(Long id);

    List<CourseCategory> getAll();
}
