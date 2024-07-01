package com.easyink.wecom.domain.entity.moment;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.enums.moment.MomentSelectUserEnum;
import com.easyink.common.enums.moment.MomentStatusEnum;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.domain.dto.moment.CreateMomentTaskDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;


/**
 * 类名： 朋友圈任务信息表
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Data
@TableName("we_moment_task")
@ApiModel("朋友圈任务信息表实体")
@NoArgsConstructor
@AllArgsConstructor
public class WeMomentTaskEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;
    /**
     * 朋友圈id
     */
    @ApiModelProperty(value = "朋友圈id")
    @TableField("moment_id")
    private String momentId;
    /**
     * 企业微信异步任务id 24小时有效
     */
    @ApiModelProperty(value = "企业微信异步任务id 24小时有效")
    @TableField("job_id")
    private String jobId;

    @ApiModelProperty(value = "文本内容 2000字符")
    @TableField("content")
    private String content;

    /**
     * 发布类型（0：企业 1：个人）
     */
    @ApiModelProperty(value = "发布类型（0：企业 1：个人）")
    @TableField("type")
    private Integer type;
    /**
     * 任务类型（0：立即发送 1：定时发送）
     */
    @ApiModelProperty(value = "任务类型（0：立即发送 1：定时发送）")
    @TableField("task_type")
    private Integer taskType;
    /**
     * 任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成
     */
    @ApiModelProperty(value = "任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成")
    @TableField("status")
    private Integer status;
    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    @TableField("send_time")
    private Date sendTime;

    @ApiModelProperty("是否选择员工（0：未选择 1：已选择）")
    @TableField("select_user")
    private Integer selectUser;
    /**
     * 所属员工
     */
    @ApiModelProperty(value = "所属员工")
    @TableField("users")
    private String users;

    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属员工")
    @TableField("departments")
    private String departments;

    /**
     * 客户标签
     */
    @ApiModelProperty(value = "客户标签")
    @TableField("tags")
    private String tags;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty("创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty("可见范围（0：全部客户 1：部分客户）")
    @TableField("push_range")
    private Integer pushRange;

    /**
     * 根据传入值构造朋友圈任务实体
     *
     * @param createMomentTaskDTO 参数
     * @return {@link WeMomentTaskEntity}
     */
    public WeMomentTaskEntity(CreateMomentTaskDTO createMomentTaskDTO, List<String> users, List<String> departments, String createBy) {
        this.setId(SnowFlakeUtil.nextId());
        BeanUtils.copyProperties(createMomentTaskDTO, this);
        this.setUsers(String.join(StrUtil.COMMA, users));
        this.setDepartments(String.join(StrUtil.COMMA, departments));
        this.setTags(String.join(StrUtil.COMMA, createMomentTaskDTO.getTags()));
        this.setStatus(MomentStatusEnum.START.getType());
        // 是否选择了员工或者部门
        this.selectUser = CollectionUtils.isNotEmpty(createMomentTaskDTO.getUsers()) || CollectionUtils.isNotEmpty(createMomentTaskDTO.getDepartments())
                ? MomentSelectUserEnum.SELECT_USER.getType() : MomentSelectUserEnum.NOT_SELECT_USER.getType();
        if (createMomentTaskDTO.getText() != null && StringUtils.isNotBlank(createMomentTaskDTO.getText().getContent())) {
            this.content = createMomentTaskDTO.getText().getContent();
        }
        this.createBy = createBy;
    }

}
