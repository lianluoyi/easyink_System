package com.easyink.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.wecom.domain.WeSensitiveAct;
import com.easyink.wecom.domain.WeSensitiveActHit;
import com.easyink.common.core.domain.conversation.ChatInfoVO;

import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @date 2021/1/13 8:46
 */
public interface WeSensitiveActHitService extends IService<WeSensitiveActHit> {
    /**
     * 查询敏感行为记录
     *
     * @param id 敏感行为记录ID
     * @return 敏感行为
     */
    WeSensitiveActHit selectWeSensitiveActHitById(Long id);

    /**
     * 查询敏感行为记录列表
     *
     * @param rootEntity 基础实体
     * @return 敏感行为记录
     */
    List<WeSensitiveActHit> selectWeSensitiveActHitList(RootEntity rootEntity);

    /**
     * 新增敏感行为记录
     *
     * @param weSensitiveActHit 敏感行为记录
     * @return 结果
     */
    boolean insertWeSensitiveActHit(WeSensitiveActHit weSensitiveActHit);

    /**
     * 过滤敏感行为，并保存
     *
     * @param chatDataList
     */
    void hitWeSensitiveAct(String corpId, List<JSONObject> chatDataList);

    /**
     * 获取敏感行为类型
     *
     * @param msgType 消息类型
     * @param corpId 公司id
     * @return
     */
    WeSensitiveAct getSensitiveActType(String msgType, String corpId);

    /**
     * 设置敏感行为操作人、操作对象信息
     *
     * @param weSensitiveActHit
     * @return
     */
    void setUserOrCustomerInfo(WeSensitiveActHit weSensitiveActHit);

    void hitWeSensitiveAct1(String corpId, List<ChatInfoVO> chatDataList);

    /**
     * 更新敏感行为信息
     *
     */
    void updateHistorySensitive();
}
