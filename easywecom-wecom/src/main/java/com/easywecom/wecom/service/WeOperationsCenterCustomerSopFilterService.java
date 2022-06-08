package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;

import java.util.List;

/**
 * 类名： 客户SOP筛选条件接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterCustomerSopFilterService extends IService<WeOperationsCenterCustomerSopFilterEntity> {
    /**
     * 根据corpId和sopIdList删除数据
     *
     * @param corpId    企业ID
     * @param sopIdList sopIdList
     */
    void delByCorpIdAndSopIdList(String corpId, List<Long> sopIdList);

    /**
     * 获得客户sop筛选条件
     *
     * @param corpId 企业id
     * @param sopId  id
     * @return {@link WeOperationsCenterCustomerSopFilterEntity}
     */
    WeOperationsCenterCustomerSopFilterEntity getCustomerSopFilter(String corpId, Long sopId);
}

