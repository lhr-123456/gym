package com.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberPointsRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MemberPointsRecordMapper extends BaseMapper<MemberPointsRecord> {

    @Select("<script>" +
            "SELECT * FROM member_points_record WHERE member_id = #{memberId} AND deleted = 0 " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<MemberPointsRecord> selectByMemberId(@Param("memberId") Long memberId);

    @Select("<script>" +
            "SELECT * FROM member_points_record WHERE member_id = #{memberId} AND deleted = 0 " +
            "<if test='taskType != null and taskType != \"\"'> AND task_type = #{taskType} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    Page<MemberPointsRecord> selectPageByMember(@Param("memberId") Long memberId,
                                                  @Param("taskType") String taskType,
                                                  Page<MemberPointsRecord> page);

    @Select("SELECT COUNT(*) FROM member_points_record " +
            "WHERE member_id = #{memberId} AND task_type = #{taskType} " +
            "AND DATE(create_time) = CURDATE() AND deleted = 0")
    int countTodayByMemberAndType(@Param("memberId") Long memberId, @Param("taskType") String taskType);

    @Select("SELECT COUNT(*) FROM member_points_record " +
            "WHERE member_id = #{memberId} AND task_type = #{taskType} AND deleted = 0")
    int countTotalByMemberAndType(@Param("memberId") Long memberId, @Param("taskType") String taskType);
}
