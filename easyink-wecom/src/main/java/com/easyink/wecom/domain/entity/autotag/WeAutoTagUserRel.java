package com.easyink.wecom.domain.entity.autotag;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 标签与员工使用范围表(WeAutoTagUserRel)表实体类
 *
 * @author tigger
 * @since 2022-02-27 15:52:45
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeAutoTagUserRel {
    /**
     * 规则id
     */
    @TableField("rule_id")
    private Long ruleId;
    /**
     * 员工id or 部门id
     */
    @TableField("target_id")
    private String targetId;

    /**
     * type：0 target表示员工id， type:1 target表示部门id
     */
    @TableField("type")
    private Integer type;
}

