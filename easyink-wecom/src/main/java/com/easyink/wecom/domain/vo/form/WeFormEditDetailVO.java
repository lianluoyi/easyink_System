package com.easyink.wecom.domain.vo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单编辑回显详情
 *
 * @author tigger
 * 2023/1/15 9:31
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeFormEditDetailVO {
    /**
     * 表单VO
     */
    private WeFormVO formVO;
    /**
     * 表单设置VO
     */
    private WeFormSettingVO formSettingVO;

}
