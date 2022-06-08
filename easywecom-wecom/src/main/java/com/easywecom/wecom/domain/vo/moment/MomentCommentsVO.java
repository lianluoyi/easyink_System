package com.easywecom.wecom.domain.vo.moment;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.easywecom.wecom.domain.entity.moment.MomentComments;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： MomentCommentsVO
 *
 * @author 佚名
 * @date 2022/1/6 16:06
 */
@Data
@ApiModel("客户朋友圈互动数据VO")
public class MomentCommentsVO extends WeResultDTO {

    @ApiModelProperty("评论列表")
    private List<MomentComments> comment_list;
    @ApiModelProperty("点赞列表")
    private List<MomentComments> like_list;
}
