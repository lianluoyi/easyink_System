package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@TableName("we_group_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("群成员实体")
public class WeGroupMember {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "群成员id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "群id")
    @TableField("chat_id")
    @NotBlank(message = "groupId")
    private String chatId;

    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "群成员名称")
    @TableField("name")
    private String memberName;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识")
    @TableField("union_id")
    private String unionId;

    @ApiModelProperty(value = "加群时间")
    @TableField("join_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinTime;

    @ApiModelProperty(value = "加入方式: 1 - 由群成员邀请入群（直接邀请入群）;2 - 由群成员邀请入群（通过邀请链接入群）;3 - 通过扫描群二维码入群")
    @TableField("join_scene")
    private Integer joinScene;

    @ApiModelProperty(value = "成员类型:1 - 企业成员;2 - 外部联系人")
    @TableField("type")
    private Integer joinType;

    @ApiModelProperty(value = "邀请者userId")
    @TableField("invitor")
    private String invitor;

    @ApiModelProperty(value = "邀请者姓名")
    @TableField(exist = false)
    private String invitorName;

    public String getUnionId() {
        if (unionId == null) {
            return StringUtils.EMPTY;
        }
        return unionId;
    }


    public String getInvitor() {
        if (invitor == null) {
            return StringUtils.EMPTY;
        }
        return invitor;
    }
}
