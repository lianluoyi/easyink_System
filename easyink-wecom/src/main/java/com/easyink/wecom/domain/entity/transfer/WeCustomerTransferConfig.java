package com.easyink.wecom.domain.entity.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 类名: 继承设置表实体
 *
 * @author : silver_chariot
 * @date : 2021-12-01 10:50:32
 */
@Data
@ApiModel("继承设置实体")
@Builder
@AllArgsConstructor
public class WeCustomerTransferConfig implements Serializable {

    private static final long serialVersionUID = 7337293201809451831L;

    @TableField("corp_id")
    @TableId
    @ApiModelProperty(value = "企业id ")
    private String corpId;

    @TableField("enable_transfer_info")
    @ApiModelProperty(value = "继承客户信息开关（true: 开启，false：关闭） ", required = true)
    private Boolean enableTransferInfo;

    @TableField("enable_side_bar")
    @ApiModelProperty(value = "侧边栏转接客户开关（true:开启，false:关闭） ", required = true)
    private Boolean enableSideBar;

    public WeCustomerTransferConfig() {
    }
}
