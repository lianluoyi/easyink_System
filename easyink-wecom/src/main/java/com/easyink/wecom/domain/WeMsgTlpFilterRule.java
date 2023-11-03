package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 欢迎语筛选条件表对象 we_msg_tlp_filter_rule
 *
 * @author lichaoyu
 * @date 2023/10/25 14:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_msg_tlp_filter_rule")
@ApiModel("欢迎语筛选条件表对象")
public class WeMsgTlpFilterRule {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "欢迎语模板id")
    @TableField("msg_tlp_id")
    private Long msgTlpId;

    @ApiModelProperty(value = "筛选类型，0：来源；1：性别")
    @TableField("filter_type")
    private Integer filterType;

    @ApiModelProperty(value = "筛选条件，0：不是；1：是")
    @TableField("filter_condition")
    private Boolean filterCondition;

    @ApiModelProperty(value = "筛选值，来源见：AddWayEnum类，性别见：WeConstans.corpUserEnum")
    @TableField("filter_value")
    private String filterValue;


    /**
     * 为筛选条件构建企业信息和欢迎语关联信息
     *
     * @param msgTlpId 欢迎语模板ID
     */
    public void builderMsgTlpInfo(Long msgTlpId) {
        this.msgTlpId = msgTlpId;
    }
}
