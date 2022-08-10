package com.easyink.wecom.domain.vo.transfer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 客户分配记录返回给前端的列表参数
 *
 * @author : silver_chariot
 * @date : 2021/12/2 14:11
 */
@Data
public class WeCustomerTransferRecordVO extends WeCustomerTransferRecord {

    @ApiModelProperty(value = "客户名称")
    @TableField(exist = false)
    private String customerName;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    @TableField(exist = false)
    private Integer customerType;

    @ApiModelProperty(value = "客户头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String corpName;

    @ApiModelProperty(value = "公司全称")
    @TableField(exist = false)
    private String corpFullName;

    @ApiModelProperty(value = "失败备注")
    @TableField(exist = false)
    private String remark;
}
