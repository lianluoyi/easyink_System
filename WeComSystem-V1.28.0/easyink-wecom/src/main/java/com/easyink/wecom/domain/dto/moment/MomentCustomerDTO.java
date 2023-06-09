package com.easyink.wecom.domain.dto.moment;

import com.easyink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： 朋友圈客户可见范围DTO
 *
 * @author 佚名
 * @date 2022/1/6 15:21
 */

@Data
@ApiModel("朋友圈客户可见范围DTO")
@NoArgsConstructor
public class MomentCustomerDTO extends MomentTaskDTO {
    @ApiModelProperty("企业发表成员userid，如果是企业创建的朋友圈，可以通过获取客户朋友圈企业发表的列表获取已发表成员userid，如果是个人创建的朋友圈，创建人userid就是企业发表成员userid")
    private String userid;

    public MomentCustomerDTO(String momentId, String userid,String nextCusor) {
        this.userid = userid;
        this.setMoment_id(momentId);
        if (StringUtils.isNotBlank(nextCusor)){
            this.setCursor(nextCusor);
        }
    }
}
