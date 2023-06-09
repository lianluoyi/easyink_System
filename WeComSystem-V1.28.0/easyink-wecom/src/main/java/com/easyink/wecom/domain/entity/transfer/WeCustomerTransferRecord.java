package com.easyink.wecom.domain.entity.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 类名: 在职员工接替记录实体
 *
 * @author : silver_chariot
 * @date : 2021/11/29 17:26
 */
@Data
@ApiModel("在职继承分配记录表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeCustomerTransferRecord {
    @TableField("id")
    @TableId
    @ApiModelProperty(value = "主键id ")
    private Long id;

    @TableField("corp_id")
    @ApiModelProperty(value = "企业id ")
    private String corpId;

    @TableField("handover_userid")
    @ApiModelProperty(value = "原跟进成员userid ")
    private String handoverUserid;

    @TableField("external_userid")
    @ApiModelProperty(value = "待分配的外部联系人userid ")
    private String externalUserid;

    @TableField("takeover_userid")
    @ApiModelProperty(value = "接替成员的userid ")
    private String takeoverUserid;

    @TableField("hanover_username")
    @ApiModelProperty(value = "原跟进成员名称 ")
    private String hanoverUsername;

    @TableField("takeover_username")
    @ApiModelProperty(value = "跟进成员名称 ")
    private String takeoverUsername;

    @TableField("handover_department_name")
    @ApiModelProperty(value = "原跟进人部门名称 ")
    private String handoverDepartmentName;

    @TableField("takeover_department_name")
    @ApiModelProperty(value = "接替人部门名称 ")
    private String takeoverDepartmentName;

    @TableField("transfer_time")
    @ApiModelProperty(value = "分配时间 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date transferTime;

    @TableField("status")
    @ApiModelProperty(value = "接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录 , 0- 转接失败（easyink自定义）")
    private Integer status;

    @TableField("takeover_time")
    @ApiModelProperty(value = "接替时间 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date takeoverTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注 ")
    private String remark;

    /**
     * 根据接替状态,更新接替备注
     *
     * @param status 接替状态
     */
    public void updateRemark(Integer status) {
        if (status == null) {
            return;
        }
        // 如果失败状态则根据失败错误码,设置响应的错误备注
        if (CustomerTransferStatusEnum.CUSTOMER_EXCEED_LIMIT.getType().equals(status)
                || CustomerTransferStatusEnum.REFUSE.getType().equals(status)) {
            setRemark(CustomerTransferStatusEnum.getDescByStatus(status));
            return;
        }
        /// 如果接替成功,则把备注清空
        if (CustomerTransferStatusEnum.SUCCEED.getType().equals(status)) {
            setRemark(StringUtils.EMPTY);
        }
    }
}

