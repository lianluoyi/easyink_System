package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.domain.vo.groupcode.GroupCodeDetailVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 客户群活码对象
 * 类名： WeGroupCode
 *
 * @author 佚名
 * @date 2021/9/30 16:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_group_code")
@ApiModel("客户群活码对象")
public class WeGroupCode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id",hidden = true)
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "企业Id",hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "二维码链接")
    @TableField("code_url")
    private String codeUrl;

    @ApiModelProperty(value = "二维码标识符")
    @TableField("uuid")
    private String uuid;

    @ApiModelProperty(value = "活码头像链接")
    @TableField("activity_head_url")
    private String activityHeadUrl;

    @ApiModelProperty(value = "活码名称")
    @TableField("activity_name")
    @Size(max = 32, message = "活码名称长度已超出限制")
    @NotBlank(message = "活码名称不能为空")
    private String activityName;

    @ApiModelProperty(value = "活码描述")
    @TableField("activity_desc")
    @Size(max = 64, message = "活码描述长度已超出限制")
    private String activityDesc;

    @ApiModelProperty(value = "场景")
    @TableField("activity_scene")
    @Size(max = 60, message = "场景最大长度为60个字符")
    private String activityScene;

    @ApiModelProperty(value = "引导语")
    @TableField("guide")
    @Size(max = 2000, message = "加群引导语长度已超出限制")
    private String guide;

    @ApiModelProperty(value = "进群是否提示:1:是;0:否;")
    @TableField("join_group_is_tip")
    private Integer joinGroupIsTip;

    @ApiModelProperty(value = "进群提示语")
    @TableField("tip_msg")
    @Size(max = 10, message = "进群提示语长度已超出限制")
    private String tipMsg;

    @ApiModelProperty(value = "客服二维码")
    @TableField("customer_server_qr_code")
    @Size(max = 150, message = "客服二维码提示语最大长度为150个字符")
    private String customerServerQrCode;

    @ApiModelProperty(value = "0:正常;2:删除;")
    @TableField("del_flag")
    @JsonIgnore
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag = 0;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "头像路径")
    @TableField("avatar_url")
    private String avatarUrl;

    @ApiModelProperty(value = "")
    @TableField("show_tip")
    private Integer showTip;

    @ApiModelProperty("实际群码顺序")
    @TableField("seq")
    private String seq;

    @ApiModelProperty("创建类型 1:群二维码 2: 企微活码")
    @TableField("create_type")
    private Integer createType;

    @ApiModelProperty("活码短链")
    @TableField("app_link")
    private String appLink;


    /**
     * 可用实际码数量
     */
    @TableField(exist = false)
    @ApiModelProperty("可用实际码数量")
    private Long availableCodes;

    /**
     * 实际码扫码次数之和
     */
    @TableField(exist = false)
    @ApiModelProperty("实际码扫码次数之和")
    private Long totalScanTimes;

    /**
     * 即将过期实际码数量
     */
    @TableField(exist = false)
    @ApiModelProperty("即将过期实际码数量")
    private Long aboutToExpireCodes;

    /**
     * 实际码
     */
    @TableField(exist = false)
    @ApiModelProperty("实际码")
    private List<WeGroupCodeActual> actualList;

    @TableField(exist = false)
    @ApiModelProperty("实际码删除列表更新时使用")
    private List<Long> delActualIdList;

    /**
     * 实际码id列表,用于前端绑定实际群活码
     */
    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty("实际码id列表,用于前端绑定实际群活码")
    private List<Long> actualIdList;

    @ApiModelProperty("主部门名称")
    @TableField(exist = false)
    private String mainDepartmentName;

    @ApiModelProperty("客户群活码详情")
    @TableField(exist = false)
    private List<GroupCodeDetailVO> groupCodeDetailVOList;

}
