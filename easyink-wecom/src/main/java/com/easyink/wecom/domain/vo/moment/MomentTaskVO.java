package com.easyink.wecom.domain.vo.moment;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.moment.MomentTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 朋友圈成员执行情况VO
 *
 * @author 佚名
 * @date 2022/1/6 15:07
 */
@Data
@ApiModel("朋友圈成员执行情况VO")
public class MomentTaskVO extends WeResultDTO {
    @ApiModelProperty("发表任务列表")
    private List<MomentTask> task_list;
    @ApiModelProperty("分页游标，下次请求时填写以获取之后分页的记录，如果已经没有更多的数据则返回空")
    private String next_cursor;
}
