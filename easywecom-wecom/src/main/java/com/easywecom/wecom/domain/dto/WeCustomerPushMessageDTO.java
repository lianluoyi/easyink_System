package com.easywecom.wecom.domain.dto;

import com.easywecom.wecom.domain.WeCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 类名： WeCustomerPushMessageDTO
 *
 * @author 佚名
 * @date 2021/11/15 12:30
 */

@ApiModel("群发客户查询对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeCustomerPushMessageDTO extends WeCustomer {
    @ApiModelProperty(value = "外部联系人性别 -1全部 0-未知 1-男性 2-女性")
    private Integer gender;
    private String filterTags;
    @ApiModelProperty("开始时间")
    private Date customerStartTime;
    @ApiModelProperty("结束时间")
    private Date customerEndTime;

    public WeCustomerPushMessageDTO(String users, String tags,String corpId) {
        this.setUserIds(users);
        this.setTagIds(tags);
        this.setCorpId(corpId);
    }
}
