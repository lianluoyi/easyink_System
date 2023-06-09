package com.easyink.wecom.domain.model.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.form.ActionInfoParamDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 跳转结束页行为param
 *
 * @author tigger
 * 2023/1/9 10:36
 **/
@Data
public class JumpResultPageActionParam {

    /**
     * 图标url
     */
    private String iconUrl;

    /**
     * 文案
     */
    private String document;

    public JumpResultPageActionParam(ActionInfoParamDTO param) {
        this.iconUrl = param.getIconUrl();
        this.document = param.getDocument();
    }

    /**
     * 校验
     */
    public void valid() {
        if (StringUtils.isAnyBlank(iconUrl, document)) {
            throw new CustomException(ResultTip.TIP_FORM_JUMP_RESULT_PARAMS_ERROR);
        }
    }
}
