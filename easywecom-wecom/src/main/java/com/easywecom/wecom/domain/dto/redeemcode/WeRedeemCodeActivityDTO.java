package com.easywecom.wecom.domain.dto.redeemcode;



import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.wecom.domain.entity.redeemcode.RedeemCodeAlarmUser;
import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


/**
 * 新增兑换码活动DTO
 * 类名： WeRedeemCodeActivityDTO
 *
 * @author wx
 * @date 2022/7/4 11:00
 */
@Data
@ApiModel("兑换码活动DTO")
public class WeRedeemCodeActivityDTO extends WeRedeemCodeActivity {

    @ApiModelProperty(value = "使用员工")
    @TableField(exist = false)
    List<RedeemCodeAlarmUser> useUsers;

    @ApiModelProperty("当前时间，搜索库存时使用")
    private String nowTime;

    /**
     * 设置兑换码activityId
     *
     * @param activityId
     */
    public void setAlarmUserActivityId(Long activityId) {
        if (CollectionUtils.isNotEmpty(this.useUsers)) {
            this.useUsers.forEach(item -> {
                item.setActivityId(activityId);
            });
        }
    }
}
