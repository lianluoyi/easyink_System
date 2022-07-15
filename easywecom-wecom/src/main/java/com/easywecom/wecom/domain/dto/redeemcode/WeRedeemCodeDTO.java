package com.easywecom.wecom.domain.dto.redeemcode;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ClassName： WeRedeemCodeDTO
 *
 * @author wx
 * @date 2022/7/6 10:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("兑换码DTO")
public class WeRedeemCodeDTO {

    @ApiModelProperty(value = "code, 兑换码本身")
    @TableField("code")
    @NotNull(message = "兑换码不能为空")
    private String code;

    @ApiModelProperty(value = "activityId, 活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    @ApiModelProperty(value = "有效期")
    private String effectiveTime;

    @ApiModelProperty(value = "领取人userId")
    private String receiveUserId;

    @ApiModelProperty(value = "corpId")
    private String corpId;

    //分页查询的数据

    @ApiModelProperty(value = "领取状态 1:已领取, 0:未领取")
    private Integer status;

    @ApiModelProperty(value = "领取人昵称")
    private String receiveName;

    @ApiModelProperty(value = "领取时间范围,开始时间")
    private String receiveStartTime;

    @ApiModelProperty(value = "领取时间范围,结束时间")
    private String receiveEndTime;

    @ApiModelProperty(value = "externalUserIdList")
    private List<String> externalUserIdList;

    /**
     * 返回实体类
     *
     * @return
     */
    public WeRedeemCode setAddOrUpdateWeRedeemCode() {
        WeRedeemCode weRedeemCode = new WeRedeemCode();
        weRedeemCode.setCode(code);
        weRedeemCode.setActivityId(String.valueOf(activityId));
        weRedeemCode.setEffectiveTime(effectiveTime);
        weRedeemCode.setStatus(status);
        weRedeemCode.setReceiveUserId(receiveUserId);
        return weRedeemCode;
    }


}
