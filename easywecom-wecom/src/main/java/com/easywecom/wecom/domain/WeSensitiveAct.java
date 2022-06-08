package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

/**
 * @author 佚名
 * @date 2021-7-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "we_sensitive_act")
@ApiModel(value = "敏感行为")
public class WeSensitiveAct extends BaseEntity {
    private static final long serialVersionUID = -7550455294164753520L;

    @ApiModelProperty(value = "敏感行为id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "敏感行为名称")
    @TableField("act_name")
    @NotBlank(message = "敏感行为名称不能为空")
    private String actName;

    @ApiModelProperty(value = "排序字段")
    @TableField("order_num")
    private Integer orderNum;

    @ApiModelProperty(value = "记录敏感行为,1 开启 0 关闭")
    @TableField("enable_flag")
    private Integer enableFlag;

    @ApiModelProperty(value = "删除标识，1 已删除 0 未删除")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("actName", getActName())
                .append("orderNum", getOrderNum())
                .append("enableFlag", getEnableFlag())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("corpId", getCorpId())
                .toString();
    }
}
