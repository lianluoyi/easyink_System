package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.Excel;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author 佚名
 * @Description:
 * @date 2021-7-28
 */
@ApiModel("群实体")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_group")
@Slf4j
public class WeGroup extends RootEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("chat_id")
    private String chatId;

    @ApiModelProperty(value = "群名")
    @TableField("group_name")
    @Excel(name = "群昵称",sort = 1)
    private String groupName;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @TableField(exist = false)
    @Excel(name = "群成员",sort = 2)
    private Long memberNum;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", sort = 6, dateFormat = "yyyy/MM/dd")
    private Date createTime;

    @ApiModelProperty(value = "群公告")
    @TableField("notice")
    private String notice;

    @ApiModelProperty(value = "群主userId")
    @TableField("owner")
    private String owner;

    @ApiModelProperty(value = "0 - 正常;1 - 跟进人离职;2 - 离职继承中;3 - 离职继承完成")
    @TableField("status")
    @Excel(name = "客户群状态", sort = 5, readConverterExp="0=正常,1=待继承")
    private Integer status;


    @TableField(exist = false)
    @Excel(name = "群主",sort = 3)
    private String groupLeaderName;


    @TableField(exist = false)
    private String groupLeader;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginTime;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endTime;


    @TableField(exist = false)
    @ApiModelProperty(value = "主部门名")
    private String mainDepartmentName;

    /**
     * 员工id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "员工id")
    private String userIds;

    /**
     * 群头像
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "群头像")
    private String avatar;


    @TableField(exist = false)
    @ApiModelProperty(value = "客户群状态(0：正常，1：待继承)")
    private Integer groupStatus;


    @TableField(exist = false)
    @ApiModelProperty(value = "客户群标签")
    private List<WeGroupTagRelDetail> tagList;

    @TableField(exist = false)
    @ApiModelProperty(value = "过滤的群id列表")
    private List<String> filterChatIds;

    public void setBeginTime(String beginTime) {
        if (StringUtils.isNotBlank(beginTime)) {
            try {
                beginTime = DateUtils.timeFormatTrans(beginTime, DateUtils.YYYYMMDD, DateUtils.YYYY_MM_DD);
            } catch (Exception e) {
                log.error("日期格式转换异常,e :{}" , ExceptionUtils.getStackTrace(e));
            }
        }
        this.beginTime = DateUtils.parseBeginDay(beginTime);
    }
    public void setEndTime(String endTime) {
        if (StringUtils.isNotBlank(endTime)) {
            try {
                endTime = DateUtils.timeFormatTrans(endTime, DateUtils.YYYYMMDD, DateUtils.YYYY_MM_DD);
            } catch (Exception e) {
                log.error("日期格式转换异常,e:{}" , ExceptionUtils.getStackTrace(e));
            }
        }
        this.endTime = DateUtils.parseEndDay(endTime);
    }
}
