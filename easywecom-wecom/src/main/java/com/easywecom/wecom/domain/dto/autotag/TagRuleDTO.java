package com.easywecom.wecom.domain.dto.autotag;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 标签规则DTO
 *
 * @author tigger
 * 2022/2/27 20:01
 **/
@Slf4j
@Data
public class TagRuleDTO extends AbstractTagRuleDTO {

    @ApiModelProperty("规则名称")
    private String ruleName;

    /**
     * 公共参数的标签规则entity转换方法的具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule convertToWeAutoTagRule() {
        if (StringUtils.isBlank(this.ruleName)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeAutoTagRule.WeAutoTagRuleBuilder builder = WeAutoTagRule.builder();
        builder
                .ruleName(this.ruleName);
        return builder.build();
    }

}
