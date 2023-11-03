package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeMsgTlp;
import com.easyink.wecom.domain.WeMsgTlpFilterRule;

import java.util.List;

/**
 * 欢迎语筛选条件Service接口
 *
 * @author lichaoyu
 * @date 2023/10/25 15:41
 */
public interface WeMsgTlpFilterRuleService extends IService<WeMsgTlpFilterRule> {

    /**
     * 批量保存欢迎语客户筛选条件
     *
     * @param msgTlpId            欢迎语模板ID
     * @param weMsgTlpFilterRules 客户筛选条件
     * @return 结果
     */
    boolean saveBatchFilterRules(Long msgTlpId, List<WeMsgTlpFilterRule> weMsgTlpFilterRules);

    /**
     * 是否发送欢迎语
     *
     * @param weMsgTlp {@link WeMsgTlp}
     * @param addWay   客户添加来源
     * @param gender   客户性别
     * @return true 是， false 否
     */
    boolean isSendMsgTlp(WeMsgTlp weMsgTlp, String addWay, Integer gender);

}
