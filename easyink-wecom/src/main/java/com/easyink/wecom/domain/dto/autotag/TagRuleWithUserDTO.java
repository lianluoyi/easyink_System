package com.easyink.wecom.domain.dto.autotag;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @ApiModelProperty("部门id列表,不传默认无")
    private List<String> departmentIdList;
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

        this.userIdList = Optional.ofNullable(this.userIdList).orElseGet(Lists::newArrayList);
        for (String userId : this.userIdList) {
            weAutoTagUserRelList.add(new WeAutoTagUserRel(ruleId, userId, WeConstans.AUTO_TAG_ADD_USER_TYPE));
        }
        this.departmentIdList = Optional.ofNullable(this.departmentIdList).orElseGet(Lists::newArrayList);
        for (String departmentId : this.departmentIdList) {
            weAutoTagUserRelList.add(new WeAutoTagUserRel(ruleId, departmentId, WeConstans.AUTO_TAG_ADD_DEPARTMENT_TYPE));
        }
        return weAutoTagUserRelList;
    }

}
