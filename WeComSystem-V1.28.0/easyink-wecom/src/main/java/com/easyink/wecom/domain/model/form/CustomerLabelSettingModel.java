package com.easyink.wecom.domain.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 客户标签设置model
 *
 * @author tigger
 * 2023/1/10 10:39
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLabelSettingModel {


    /**
     * 点击表单添加标签列表
     */
    private List<String> clickLabelIdList;
    /**
     * 提交表单添加标签列表
     */
    private List<String> submitLabelIdList;

}
