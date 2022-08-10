package com.easyink.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeSensitive;
import com.easyink.wecom.domain.query.WeSensitiveHitQuery;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 敏感词设置Service接口
 *
 * @author admin
 * @date 2020-12-29
 */
public interface WeSensitiveService {
    /**
     * 查询敏感词设置
     *
     * @param id 敏感词设置ID
     * @return 敏感词设置
     */
    WeSensitive selectWeSensitiveById(Long id);

    /**
     * 查询敏感词设置列表
     *
     * @param weSensitive 敏感词设置
     * @return 敏感词设置集合
     */
    List<WeSensitive> selectWeSensitiveList(WeSensitive weSensitive);

    /**
     * 新增敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    int insertWeSensitive(WeSensitive weSensitive);

    /**
     * 修改敏感词设置
     *
     * @param weSensitive 敏感词设置
     * @return 结果
     */
    int updateWeSensitive(WeSensitive weSensitive);

    /**
     * 批量删除敏感词设置
     *
     * @param ids 需要删除的敏感词设置ID
     * @return 结果
     */
    int deleteWeSensitiveByIds(Long[] ids);

    /**
     * 批量删除敏感词设置信息
     *
     * @param ids 敏感词设置ID
     * @return 结果
     */
    int destroyWeSensitiveByIds(Long[] ids);

    /**
     * 敏感词命中
     *
     * @param corpId
     * @param entityList
     */
    void hitSensitive(String corpId, List<JSONObject> entityList);

    PageInfo<JSONObject> getHitSensitiveList(WeSensitiveHitQuery weSensitiveHitQuery, LoginUser loginUser);

    void hitSensitive1(String corpId, List<ChatInfoVO> elasticSearchEntities);

}
