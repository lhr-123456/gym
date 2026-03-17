package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseCategory;
import com.gym.service.CourseCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course/category")
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    public CourseCategoryController(CourseCategoryService courseCategoryService) {
        this.courseCategoryService = courseCategoryService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CourseCategory>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseCategory courseCategory) {
        Page<CourseCategory> page = courseCategoryService.getPage(pageNum, pageSize, courseCategory);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<CourseCategory>> getList(CourseCategory courseCategory) {
        List<CourseCategory> list = courseCategoryService.getList(courseCategory);
        return ApiResponse.success(list);
    }

    @GetMapping("/all")
    public ApiResponse<List<CourseCategory>> getAll() {
        List<CourseCategory> list = courseCategoryService.getAll();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseCategory> getById(@PathVariable Long id) {
        CourseCategory courseCategory = courseCategoryService.getById(id);
        if (courseCategory == null) {
            return ApiResponse.error("分类不存在");
        }
        return ApiResponse.success(courseCategory);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseCategory courseCategory) {
        boolean result = courseCategoryService.save(courseCategory);
        if (result) {
            return ApiResponse.success("添加分类成功");
        }
        return ApiResponse.error("添加分类失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CourseCategory courseCategory) {
        if (courseCategory.getCategoryId() == null) {
            return ApiResponse.error("分类 ID 不能为空");
        }
        boolean result = courseCategoryService.updateById(courseCategory);
        if (result) {
            return ApiResponse.success("更新分类成功");
        }
        return ApiResponse.error("更新分类失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseCategoryService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除分类成功");
        }
        return ApiResponse.error("删除分类失败");
    }
}
