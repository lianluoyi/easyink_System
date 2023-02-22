package com.easyink.wecom.domain.entity.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 表单-短链关联表(WeFormShortCodeRel)实体类
 *
 * @author wx
 * @since 2023-01-15 16:35:23
 */
@Data
@TableName("we_form_short_code_rel")
@NoArgsConstructor
@AllArgsConstructor
public class WeFormShortCodeRel {
    @ApiModelProperty("表单id")
    @TableField("form_id")
    private Integer formId;

    @ApiModelProperty("短链后面的唯一字符串（用于和域名拼接成短链）")
    @TableField("short_code")
    private String shortCode;

    @ApiModelProperty("生成短链的员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

}


