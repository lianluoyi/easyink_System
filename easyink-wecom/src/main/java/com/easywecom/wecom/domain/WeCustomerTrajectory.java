package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.constant.Constants;
import com.easywecom.wecom.domain.vo.sop.SopAttachmentVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * 活动轨迹相关
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@TableName("we_customer_trajectory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活动轨迹相关")
public class WeCustomerTrajectory {
    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private String id;

    @ApiModelProperty(value = "轨迹类型(1:信息动态;2:社交动态;3:活动规则;4:待办动态)")
    @TableField("trajectory_type")
    private Integer trajectoryType;

    @ApiModelProperty(value = "sop任务详情id")
    @TableField("detail_id")
    private Long detailId = Constants.DEFAULT_ID;

    @ApiModelProperty(value = "sop任务待办id 逗号隔开")
    @TableField("sop_task_ids")
    private String sopTaskIds = Strings.EMPTY;


    @ApiModelProperty(value = "外部联系人id")
    @TableField("external_userid")
    private String externalUserid;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "文案内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "处理日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("create_date")
    private Date createDate;

    @ApiModelProperty(value = "处理开始时间")
    @TableField("start_time")
    private Time startTime;

    @ApiModelProperty(value = "处理结束时间")
    @TableField("end_time")
    private Time endTime;

    @ApiModelProperty(value = "0:正常;1:完成;2:删除")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "当前员工的id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "当前应用的id")
    @TableField("agent_id")
    private String agentId;

    @TableField("detail")
    @ApiModelProperty(value = "操作细节（如果是文件图片则是url,如果多个选项则,隔开)")
    private String detail;

    @TableField("sub_type")
    @ApiModelProperty(value = "子类型（修改备注：edit_remark;修改标签：edit_tag;编辑多选框：edit_multi;编辑单选：edit_choice;编辑图片：edit_pic;编辑文件：edit_file;加入群聊:join_group;退出群聊：quit_group;加好友：add_user；删除好友：del_user")
    private String subType;

    @ApiModelProperty(value = "规则附件")
    @TableField(exist = false)
    private List<SopAttachmentVO> materialList;

}
