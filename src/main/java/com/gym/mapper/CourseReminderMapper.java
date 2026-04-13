package com.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.entity.CourseReminder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CourseReminderMapper extends BaseMapper<CourseReminder> {

    @Select("SELECT * FROM course_reminder WHERE member_id = #{memberId} AND deleted = 0 ORDER BY class_time ASC")
    List<CourseReminder> selectByMemberId(@Param("memberId") Long memberId);

    @Select("SELECT * FROM course_reminder WHERE member_id = #{memberId} AND deleted = 0 AND class_time > NOW() ORDER BY class_time ASC")
    List<CourseReminder> selectUpcomingByMemberId(@Param("memberId") Long memberId);
}
