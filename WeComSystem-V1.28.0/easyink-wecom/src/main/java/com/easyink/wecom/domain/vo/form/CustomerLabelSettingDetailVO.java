package com.easyink.wecom.domain.vo.form;

import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class CustomerLabelSettingDetailVO {


    /**
     * 点击表单设置的标签详情VO列表
     */
    private List<TagInfoVO> clickLabelIdList;
    /**
     * 提交表单设置的标签详情VO列表
     */
    private List<TagInfoVO> submitLabelIdList;


    /**
     * 空VO
     *
     * @return
     */
    public static CustomerLabelSettingDetailVO empty() {
        return new CustomerLabelSettingDetailVO(new ArrayList<>(), new ArrayList<>());
    }
}
