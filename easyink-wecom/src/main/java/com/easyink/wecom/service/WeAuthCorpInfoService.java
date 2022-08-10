package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.vo.CheckCorpIdVO;

import java.util.List;

/**
 * 类名: WeAuthCorpInfoService
 *
 * @author: 1*+
 * @date: 2021-09-09 9:45
 */
public interface WeAuthCorpInfoService extends IService<WeAuthCorpInfo> {


    /**
     * 企业已授权
     *
     * @param corpId  企业ID
     * @param suiteId 第三方应用ID
     * @return 已授权：true ， 未授权：false
     */
    boolean corpAuthorized(String corpId, String suiteId);

    /**
     * 企业已授权
     *
     * @param corpId 企业ID
     * @return 已授权：true ， 未授权：false
     */
    boolean corpAuthorized(String corpId);

    /**
     * 获取已授权企业列表
     *
     * @param suiteId 第三方应用ID
     * @return List<WeAuthCorpInfo>
     */
    List<WeAuthCorpInfo> listOfAuthorizedCorpInfo(String suiteId);

    /**
     * 获取授权企业
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfo}
     */
    WeAuthCorpInfo getOne(String corpId, String suiteId);

    /**
     * 获取授权企业忽略是否取消
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfo}
     */
    WeAuthCorpInfo getAuthIgnoreCancel(String corpId, String suiteId);

    /**
     * 获取授权企业
     *
     * @param corpId 企业ID
     * @return {@link WeAuthCorpInfo}
     */
    WeAuthCorpInfo getOne(String corpId);

    /**
     * 是否是代开发企业
     *
     * @param corpId 企业ID
     * @return {@link CheckCorpIdVO}
     */
    CheckCorpIdVO isDkCorp(String corpId);

}
