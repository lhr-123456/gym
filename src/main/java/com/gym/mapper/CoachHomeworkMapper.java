package com.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachHomework;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CoachHomeworkMapper extends BaseMapper<CoachHomework> {

    @Select("SELECT * FROM coach_homework WHERE jiaolian_id = #{coachId} AND deleted = 0 ORDER BY chuangjian_shijian DESC")
    List<CoachHomework> selectByCoachId(@Param("coachId") Long coachId);

    @Select("SELECT * FROM coach_homework WHERE huiyuan_id = #{memberId} AND deleted = 0 ORDER BY zhuangtai ASC, mubiao_riqi ASC")
    List<CoachHomework> selectByMemberId(@Param("memberId") Long memberId);

    @Select("SELECT * FROM coach_homework WHERE huiyuan_id = #{memberId} AND deleted = 0 AND zhuangtai = 0 ORDER BY mubiao_riqi ASC")
    List<CoachHomework> selectPendingByMemberId(@Param("memberId") Long memberId);

    @Select("<script>" +
            "SELECT * FROM coach_homework WHERE jiaolian_id = #{coachId} AND deleted = 0 " +
            "<if test='status != null'> AND zhuangtai = #{status} </if>" +
            "<if test='memberId != null'> AND huiyuan_id = #{memberId} </if>" +
            "ORDER BY chuangjian_shijian DESC" +
            "</script>")
    Page<CoachHomework> selectPageByCoach(@Param("coachId") Long coachId,
                                          @Param("memberId") Long memberId,
                                          @Param("status") Integer status,
                                          Page<CoachHomework> page);
}
