package com.easywecom.wecom.domain.dto.autotag.customer;

import com.easywecom.wecom.convert.autotag.customer.UpdateTagRuleCustomerConvert;
import com.easywecom.wecom.domain.entity.autotag.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改新客标签规则DTO
 *
 * @author tigger
 * 2022/2/28 14:24
 **/
@Data
public class UpdateCustomerTagRuleDTO extends CustomerTagRuleDTO implements UpdateTagRuleCustomerConvert {
    @ApiModelProperty("要修改的规则id")
    private Long id;
    @ApiModelProperty("要删除的场景id列表")
    private List<Long> removeSceneIdList;

    /**
     * 转换为修改对应标签规则entity的具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagRule toWeAutoTagRule() {
        WeAutoTagRule weAutoTagRule = super.convertToWeAutoTagRule();
        weAutoTagRule.setId(this.id);
        return weAutoTagRule;
    }

    /**
     * 转换为修改对应新客标签类型规则生效时间entity的具体实现
     *
     * @param ruleId 规则id
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
     * 转换为修改对应新客标签类型规则场景entity的具体实现
     *
     * @param ruleId 规则id
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
     * 转换为修改对应新客标签类型规则场景与标签关系entity
     *
     * @param customerSceneIdList 新客场景id列表
     * @return
     */
    @Override
    public List<WeAutoTagCustomerSceneTagRel> toWeAutoTagCustomerSceneTagRelList(List<Long> customerSceneIdList, Long ruleId) {
        final int size = this.getCustomerSceneList().size();
        List<WeAutoTagCustomerSceneTagRel> allList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long customerSceneId = customerSceneIdList.get(i);
            CustomerSceneDTO customerScene = this.getCustomerSceneList().get(i);
            List<WeAutoTagCustomerSceneTagRel> weAutoTagCustomerSceneTagRelList =
                    super.convertToWeAutoTagCustomerSceneTagRelList(customerScene.getTagIdList());
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
