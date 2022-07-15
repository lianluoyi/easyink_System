package com.easywecom.wecom.domain.vo.moment;

import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.vo.WeUserVO;
import com.easywecom.wecom.domain.vo.sop.DepartmentVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 类名： 查询朋友圈创建发表任务VO
 *
 * @author 佚名
 * @date 2022/1/12 22:50
 */
@Data
@ApiModel("查询朋友圈创建发表任务VO")
@NoArgsConstructor
public class SearchMomentVO {
    @ApiModelProperty(value = "附件类型 0:图片,2:视频,5:图文链接")
    private Integer mediaType;
    @ApiModelProperty("朋友圈附件")
    private List<WeWordsDetailEntity> weWordsDetailList;

    @ApiModelProperty("企业id")
    @JsonIgnore
    private String corpId;
    @ApiModelProperty("朋友圈任务id")
    private Long momentTaskId;

    @ApiModelProperty("朋友圈文本")
    private String content;

    @ApiModelProperty(value = "发布类型（0：企业 1：个人）")
    private Integer type;

    @ApiModelProperty(value = "任务类型（0：立即发送 1：定时发送）")
    private Integer taskType;

    @ApiModelProperty(value = "任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成")
    private Integer status;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date sendTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(value = "已发布人数")
    private Integer publishNum;

    @ApiModelProperty(value = "待发布人数")
    private Integer notPublishNum;

    @ApiModelProperty("创建人姓名 (详情页返回姓名)")
    private String createBy;

    @ApiModelProperty(value = "选择的标签id")
    private String tags;

    @ApiModelProperty("是否选择员工（0：未选择 1：已选择）")
    private Integer selectUser;

    @ApiModelProperty("所属员工id")
    private String users;

    @ApiModelProperty("所属部门id")
    private String departments;

    @ApiModelProperty("所属部门详情")
    private List<DepartmentVO> useDepartmentList;

    @ApiModelProperty("所属员工信息")
    private List<WeUserVO> useUserList;

    @ApiModelProperty("员工发布状态成员发表状态。0:待发布 1：已发布 2：已过期 3：不可发布")
    private Integer publishStatus;
    @ApiModelProperty("标签信息")
    private List<WeTag> tagList;
    @ApiModelProperty("所属员工信息 全部客户时返回空数组")
    private List<MomentUserVO> userList;

    @ApiModelProperty("可见范围（0：全部客户 1：部分客户）")
    private Integer pushRange;

    public SearchMomentVO(Integer type) {
        this.status = type;
    }

    @Data
    @ApiModel("所属员工信息")
    @NoArgsConstructor
    public static class MomentUserVO {
        @ApiModelProperty("所属员工id")
        private String userId;
        @ApiModelProperty("所属员工姓名")
        private String userName;

        public MomentUserVO(WeUser weUser) {
            this.userId = weUser.getUserId();
            this.userName = weUser.getName();
        }
    }

}
