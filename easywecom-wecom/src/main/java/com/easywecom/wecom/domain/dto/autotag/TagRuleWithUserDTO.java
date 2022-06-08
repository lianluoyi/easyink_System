package com.easywecom.wecom.domain.dto.autotag;

import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagUserRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有员工使用范围的标签规则DTO
 *
 * @author tigger
 * 2022/2/27 16:52
 **/
@Slf4j
@Data
public class TagRuleWithUserDTO extends AbstractTagRuleWithUserDTO {

    @ApiModelProperty("员工id列表,不传默认全部")
    private List<String> userIdList;

    /**
     * 公共参数的带员工使用范围的标签规则entity转换方法的具体实现
     *
     * @return
     */
    @Override
    public List<WeAutoTagUserRel> convertToWeAutoTagUserRelList(Long ruleId) {
        if (ruleId == null) {
            log.error("ruleId 为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeAutoTagUserRel> weAutoTagUserRelList = new ArrayList<>();
        for (String userId : this.userIdList) {
            weAutoTagUserRelList.add(new WeAutoTagUserRel(ruleId, userId));
        }
        return weAutoTagUserRelList;
    }

}
