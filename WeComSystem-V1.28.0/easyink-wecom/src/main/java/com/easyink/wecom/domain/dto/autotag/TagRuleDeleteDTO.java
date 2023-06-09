package com.easyink.wecom.domain.dto.autotag;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 标签规则删除DTO
 *
 * @author tigger
 * 2022/2/28 15:14
 **/
@Data
public class TagRuleDeleteDTO {
    @NotEmpty(message = "请至少选择一个删除的目标")
    private List<Long> idList;
}
