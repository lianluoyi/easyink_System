package com.easywecom.wecom.domain.vo.moment;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.easywecom.wecom.domain.entity.moment.VisibleRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 获取任务创建结果VO
 *
 * @author 佚名
 * @date 2022/1/6 17:26
 */
@Data
@ApiModel("获取任务创建结果VO")
public class MomentTaskResultVO extends WeResultDTO {
    @ApiModelProperty("任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成")
    private Integer status;
    @ApiModelProperty("操作类型，字节串，此处固定为add_moment_task")
    private String type;
    @ApiModelProperty("详细的处理结果。当任务完成后此字段有效")
    private AddMomentResult result;

    @ApiModel("任务创建结果")
    @Data
    public class AddMomentResult extends WeResultDTO {
        @ApiModelProperty("朋友圈id，可通过获取客户朋友圈企业发表的列表接口获取朋友圈企业发表的列表")
        private String moment_id;
        @ApiModelProperty("不合法的执行者列表，包括不存在的id以及不在应用可见范围内的部门或者成员")
        private VisibleRange.SenderList invalid_sender_list;
        @ApiModelProperty("不合法的客户列表")
        private VisibleRange.ExternalContactList invalid_external_contact_list;
    }
}
