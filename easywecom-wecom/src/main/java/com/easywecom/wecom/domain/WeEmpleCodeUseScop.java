package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工活码使用人对象 we_emple_code_use_scop
 *
 * @author 佚名
 * @date 2021-7-28
 */
@ApiModel
@Data
@TableName("we_emple_code_use_scop")
public class WeEmpleCodeUseScop {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "员工活码id")
    @TableField("emple_code_id")
    private Long empleCodeId;

    @ApiModelProperty(value = "业务id类型1:组织机构id,2:成员id")
    @TableField("business_id_type")
    private Integer businessIdType;

    @ApiModelProperty(value = "活码下使用人/部门姓名")
    @TableField("business_name")
    private String businessName;

    @ApiModelProperty(value = "活码类型下业务使用人的id,传入部门时为部门id")
    @TableField("business_id")
    private String businessId;

    @ApiModelProperty(value = "0:正常;2:删除;")
    @TableField("del_flag")
    private Integer delFlag = 0;

    @ApiModelProperty(value = "部门id，现在不启用")
    @TableField("party_id")
    private Long partyId;

    /**
     * 活码使用人员手机号
     */
    @ApiModelProperty("活码使用人员手机号")
    @TableField(exist = false)
    private String mobile;


}
