package com.easyink.wecom.domain.model.form;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.form.ActionInfoParamDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 跳转链接参数
 *
 * @author tigger
 * 2023/1/9 14:25
 **/
@Data
public class JumpLinkActionParam {

    /**
     * 链接URL
     */
    private String url;

    public JumpLinkActionParam(ActionInfoParamDTO param) {
        this.url = param.getUrl();
    }

    /**
     * 校验
     */
    public void valid() {
        if (StringUtils.isBlank(this.url)) {
            throw new CustomException(ResultTip.TIP_FORM_JUMP_LINK_PARAMS_ERROR);
        }
    }
}
