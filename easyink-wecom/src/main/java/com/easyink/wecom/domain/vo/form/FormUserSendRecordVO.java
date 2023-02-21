package com.easyink.wecom.domain.vo.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工发送表单记录VO
 *
 * @author wx
 * 2023/1/29 17:13
 **/
@NoArgsConstructor
@Data
public class FormUserSendRecordVO {
    @ApiModelProperty("发送智能表单的员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("员工名称")
    @TableField("user_name")
    @Excel(name = "员工", sort = 1)
    private String userName;

    @ApiModelProperty("员工头像地址url")
    @TableField("user_head_image")
    private String userHeadImage;

    @ApiModelProperty("员工所属部门")
    private String departmentName;

    @ApiModelProperty("点击人数")
    @Excel(name = "点击人数", sort = 2)
    private Integer clickCount;

    @ApiModelProperty("提交人数")
    @Excel(name = "提交人数", sort = 3)
    private Integer submitCount;

    @ApiModelProperty("填写率")
    @Excel(name = "填写率", sort = 4)
    private String submitPercent;
}
