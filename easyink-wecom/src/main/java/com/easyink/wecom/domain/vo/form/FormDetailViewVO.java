package com.easyink.wecom.domain.vo.form;

import com.easyink.wecom.domain.enums.form.DeadLineType;
import com.easyink.wecom.domain.enums.form.SubmitActionType;
import com.easyink.wecom.domain.enums.form.SubmitCntType;
import com.easyink.wecom.domain.model.form.JumpLinkActionParam;
import com.easyink.wecom.domain.model.form.JumpResultPageActionParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 表单详情视图VO
 *
 * @author tigger
 * 2023/1/11 13:58
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDetailViewVO {

    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 提交文本内容
     */
    private String submitText;
    /**
     * 提交按钮颜色
     */
    private String submitColor;

    /**
     * 头图开关
     */
    private Boolean headImageOpenFlag;

    /**
     * 显示排序开关
     */
    private Boolean showSortFlag;
    /**
     * 表单说明
     */
    private String description;
    private Boolean descriptionFlag;

    /**
     * 表单字段列表json
     */
    private String formFieldListJson;

    /**
     * 公众号名称
     */
    private String weChatPublicPlatformNickName;
    /**
     * 公众号头像
     */
    private String weChatPublicPlatformHeadImg;


    /**
     * 截止时间类型dict {@link DeadLineType}
     */
    private Integer deadLineType;
    private String deadLineTypeDict;
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date customDate;

    /**
     * 提交次数类型dict {@link SubmitCntType}
     */
    private Integer submitCntType;
    private String submitCntTypeDict;
    /**
     * 提交结果行为详情参数
     * {@link SubmitActionType}
     * {@link JumpLinkActionParam } {@link JumpResultPageActionParam}
     */
    private String actionInfoParamJson;

    /**
     * 提交结果类型 {@link SubmitActionType}
     */
    private Integer submitActionType;
    private String submitActionTypeDict;

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
     * 推送内容开关
     */
    private Boolean pushContentFlag;

    /**
     * 客户自定义属性id列表json
     */
    @JsonIgnore
    private String customerPropertyIdJson;

    /**
     * 超时配置
     */
    private Integer timeoutHours;


    /**
     * 客户标签开关设置详情VO
     */
    @JsonIgnore
    private String labelSettingJson;
    /**
     * 标签设置VO
     */
    private CustomerLabelSettingDetailVO labelSetting;
    /**
     * 客户自定义属性VO列表
     */
    private List<CustomerPropertySettingVO> customerPropertyIdVOList;




    /**
     * 填充字典值
     *
     * @return FormDetailViewVO
     */
    public FormDetailViewVO fillDict() {
        this.deadLineTypeDict = DeadLineType.validCode(this.deadLineType).getDict();
        this.submitCntTypeDict = SubmitCntType.validCode(this.submitCntType).getDict();
        this.submitActionTypeDict = SubmitActionType.validCode(this.submitActionType).getDict();
        return this;
    }
}
