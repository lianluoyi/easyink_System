package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.wecom.domain.vo.WeGroupCodeVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 社区运营 - 关键词拉群任务实体
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_keyword_group")
@ApiModel("关键词拉群模型")
public class WeKeywordGroupTask extends BaseEntity {

    @ApiModelProperty(value = "关键词拉群任务主键")
    @TableId
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称", required = true)
    @TableField("task_name")
    @Size(max = 100, message = "任务名最大长度100")
    @NotNull(message = "任务名不能为空")
    private String taskName;

    @ApiModelProperty(value = "群活码id", required = true)
    @TableField("group_code_id")
    @NotNull(message = "群活码不能为空")
    private Long groupCodeId;

    @ApiModelProperty(value = "加群欢迎语", required = true)
    @TableField("welcome_msg")
    @Size(max = 255, message = "引导语过长,最大长度255")
    @NotNull(message = "引导语不可为空")
    private String welcomeMsg;


    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词", required = true)
    @Size(max = 255, message = "关键词长度过长。最大长度255")
    @NotNull(message = "关键词不可为空")
    @TableField("keywords")
    private String keywords;

    /**
     * 逻辑删除
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 群活码信息
     */
    @ApiModelProperty(value = "群活码信息")
    @TableField(exist = false)
    private WeGroupCodeVO groupCodeInfo;

    /**
     * 实际群聊名称
     */
    @ApiModelProperty(value = "实际群聊名称字符串")
    @TableField(exist = false)
    private String groupNameList;
}
