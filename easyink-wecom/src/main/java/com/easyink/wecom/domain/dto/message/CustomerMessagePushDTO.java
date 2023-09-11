package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.vo.UserVO;
import com.easyink.wecom.domain.vo.sop.DepartmentVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类名： CustomerMessagePushDTO
 *
 * @author 佚名
 * @date 2021/9/13 18:06
 */
@SuppressWarnings("all")
@Data
@ApiModel
public class CustomerMessagePushDTO implements Cloneable {
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;

    @ApiModelProperty(value = "消息id 更新时必传")
    private Long messageId;

    @ApiModelProperty("群发类型 0 发给客户 1 发给客户群")
    private String pushType;

    @ApiModelProperty("消息范围 0 全部客户  1 指定客户")
    private String pushRange;

    @ApiModelProperty("客户标签id列表用逗号隔开")
    private String tag;

    @ApiModelProperty("过滤标签id列表用逗号隔开")
    private String filterTags;

    @ApiModelProperty("过滤员工列表，用逗号隔开")
    private String filterUsers;

    @ApiModelProperty("过滤部门列表，用逗号隔开")
    private String filterDepartments;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性 -1-全部")
    private Integer gender;

    @ApiModelProperty(value = "部门id ','分隔")
    private String department;

    @ApiModelProperty(value = "部门序列")
    private List<DepartmentVO> departmentList;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("员工id 用逗号隔开")
    private String staffId;

    @ApiModelProperty(value = "员工序列")
    private List<UserVO> userList;

    @ApiModelProperty(value = "过滤的员工序列")
    private List<UserVO> filterUserList;

    @ApiModelProperty(value = "过滤的部门序列")
    private List<DepartmentVO> filterDepartmentList;

    @ApiModelProperty("发送时间 为空表示立即发送 ，不为空为指定时间发送")
    private String settingTime;

    @ApiModelProperty("客户添加开始时间")
    private Date customerStartTime;
    @ApiModelProperty("客户添加结束时间")
    private Date customerEndTime;

    @ApiModelProperty("消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 用逗号隔开")
    private String messageType;

    /********************附件信息*******************/
    @ApiModelProperty("消息附件 （统计发送人数接口可不传）")
    private List<Attachment> attachments;

    @ApiModelProperty("文本消息")
    private TextMessageDTO textMessage;


    @Override
    public CustomerMessagePushDTO clone() throws CloneNotSupportedException {
        return (CustomerMessagePushDTO) super.clone();
    }


}
