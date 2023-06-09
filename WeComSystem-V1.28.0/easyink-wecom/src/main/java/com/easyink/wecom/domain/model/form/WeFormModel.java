package com.easyink.wecom.domain.model.form;

import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.entity.form.WeForm;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单内容（传给中间页）
 *
 * @author wx
 * 2023/1/13 16:38
 **/
@Data
@NoArgsConstructor
public class WeFormModel {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 表单说明
     */
    private String description;
    /**
     * 提交按钮文本描述
     */
    private String submitText;
    /**
     * 提交按钮颜色
     */
    private String submitColor;

    /**
     * 表单字段列表json
     */
    private String formFieldListJson;

    /**
     * 启用标识(true: 未启用 false:启用)
     */
    private Boolean enableFlag;

    /**
     * 企业id
     */
    private String corpId;
    /**
     * 头图开关(false:关闭 true:开启)
     */
    private Boolean headImageOpenFlag;
    /**
     * 显示排序开关(false:关闭 true:开启)
     */
    private Boolean showSortFlag;
    /**
     * 表单说明开关(false:关闭 true:开启)
     */
    private Boolean descriptionFlag;

    /**
     * 转化为WeFormModel
     *
     * @param weForm    {@link WeForm}
     * @return WeFormModel
     */
    public static WeFormModel convert2WeFormModel(WeForm weForm) {
        if (weForm == null) {
            return null;
        }
        WeFormModel weFormModel = new WeFormModel();
        BeanUtils.copyPropertiesASM(weForm, weFormModel);
        return weFormModel;
    }


}
