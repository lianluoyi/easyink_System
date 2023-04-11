package com.easyink.wecom.domain.entity.form;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 表单设置表(WeFormAdvanceSetting)表实体类
 *
 * @author tigger
 * @since 2023-01-09 15:00:47
 */
@Data
@TableName("we_form_advance_setting")
public class WeFormAdvanceSetting {
    /**
     * 关联表单id
     */
    @TableId(type = IdType.INPUT)
    private Long formId;
    /**
     * 截止时间类型(1: 永久有效 2:自定义日期)
     */
    private Integer deadLineType;
    /**
     * 截止时间的自定义时间(deadLineType == 1时才有)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date customDate;
    /**
     * 公众号设置
     */
    private String weChatPublicPlatform;
    /**
     * 提交次数类型(1: 不限 2:每个客户限提交1次)
     */
    private Integer submitCntType;
    /**
     * 提交结果行为类型(1:不跳转 2:跳转结果页面 3:跳转连接)
     */
    private Integer submitActionType;
    /**
     * 提交结果行为详情参数(当 submitActionType不为1的时候有用)
     */
    private String actionInfoParamJson;
    /**
     * 行为通知开关(false:关闭 true:开启)
     */
    private Boolean actionNoteFlag;
    /**
     * 轨迹记录开关(false:关闭 true:开启)
     */
    private Boolean tractRecordFlag;
    /**
     * 客户标签开关(false:关闭 true:开启)
     */
    private Boolean customerLabelFlag;
    /**
     * 客户标签开关设置详情json
     */
    private String labelSettingJson;

}

