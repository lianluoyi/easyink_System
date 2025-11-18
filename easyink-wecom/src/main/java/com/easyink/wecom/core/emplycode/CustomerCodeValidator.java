package com.easyink.wecom.core.emplycode;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.emplecode.TagSelectScopeDTO;
import org.apache.commons.lang3.StringUtils;

/**
 * 客户专属活码校验器
 *
 * @author tigger
 * 2025/4/29 14:34
 **/
public class CustomerCodeValidator {

    /**
     * 校验专属活码选标签DTO参数
     *
     * @param tagSelectScopeDTO DTO参数
     */
    public static void validateSelectTag(TagSelectScopeDTO tagSelectScopeDTO) {
        if (tagSelectScopeDTO == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        if (StringUtils.isBlank(tagSelectScopeDTO.getOriginEmpleId())) {
            throw new CustomException("所属活码不能为空");
        }


    }

    /**
     * 校验专属活码选标签详情
     *
     * @param empleCodeId 活码id
     */
    public static void validateSelectTagDetail(String empleCodeId) {
        if (StringUtils.isBlank(empleCodeId)) {
            throw new CustomException("活码不能为空");
        }
    }
}
