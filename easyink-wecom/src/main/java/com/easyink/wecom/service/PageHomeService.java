package com.easyink.wecom.service;

import com.easyink.wecom.domain.dto.WePageStaticDataDTO;

/**
 * 类名： PageHomeService
 *
 * @author 佚名
 * @date 2021/9/1 18:57
 */
public interface PageHomeService {
    /**
     * 刷新首页数据
     *
     * @param corpId 授权企业id
     */
    void reloadPageHome(String corpId);

    /**
     * 统计首页企业数据概览到redis缓存中
     *
     * @param corpId 授权企业id
     */
    void getCorpBasicData(String corpId);

    /**
     * 获取、更新用户信息
     *
     * @param corpId 企业id
     */
    void getUserData(String corpId);

    /**
     * 获取、更新客户信息
     *
     * @param corpId 企业id
     */
    void getCustomerData(String corpId);

    /**
     * 获取、更新群聊信息
     *
     * @param corpId 企业id
     */
    void getGroupData(String corpId);

    /**
     * 初始化公司实时数据
     *
     * @param corpId 授权企业id
     * @return 首页基础数据DTO
     */
    WePageStaticDataDTO initCorpRealTimeData(String corpId);
}