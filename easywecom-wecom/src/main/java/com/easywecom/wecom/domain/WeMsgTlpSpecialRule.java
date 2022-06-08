package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Time;
import java.util.List;


/**
 * 特殊规则欢迎语表 we_msg_tlp_special_rule
 *
 * @author Administrator
 */
@Data
@TableName("we_msg_tlp_special_rule")
@ApiModel("特殊规则欢迎语表")
public class WeMsgTlpSpecialRule {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", hidden = true)
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "默认欢迎语模板id", hidden = true)
    @TableField("msg_tlp_id")
    private Long msgTlpId;

    @ApiModelProperty(value = "特殊欢迎语模板消息")
    @TableField("special_welcome_msg")
    private String specialWelcomeMsg;

    @ApiModelProperty(value = "特殊欢迎语消息规则类型 1:周策略欢迎语", hidden = true)
    @TableField("rule_type")
    private Integer ruleType;

    @ApiModelProperty(value = "1-7 周一到周日,多个逗号隔开", hidden = true)
    @TableField("weekends")
    private String weekends;

    @ApiModelProperty(value = "周策略开始时间")
    @TableField("weekend_begin_time")
    private Time weekendBeginTime;

    @ApiModelProperty(value = "周策略结束时间")
    @TableField("weekend_end_time")
    private Time weekendEndTime;

    @TableField(exist = false)
    @ApiModelProperty("特殊时段素材-查询修改使用")
    private List<WeMsgTlpMaterial> specialMaterialList;
    @TableField(exist = false)
    @ApiModelProperty("需要删除的特殊时段欢迎语附件ids")
    private List<Long> removeSpecialRuleMaterialIds;

    @ApiModelProperty(value = "1-7 星期几")
    @TableField(exist = false)
    private String[] weekendList;

}
