package com.easywecom.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeChatContactMapping;
import com.easywecom.common.core.domain.conversation.ChatInfoVO;

import java.util.List;

/**
 * 聊天关系映射Service接口
 *
 * @author admin
 * @date 2020-12-27
 */
public interface WeChatContactMappingService extends IService<WeChatContactMapping> {
    /**
     * 查询聊天关系映射
     *
     * @param id 聊天关系映射ID
     * @return 聊天关系映射
     */
    WeChatContactMapping selectWeChatContactMappingById(Long id);

    /**
     * 查询聊天关系映射列表
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 聊天关系映射集合
     */
    List<WeChatContactMapping> selectWeChatContactMappingList(WeChatContactMapping weChatContactMapping);

    /**
     * 新增聊天关系映射
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 结果
     */
    int insertWeChatContactMapping(WeChatContactMapping weChatContactMapping);

    /**
     * 修改聊天关系映射
     *
     * @param weChatContactMapping 聊天关系映射
     * @return 结果
     */
    int updateWeChatContactMapping(WeChatContactMapping weChatContactMapping);

    /**
     * 批量删除聊天关系映射
     *
     * @param ids 需要删除的聊天关系映射ID
     * @return 结果
     */
    int deleteWeChatContactMappingByIds(Long[] ids);

    /**
     * 删除聊天关系映射信息
     *
     * @param id 聊天关系映射ID
     * @return 结果
     */
    int deleteWeChatContactMappingById(Long id);

    /**
     * 保存
     *
     * @param corpId 企业id
     * @param query 会话列表
     * @return 会话联系人列表结果
     */
    List<JSONObject> saveWeChatContactMapping(String corpId, List<JSONObject> query);

    List<ChatInfoVO> saveWeChatContactMapping1(String corpId, List<ChatInfoVO> chatDataList);
}
