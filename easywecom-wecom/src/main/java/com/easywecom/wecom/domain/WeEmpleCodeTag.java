package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 员工活码标签对象 we_emple_code_tag
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@TableName("we_emple_code_tag")
public class WeEmpleCodeTag {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "标签id")
    @TableField("tag_id")
    private String tagId;

    @ApiModelProperty(value = "标签名称")
    @TableField("tag_name")
    private String tagName;

    @ApiModelProperty(value = "员工活码id")
    @TableField("emple_code_id")
    private Long empleCodeId;

    @ApiModelProperty(value = "0:正常;2:删除;")
    @TableField("del_flag")
    private Integer delFlag = 0;

}
