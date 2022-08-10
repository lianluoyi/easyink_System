package com.easyink.wecom.domain.dto.autotag.customer;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.convert.autotag.TagRuleUserRelConvert;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerRuleEffectTime;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerScene;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagCustomerSceneTagRel;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagUserRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.easyink.common.constant.WeConstans.AUTO_TAG_CUSTOMER_SCENE_NUM_LIMIT;
import static com.easyink.common.constant.WeConstans.AUTO_TAG_CUSTOMER_SCENE_TAG_NUM_LIMIT;

/**
 * 新客标签规则DTO
 *
 * @author tigger
 * 2022/2/28 13:36
 **/
@Slf4j
@Data
public class CustomerTagRuleDTO extends AbstractCustomerTagRuleDTO implements TagRuleUserRelConvert {

    @ApiModelProperty("生效开始时间")
    private String effectBeginTime;
    @ApiModelProperty("生效结束时间")
    private String effectEndTime;
    @ApiModelProperty("客户场景列表")
    List<CustomerSceneDTO> customerSceneList;

    /**
     * 公共参数的新客标签类型规则生效时间entity转换方法具体实现
     *
     * @return
     */
    @Override
    public WeAutoTagCustomerRuleEffectTime convertToWeAutoTagCustomerRuleEffectTime() {
        if (StringUtils.isBlank(this.effectBeginTime) || StringUtils.isBlank(this.effectEndTime)) {
            return null;
        }
        WeAutoTagCustomerRuleEffectTime.WeAutoTagCustomerRuleEffectTimeBuilder builder = WeAutoTagCustomerRuleEffectTime.builder();
        builder.effectBeginTime(this.effectBeginTime).effectEndTime(this.effectEndTime);
        return builder.build();
    }

    /**
     * 公共参数的新客标签类型规则场景entity转换方法具体实现
     *
     * @return
     */
    @Override
    public List<WeAutoTagCustomerScene> convertToWeAutoTagCustomerSceneList() {
        if (CollectionUtils.isEmpty(customerSceneList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_NOT_NULL);
        }
        if (customerSceneList.size() > AUTO_TAG_CUSTOMER_SCENE_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_GROUP_SCENE_NUM_LIMIT);
        }
        List<WeAutoTagCustomerScene> weAutoTagCustomerSceneList = new ArrayList<>();
        for (CustomerSceneDTO customerScene : customerSceneList) {
            weAutoTagCustomerSceneList.add(
                    new WeAutoTagCustomerScene(customerScene.getId(), customerScene.getLoopPoint(),
                            customerScene.getLoopBeginTime(), customerScene.getLoopEndTime(), customerScene.getSceneType()));
        }
        return weAutoTagCustomerSceneList;
    }

    /**
     * 公共参数的新客标签类型规则场景与标签关系entity转换方法
     *
     * @param tagIdList 标签id列表
     * @return
     */
    @Override
    public List<WeAutoTagCustomerSceneTagRel> convertToWeAutoTagCustomerSceneTagRelList(List<String> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_TAG_NOT_NULL);
        }
        if (tagIdList.size() > AUTO_TAG_CUSTOMER_SCENE_TAG_NUM_LIMIT) {
            throw new CustomException(ResultTip.TIP_AUTO_TAG_SCENE_TAG_NUM_LIMIT);
        }
        List<WeAutoTagCustomerSceneTagRel> weAutoTagCustomerSceneTagRelList = new ArrayList<>();
        for (String tagId : tagIdList) {
            weAutoTagCustomerSceneTagRelList.add(new WeAutoTagCustomerSceneTagRel(tagId));
        }
        return weAutoTagCustomerSceneTagRelList;
    }

    /**
     * 转换为员工使用范围entity的接口具体实现
     *
     * @param ruleId 标签规则id
     * @return
     */
    @Override
    public List<WeAutoTagUserRel> toWeAutoTagUserRel(Long ruleId) {

        return super.convertToWeAutoTagUserRelList(ruleId);
    }
}
