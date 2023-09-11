package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.enums.WeEmployCodeRemarkTypeEnum;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;


/**
 * 员工活码
 * 类名： WeEmpleCode
 *
 * @author 佚名
 * @date 2021/9/30 15:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("we_emple_code")
@ApiModel("员工活码")
public class WeEmpleCode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "新增联系方式的配置id")
    @TableField("config_id")
    private String configId;

    @ApiModelProperty(value = "活码类型:1:单人;2:多人;3:批量", required = true)
    @TableField("code_type")
    @NotNull(message = "codeType不能为空")
    private Integer codeType;

    @ApiModelProperty(value = "自动成为好友:0：否，1：全天，2：时间段", required = true)
    @TableField("skip_verify")
    @NotNull(message = "skipVerify不能为空")
    private Integer skipVerify;

    @ApiModelProperty(value = "活动场景", required = true)
    @TableField("scenario")
    @Size(max = 32, message = "活动场景长度已超出限制")
    @NotBlank(message = "scenario不能为空")
    private String scenario;

    @ApiModelProperty(value = "场景描述")
    @TableField("description")
    @Size(max = 64, message = "场景描述已超出限制")
    private String description;

    @ApiModelProperty(value = "获客链接id")
    @TableField("link_id")
    private String linkId;

    @ApiModelProperty(value = "欢迎语")
    @TableField("welcome_msg")
    @Size(max = 2000, message = "欢迎语长度已超出限制")
    private String welcomeMsg;

    @ApiModelProperty(value = "0:正常;1:删除;")
    @TableField("del_flag")
    private Integer delFlag = 0;

    @ApiModelProperty(value = "二维码链接")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "用于区分客户具体是通过哪个「联系我」添加。不能超过30个字符")
    @TableField("state")
    private String state;

    @TableField("source")
    @ApiModelProperty(value = "来源类型：0：活码创建，1：新客建群创建")
    private Integer source;

    @TableField("remark_type")
    @ApiModelProperty(value = "备注类型：0：不备注，1：在昵称前，2：在昵称后", required = true)
    @NotNull(message = "remarkType不能为空")
    private Integer remarkType = WeEmployCodeRemarkTypeEnum.NO.getRemarkType();

    @TableField("remark_name")
    @ApiModelProperty(value = "备注名")
    private String remarkName;

    @TableField("effect_time_open")
    @ApiModelProperty(value = "开启时间 HH:mm")
    private String effectTimeOpen;

    @TableField("effect_time_close")
    @ApiModelProperty(value = "关闭时间 HH:mm")
    private String effectTimeClose;

    @TableField("material_sort")
    @ApiModelProperty(value = "附件排序")
    @JsonIgnore
    private String[] materialSort;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用员工", required = true)
    @NotEmpty(message = "员工信息不能为空")
    private List<WeEmpleCodeUseScop> weEmpleCodeUseScops;

    @TableField(exist = false)
    @ApiModelProperty("扫码标签")
    private List<WeEmpleCodeTag> weEmpleCodeTags;

    @TableField(exist = false)
    @ApiModelProperty("素材")
    private List<AddWeMaterialDTO> materialList;

    @TableField("tag_flag")
    @NotNull(message = "tagFlag不能为空")
    @ApiModelProperty(value = "标签开关(默认开启状态):0关闭,1开启 ", required = true)
    private Integer tagFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "群活码主键ID", required = true)
    private Long groupCodeId;

    @ApiModelProperty(value = "欢迎语类型，0：普通欢迎语，1：活动欢迎语", required = true)
    @TableField("welcome_msg_type")
    private Integer welcomeMsgType;

    @TableField("code_activity_id")
    @ApiModelProperty(value = "兑换码活动id")
    @JsonIgnore
    private String codeActivityId;

    @TableField(exist = false)
    @ApiModelProperty("兑换码活动实体")
    private WeRedeemCodeActivity codeActivity;

    @TableField("code_success_msg")
    @ApiModelProperty(value = "有可使用兑换码，发送该欢迎语")
    private String codeSuccessMsg;

    @TableField("code_success_material_sort")
    @ApiModelProperty(value = "有可使用兑换码,使用该附件排序")
    @JsonIgnore
    private String[] codeSuccessMaterialSort;

    @ApiModelProperty(value = "有可使用兑换码,使用该素材")
    @TableField(exist = false)
    private List<AddWeMaterialDTO> codeSuccessMaterialList;

    @TableField("code_fail_msg")
    @ApiModelProperty(value = "没有可用的兑换码，或者兑换活动已被删除，发送该欢迎语")
    private String codeFailMsg;

    @TableField("code_fail_material_sort")
    @ApiModelProperty(value = "没有可用的兑换码，或者兑换活动已被删除，使用该附件排序")
    @JsonIgnore
    private String[] codeFailMaterialSort;

    @ApiModelProperty(value = "没有可用的兑换码,，或者兑换活动已被删除,使用该素材")
    @TableField(exist = false)
    private List<AddWeMaterialDTO> codeFailMaterialList;

    @TableField("code_repeat_msg")
    @ApiModelProperty(value = "客户再次触发，若活动开启参与限制，发送该欢迎语")
    private String codeRepeatMsg;

    @TableField("code_repeat_material_sort")
    @ApiModelProperty(value = "客户再次触发，若活动开启参与限制，使用附件排序")
    @JsonIgnore
    private String[] codeRepeatMaterialSort;

    @ApiModelProperty(value = "客户再次触发,若活动开启参与限制,使用该素材")
    @TableField(exist = false)
    private List<AddWeMaterialDTO> codeRepeatMaterialList;


    @ApiModelProperty(value = "小程序活码短链url")
    @TableField("app_link")
    private String appLink;


    public void buildCodeMsg() {
        this.codeSuccessMsg = Optional.ofNullable(this.codeSuccessMsg).orElseGet(() -> StringUtils.EMPTY);
        this.codeSuccessMaterialSort = Optional.ofNullable(codeSuccessMaterialSort).orElseGet(() -> new String[]{});
        this.codeSuccessMaterialList = Optional.ofNullable(this.codeSuccessMaterialList).orElseGet(Lists::newArrayList);

        this.codeFailMsg = Optional.ofNullable(this.codeFailMsg).orElseGet(() -> StringUtils.EMPTY);
        this.codeFailMaterialSort = Optional.ofNullable(this.codeFailMaterialSort).orElseGet(() -> new String[]{});
        this.codeFailMaterialList = Optional.ofNullable(this.codeFailMaterialList).orElseGet(Lists::newArrayList);

        this.codeRepeatMsg = Optional.ofNullable(this.codeRepeatMsg).orElseGet(() -> StringUtils.EMPTY);
        this.codeRepeatMaterialSort = Optional.ofNullable(this.codeRepeatMaterialSort).orElseGet(() -> new String[]{});
        this.codeRepeatMaterialList = Optional.ofNullable(this.codeRepeatMaterialList).orElseGet(Lists::newArrayList);
        this.codeActivityId = String.valueOf(codeActivity.getId());
    }

}
