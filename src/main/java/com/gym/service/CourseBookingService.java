package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.CourseBooking;

import java.util.List;

public interface CourseBookingService extends IService<CourseBooking> {

    Page<CourseBooking> getPageWithDetails(int pageNum, int pageSize, CourseBooking query);

    List<CourseBooking> getListWithDetails(CourseBooking query);

    boolean approveBooking(Long bookingId);

    boolean rejectBooking(Long bookingId, String reason);

    boolean signIn(Long bookingId);
}
