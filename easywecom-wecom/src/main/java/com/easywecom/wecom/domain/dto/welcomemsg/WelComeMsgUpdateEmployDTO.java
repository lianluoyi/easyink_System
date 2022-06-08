package com.easywecom.wecom.domain.dto.welcomemsg;

import com.easywecom.wecom.domain.WeMsgTlpSpecialRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 好友欢迎语修改DTO
 *
 * @author tigger
 * 2022/1/7 11:41
 **/
@Data
public class WelComeMsgUpdateEmployDTO extends WelComeMsgUpdateDTO{

    @ApiModelProperty(value = "是否存在有特殊时段欢迎语(存在则有关联rule_id) 0:否 1:是", hidden = true)
    private Boolean existSpecialFlag;

    @ApiModelProperty("特殊规则欢迎语列表")
    private List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules;
    @ApiModelProperty("需要删除的特殊时段欢迎语ids")
    private List<Long> removeSpecialRuleIds;




    @ApiModelProperty("欢迎语使用人ids")
    @NotEmpty(message = "请选择至少一个使用人员")
    private List<String> useUserIds;
}
