package com.easyink.wecom.service;

import java.util.Date;
import java.util.Map;

/**
 * 客户补偿服务接口
 * 用于处理5月1日至5月5日期间的新增和流失客户补偿逻辑
 *
 * @author 系统
 * @date 2024-05-01
 */
public interface CustomerCompensationService {

    /**
     * 补偿5月1日至5月5日期间的新增和流失客户
     *
     * @param corpId 企业ID
     * @return 处理结果
     */
    Map<String, Object> compensateCustomers(String corpId, Date beginTime, Date endTime);

    /**
     * 重新生成统计数据（需要调用定时任务）
     *
     * @param corpId 企业ID
     * @return 处理结果
     */
    Map<String, Object> regenerateStatistics(String corpId, Date beginTime, Date endTime);
} 