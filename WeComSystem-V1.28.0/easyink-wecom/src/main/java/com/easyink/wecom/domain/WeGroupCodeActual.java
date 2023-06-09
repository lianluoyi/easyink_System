package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


/**
 * 实际群码对象
 * 类名： WeGroupCodeActual
 *
 * @author 佚名
 * @date 2021/9/30 16:07
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("实际群码对象")
@Data
@TableName("we_group_code_actual")
public class WeGroupCodeActual extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "群活码id")
    @NotNull(message = "群活码id不能为空")
    @TableField("group_code_id")
    private Long groupCodeId;

    @ApiModelProperty(value = "实际群码")
    @TableField("actual_group_qr_code")
    @NotNull(message = "实际群活码称不能为空")
    private String actualGroupQrCode;

    @ApiModelProperty(value = "群名称")
    @TableField("group_name")
    @NotNull(message = "群名称不能为空")
    @Size(max = 100, message = "群名称长度已超出限制")
    private String groupName;

    @ApiModelProperty(value = "有效期")
    @TableField("effect_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date effectTime;

    @ApiModelProperty(value = "扫码次数限制")
    @TableField("scan_code_times_limit")
    @NotNull(message = "次数限制不能为空")
    private Integer scanCodeTimesLimit;


    @ApiModelProperty(value = "群聊id")
    @TableField("chat_id")
    private String chatId;

    @ApiModelProperty(value = "群聊名称")
    @TableField("chat_group_name")
    private String chatGroupName;

    @ApiModelProperty(value = "扫码次数")
    @TableField("scan_code_times")
    private Integer scanCodeTimes;

    @ApiModelProperty(value = "0:正常使用;2:删除;")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty(value = "0:使用中 1:已停用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "群ids，逗号分割")
    @TableField("chat_ids")
    private String chatIds;

    @ApiModelProperty(value = "场景。1 - 群的小程序插件 2 - 群的二维码插件")
    @TableField("scene")
    private Integer scene;

    @ApiModelProperty(value = "联系方式的备注信息，用于助记，超过30个字符将被截断")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "起始序号")
    @TableField("room_base_id")
    private Integer roomBaseId;

    @ApiModelProperty(value = "群名前缀")
    @TableField("room_base_name")
    private String roomBaseName;

    @ApiModelProperty(value = "是否自动新建群。0-否；1-是。 默认为1")
    @TableField("auto_create_room")
    private Integer autoCreateRoom;

    @ApiModelProperty(value = "企业自定义的state参数，用于区分不同的入群渠道")
    @TableField("state")
    private String state;

    @ApiModelProperty(value = "加群配置id")
    @TableField("config_id")
    private String configId;

    @ApiModelProperty(value = "排序字段,目前由前端控制")
    @TableField("sort_no")
    private Integer sortNo;


    /**
     * 以下是非表字段
     */
    @ApiModelProperty(value = "是否即将过期 0：否 1：是")
    @TableField(exist = false)
    private Integer isExpire;

    /**
     * 创建者
     */
    @TableField(exist = false)
    private String createBy;

    /**
     * 更新者
     */
    @TableField(exist = false)
    private String updateBy;

}
