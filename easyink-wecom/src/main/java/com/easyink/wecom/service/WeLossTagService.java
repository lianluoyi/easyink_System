package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.customerloss.CustomerAddLossTagDTO;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossTagVO;

/**
 * 流失提醒Service接口
 *
 * @author lichaoyu
 * @date 2023/3/24 11:47
 */
public interface WeLossTagService {

    /**
     * 新增流失客户标签
     *
     * @param customerAddLossTagDTO 流失提醒和打标签类
     */
    void insertWeLossTag(CustomerAddLossTagDTO customerAddLossTagDTO);

    /**
     * 查询客户流失标签
     *
     * @return 结果
     */
    CustomerLossTagVO selectLossWeTag(String corpId);
}
