package com.easyink.wecom.domain.vo.moment;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.moment.MomentCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 客户可见范围VO
 *
 * @author 佚名
 * @date 2022/1/6 15:27
 */
@Data
@ApiModel("客户可见范围VO")
public class MomentCustomerVO extends WeResultDTO {
    @ApiModelProperty("成员可见客户列表")
    private List<MomentCustomer> customer_list;
    @ApiModelProperty("分页游标，下次请求时填写以获取之后分页的记录，如果已经没有更多的数据则返回空")
    private String next_cursor;
}
