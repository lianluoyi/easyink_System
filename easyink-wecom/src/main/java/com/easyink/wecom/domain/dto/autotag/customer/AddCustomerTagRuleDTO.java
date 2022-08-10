package com.easyink.wecom.domain.dto.autotag.customer;

import com.easyink.common.enums.autotag.AutoTagLabelTypeEnum;
import com.easyink.wecom.convert.autotag.customer.AddTagRuleCustomerConvert;
import com.easyink.wecom.domain.entity.autotag.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新增新客标签规则DTO
 *
 * @author tigger
 * 2022/2/28 13:33
 **/
@Slf4j
@Data
public class AddCustomerTagRuleDTO extends CustomerTagRuleDTO implements AddTagRuleCustomerConvert {
    @ApiModelProperty(value = "企业id", hidden = true)
    private String corpId;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    /**
     * 转换为新增对应的标签规则entity具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule toWeAutoTagRule() {
        WeAutoTagRule weAutoTagRule = super.convertToWeAutoTagRule();
        weAutoTagRule.setCorpId(this.getCorpId());
        weAutoTagRule.setLabelType(AutoTagLabelTypeEnum.CUSTOMER.getType());
        weAutoTagRule.setCreateTime(new Date());
        weAutoTagRule.setCreateBy(this.getCreateBy());
        return weAutoTagRule;
    }

    /**
     * 转换为新政对应新客标签类型规则生效时间entity的具体实现
     *
     * @param ruleId 标签规则id
     * @return
     */
    @Override
    public WeAutoTagCustomerRuleEffectTime toWeAutoTagCustomerRuleEffectTime(Long ruleId) {
        WeAutoTagCustomerRuleEffectTime weAutoTagCustomerRuleEffectTime = super.convertToWeAutoTagCustomerRuleEffectTime();
        if (weAutoTagCustomerRuleEffectTime == null) {
            return null;
        }
        weAutoTagCustomerRuleEffectTime.setRuleId(ruleId);
        return weAutoTagCustomerRuleEffectTime;
    }

    /**
     * 转换为新增对应新客标签类型规则场景entity的具体实现
     *
     * @param ruleId 标签规则id
     * @return
     */
    @Override
    public List<WeAutoTagCustomerScene> toWeAutoTagCustomerSceneList(Long ruleId, String corpId) {
        List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList = super.convertToWeAutoTagCustomerSceneList();
        for (WeAutoTagCustomerScene weAutoTagCustomerScene : weAutoTagCustomerSceneList) {
            weAutoTagCustomerScene.setRuleId(ruleId);
            weAutoTagCustomerScene.setCorpId(corpId);
        }
        return weAutoTagCustomerSceneList;
    }

    /**
     * 转换为新增对应新客标签类型规则场景与标签关系entity的具体实现
     *
     * @param customerSceneIdList 群场景id列表
     * @return
     */
    @Override
    public List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList(List<Long> customerSceneIdList, Long ruleId) {
        int size = customerSceneIdList.size();
        List<WeAutoTagCustomerSceneTagRel> allList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long customerSceneId = customerSceneIdList.get(i);
            CustomerSceneDTO customerScene = this.getCustomerSceneList().get(i);
            List<WeAutoTagCustomerSceneTagRel> weAutoTagCustomerSceneTagRelList = super.convertToWeAutoTagCustomerSceneTagRelList(customerScene.getTagIdList());
            for (WeAutoTagCustomerSceneTagRel weAutoTagCustomerSceneTagRel : weAutoTagCustomerSceneTagRelList) {
                weAutoTagCustomerSceneTagRel.setCustomerSceneId(customerSceneId);
                weAutoTagCustomerSceneTagRel.setRuleId(ruleId);
            }
            allList.addAll(weAutoTagCustomerSceneTagRelList);
        }
        return allList;
    }

    /**
     * 转换为员工使用范围entity的接口具体实现
     *
     * @param ruleId 标签规则id
     * @return
     */
    @Override
    public List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId) {
        return super.toWeAutoTagUserRel(ruleId);
    }

}
