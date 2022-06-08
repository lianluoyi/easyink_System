package com.easywecom.wecom.domain.vo.moment;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： AddMomentTaskVO
 *
 * @author 佚名
 * @date 2022/1/6 16:49
 */
@Data
@ApiModel("客户朋友圈创建发表任务VO")
public class AddMomentTaskVO extends WeResultDTO {
    @ApiModelProperty("异步任务id，最大长度为64字节，24小时有效；可使用获取发表朋友圈任务结果查询任务状态")
    private String jobid;
}
