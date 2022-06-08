package com.easywecom.wecom.domain.entity.moment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 客户朋友圈互动数据
 *
 * @author 佚名
 * @date 2022/1/6 16:14
 */

@ApiModel("客户朋友圈互动数据")
@Data
public class MomentComments {
    @ApiModelProperty("评论/点赞的外部联系人userid")
    private String external_userid;
    @ApiModelProperty("评论/点赞的企业成员userid，userid与external_userid不会同时出现")
    private String userid;
    @ApiModelProperty("评论/点赞时间")
    private Long create_time;
}
