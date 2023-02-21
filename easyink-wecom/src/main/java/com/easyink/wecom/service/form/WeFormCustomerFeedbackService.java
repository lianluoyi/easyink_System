package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.form.WeFormCustomerFeedback;
import com.easyink.wecom.domain.enums.form.FormComponentType;

import java.util.List;

/**
 * 客户好评评价表(WeFormCustomerFeedback)表服务接口
 *
 * @author tigger
 * @since 2023-01-13 16:10:14
 */
public interface WeFormCustomerFeedbackService extends IService<WeFormCustomerFeedback> {

    /**
     * 保存客户评分
     *
     * @param formId         表单id
     * @param customerId     客户id
     * @param userId         员工id
     * @param componentType  组件类型
     * @param componentValue 组件值
     * @return 推广详情VO
     */
    void saveFeedback(Integer formId, String customerId, String userId, FormComponentType componentType, Integer componentValue);


    /**
     * 保存客户评分
     *
     * @param formId         表单id
     * @param customerId     客户id
     * @param userId         员工id
     * @param componentType  组件类型
     * @param componentValue 组件值
     * @param corId          企业id
     * @return 推广详情VO
     */
    void saveFeedback(Integer formId, String customerId, String userId, FormComponentType componentType, Integer componentValue, String corId);

    /**
     * 批量保存客户评分
     *
     * @param formId         表单id
     * @param customerId     客户id
     * @param userId         员工id
     * @param scoreValueList {@link FormComponentType#SCORE_COMPONENT} value
     * @param npsValueList   {@link FormComponentType#NPS_COMPONENT} value
     * @param corpId         企业id
     */
    void batchAddFeedback(Integer formId, String customerId, String userId, List<Integer> scoreValueList, List<Integer> npsValueList, String corpId);

}

