package com.easyink.wecom.service.impl.form;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.caculate.form.AbstractCustomerFeedbackCalculate;
import com.easyink.wecom.domain.entity.form.WeFormCustomerFeedback;
import com.easyink.wecom.domain.enums.form.FormComponentType;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.form.WeFormCustomerFeedbackMapper;
import com.easyink.wecom.service.form.WeFormCustomerFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户好评评价表(WeFormCustomerFeedback)表服务实现类
 *
 * @author tigger
 * @since 2023-01-13 16:10:15
 */
@Slf4j
@Service("weFormCustomerFeedbackService")
public class WeFormCustomerFeedbackServiceImpl extends ServiceImpl<WeFormCustomerFeedbackMapper, WeFormCustomerFeedback> implements WeFormCustomerFeedbackService {


    @Override
    public void saveFeedback(Integer formId, String customerId, String userId, FormComponentType componentType, Integer componentValue) {
        this.saveFeedback(formId, customerId, userId, componentType, componentValue, LoginTokenService.getLoginUser().getCorpId());
    }

    @Override
    public void saveFeedback(Integer formId, String customerId, String userId, FormComponentType componentType, Integer componentValue, String corpId) {
        if (formId == null || StringUtils.isAnyBlank(customerId, userId, corpId) || componentType == null || componentValue == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 判断是否评分组件
        Boolean gradeComponent = FormComponentType.isGradeComponent(componentType.getCode());
        if (!gradeComponent) {
            log.warn("[新增客户评分] 非评分组件调用新增客户好评不执行 formId: {}, customerId: {}, userId: {}, componentType: {}, componentValue: {}, corpId: {}",
                    formId, customerId, userId, componentType, componentValue, corpId
            );
            return;
        }
        WeFormCustomerFeedback weFormCustomerFeedback = new WeFormCustomerFeedback();
        weFormCustomerFeedback.setFormId(formId);
        weFormCustomerFeedback.setCustomerId(customerId);
        weFormCustomerFeedback.setUserId(userId);
        weFormCustomerFeedback.setCorpId(corpId);
        // 设置评分
        weFormCustomerFeedback.setScore(AbstractCustomerFeedbackCalculate.calculate(componentType, componentValue));

        this.save(weFormCustomerFeedback);

    }

    @Override
    public void batchAddFeedback(Integer formId, String customerId, String userId, List<Integer> scoreValueList, List<Integer> npsValueList, String corpId) {
        if (formId == null || StringUtils.isAnyBlank(customerId, userId, corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        if (CollectionUtils.isEmpty(scoreValueList) && CollectionUtils.isEmpty(npsValueList)) {
            return;
        }
        List<WeFormCustomerFeedback> formCustomerFeedbacks = new ArrayList<>();
        // 保存普通评分
        for (Integer value : scoreValueList) {
            formCustomerFeedbacks.add(WeFormCustomerFeedback.builder()
                    .formId(formId)
                    .customerId(customerId)
                    .userId(userId)
                    .corpId(corpId)
                    .score(AbstractCustomerFeedbackCalculate.calculate(FormComponentType.SCORE_COMPONENT, value))
                    .build());
        }
        // 保存NPS评分
        for (Integer value : npsValueList) {
            formCustomerFeedbacks.add(WeFormCustomerFeedback.builder()
                    .formId(formId)
                    .customerId(customerId)
                    .userId(userId)
                    .corpId(corpId)
                    .score(AbstractCustomerFeedbackCalculate.calculate(FormComponentType.NPS_COMPONENT, value))
                    .build());
        }
        this.saveBatch(formCustomerFeedbacks);
    }
}

