package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.WeCorpUpdateId;

/**
 * ClassName： UpdateIDSecurityService
 *
 * @author wx
 * @date 2022/8/19 9:31
 */
public interface UpdateIDSecurityService extends IService<WeCorpUpdateId> {

    /**
     * 企业id安全性升级入口
     *
     * @param enableFullUpdate
     * @param corpId
     */
    void corpIdHandle(Boolean enableFullUpdate, String corpId);

    /**
     * 单独企业处理
     *
     * @param corpId
     * @param agentId
     */
    void singleCorpHandle(String corpId, String agentId);

    /**
     * 移除corpId相关缓存
     *
     * @param corpId
     */
    void removeCorpRedisCache(String corpId);
}
