package com.easyink.wecom.domain.vo;

import com.easyink.common.enums.EmployCodeSourceEnum;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：SelectWeEmplyCodeWelcomeMsgVO
 *
 * @author Society my sister Li
 * @date 2021-11-05 09:50
 */
@Data
public class SelectWeEmplyCodeWelcomeMsgVO {

    /**
     * 员工活码ID
     */
    private String empleCodeId;

    /**
     * 活码场景（活码名称）
     */
    private String scenario;

    /**
     * 欢迎语-文本
     */
    private String welcomeMsg;

    /**
     * 欢迎语-素材（员工活码使用）
     */
    private List<AddWeMaterialDTO> materialList;

    /**
     * 素材排序
     */
    private String[] materialSort;

    /**
     * 数据来源 {@link EmployCodeSourceEnum}
     */
    private Integer source;

    /**
     * 新客进群使用的群活码链接
     */
    private String groupCodeUrl;

    /**
     * 是否打标签 0:否,1:是
     */
    private Boolean tagFlag;

    /**
     * 备注类型：0：不备注，1：在昵称前，2：在昵称后
     */
    private Integer remarkType;

    /**
     * 备注名
     */
    private String remarkName;

    /**
     * 兑换码
     */
    private String redeemCode;

    /**
     * 兑换码锁
     */
    private String redeemCodeRedisKey;

    @ApiModelProperty(value = "欢迎语类型，0：普通欢迎语，1：活动欢迎语")
    private Integer welcomeMsgType;

    @ApiModelProperty(value = "兑换码活动id")
    private String codeActivityId;

    @ApiModelProperty(value = "有可使用兑换码，发送该欢迎语", hidden = true)
    private String codeSuccessMsg;

    @ApiModelProperty(value = "有可使用兑换码,使用该附件排序", hidden = true)
    private String[] codeSuccessMaterialSort;

    @ApiModelProperty(value = "没有可用的兑换码，或者兑换活动已被删除，发送该欢迎语", hidden = true)
    private String codeFailMsg;

    @ApiModelProperty(value = "没有可用的兑换码，或者兑换活动已被删除，使用该附件排序", hidden = true)
    private String[] codeFailMaterialSort;

    @ApiModelProperty(value = "客户再次触发，若活动开启参与限制，发送该欢迎语", hidden = true)
    private String codeRepeatMsg;

    @ApiModelProperty(value = "客户再次触发，若活动开启参与限制，使用附件排序", hidden = true)
    private String[] codeRepeatMaterialSort;

    @ApiModelProperty(value = "活动欢迎语type，1：发送成功，2：发送失败，3：发送限制", hidden = true)
    private Integer redeemCodeActivityType;
}
