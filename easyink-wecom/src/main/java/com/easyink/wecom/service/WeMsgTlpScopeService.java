package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeMsgTlpScope;

import java.util.List;

/**
 * 模板使用人员范围Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
public interface WeMsgTlpScopeService extends IService<WeMsgTlpScope> {

    /**
     * 插入好友欢迎语使用范围
     *
     * @param defaultMsgId 默认欢迎语id
     * @param useUserIds   员工ids
     */
    void saveScopeBatch(Long defaultMsgId, List<String> useUserIds);

    /**
     * 修改欢迎语员工使用范围
     *
     * @param defaultMsgId 默认欢迎语id
     * @param useUserIds   员工ids
     */
    void updateScope(Long defaultMsgId, List<String> useUserIds);

    /**
     * 批量更新
     *
     * @param scopeBatchList
     */
    void batchSaveOrUpdate(List<WeMsgTlpScope> scopeBatchList);

    /**
     * 删除不存在集合中的的记录
     *
     * @param defaultMsgId
     * @param useUserIds
     */
    void deleteNotInUserIds(Long defaultMsgId, List<String> useUserIds);
}
