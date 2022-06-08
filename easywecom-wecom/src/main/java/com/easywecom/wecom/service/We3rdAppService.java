package com.easywecom.wecom.service;

import com.easywecom.wecom.domain.vo.WePreAuthCodeVO;
import com.easywecom.wecom.domain.vo.WePreLoginParamVO;
import com.easywecom.wecom.domain.vo.WeServerTypeVO;

import java.util.Map;

/**
 * 类名: We3rdAppService
 *
 * @author: 1*+
 * @date: 2021-09-08 16:37
 */
public interface We3rdAppService {

    /**
     * 获取预授权码
     *
     * @return WePreAuthCodeVO
     */
    WePreAuthCodeVO getPreAuthCode();

    /**
     * 获取预登录参数
     *
     * @return WePreLoginParamVO
     */
    WePreLoginParamVO getPreLoginParam();

    /**
     * 处理永久授权码
     *
     * @param authCode 临时授权码
     * @param suiteId  第三方应用的SuiteId
     */
    void handlePermanentCode(String authCode, String suiteId);

    /**
     * 取消授权
     *
     * @param authCorpId 授权方企业的corpid
     * @param suiteId    第三方应用的SuiteId
     */
    void cancelAuth(String authCorpId, String suiteId);

    /**
     * 获取服务器类型
     *
     * @return WeServerTypeVO
     */
    WeServerTypeVO getServerType();

    /**
     * 获取企业管理员列表
     *
     * @param corpId 企业ID
     * @param suiteId 应用suiteId
     * @return 管理员
     */
    Map<String, Integer> getAdminList(String corpId,String suiteId);

    /**
     * 获取企业管理员列表
     *
     * @param corpId 企业ID
     * @return 管理员
     */
    Map<String, Integer> getAdminList(String corpId);

    /**
     * 处理永久授权码
     *
     * @param authCode 临时授权码
     * @param suiteId  第三方应用的SuiteId
     */
    void resetPermanentCode(String authCode, String suiteId);
}
