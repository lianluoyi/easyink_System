package com.easyink.wecom.domain.dto.welcomemsg;

import com.easyink.wecom.domain.WeMsgTlp;
import com.easyink.wecom.domain.WeMsgTlpSpecialRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 欢迎语添加dto
 *
 * @author tigger
 * 2022/1/4 16:52
 **/
@Data
public class WelComeMsgAddDTO {

    @ApiModelProperty("默认欢迎语消息模板")
    private WeMsgTlp weMsgTlp;

    @ApiModelProperty("特殊规则欢迎语列表")
    private List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules;

    @ApiModelProperty("欢迎语使用人ids")
    private List<String> useUserIds;

}
