package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_info")
public class MemberInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "huiyuan_id", type = IdType.AUTO)
    private Long memberId;

    @TableField("huiyuan_xingming")
    private String memberName;

    @TableField("xingbie")
    private String gender;

    @TableField("chusheng_riqi")
    private LocalDate birthDate;

    @TableField("shouji_haoma")
    private String phoneNum;

    @TableField("youxiang")
    private String emailAddr;

    @TableField(value = "zhuce_shijian", fill = FieldFill.INSERT)
    private LocalDateTime regTime;

    @TableField("tican_dengji")
    private String fitnessLevel;

    @TableField("zuihou_fangwen")
    private LocalDateTime lastVisit;

    @TableField("zhanghu_zhuangtai")
    private Integer accountStatus;

    @TableField("huiyuan_dengji")
    private Integer memberLevel;

    @TableField("jifen")
    private Integer points;

    @TableField("yue_e")
    private Double balance;

    @TableField("touxiang")
    private String avatar;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableLogic
    private Integer deleted;
}
