package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossSwitchVO;

import java.util.List;

/**
 * 企业id相关配置Service接口
 *
 * @author admin
 * @date 2020-08-24
 */
public interface WeCorpAccountService extends IService<WeCorpAccount> {


    /**
     * 修改企业配置
     *
     * @param wxCorpAccount 企业配置
     * @param loginUser     当前登录人
     */
    void updateWeCorpAccount(WeCorpAccount wxCorpAccount, LoginUser loginUser);

    /**
     * 修改企业配置
     *
     * @param wxCorpAccount 企业配置
     * @param corpId        企业ID
     */
    void updateWeCorpAccount(WeCorpAccount wxCorpAccount, String corpId);


    /**
     * 获取有效的企业配置
     *
     * @return WeCorpAccount
     */
    WeCorpAccount findValidWeCorpAccount();

    /**
     * 获取有效的企业配置
     *
     * @param corpId 企业ID
     * @return WeCorpAccount
     */
    WeCorpAccount findValidWeCorpAccount(String corpId);


    /**
     * 启用有效的企业微信账号
     *
     * @param corpId
     */
    int startVailWeCorpAccount(String corpId);

    /**
     * 客户流失通知开关
     *
     * @param status 开关状态
     * @return
     */
    void startCustomerChurnNoticeSwitch(String corpId, String status);

    /**
     * 客户流失标签开关
     *
     * @param status 开关状态
     * @return
     */
    void startCustomerLossTagSwitch(String corpId, String status);

    /**
     * 客户流失通知开关查询
     */
    String getCustomerChurnNoticeSwitch(String corpId);

    /**
     * 客户流失开关查询
     * @param corpId 企业ID
     * @return
     */
    CustomerLossSwitchVO getCustomerLossSwitch(String corpId);

    /**
     * 企业是否已配置内部应用
     *
     * @param corpId 授权企业ID
     * @return
     */
    WeCorpAccount internalAppConfigured(String corpId);

    /**
     * 获取授权企业配置内部应用配置
     *
     * @return List<WeCorpAccount>
     */
    List<WeCorpAccount> listOfAuthCorpInternalWeCorpAccount();


    /**
     * 初始化企业配置
     *
     * @param corpId   企业ID
     * @param corpName 企业名
     * @param isCustomApp 是否代开发
     */
    void initWeCorpAccount(String corpId, String corpName, boolean isCustomApp);

    /**
     * 根据公司实体 获取公司ID
     *
     * @param weCorpAccount 公司实体
     * @return 公司ID, 如果公司实体不存在或者实体里corpId为空, 则返回空字符串
     */
    String getCorpId(WeCorpAccount weCorpAccount);

    /**
     * 保存企业配置
     *
     * @param weCorpAccount
     * @param loginUser
     */
    void saveCorpConfig(WeCorpAccount weCorpAccount, LoginUser loginUser);

    /**
     * 获取公司名称
     *
     * @param corpId 公司ID
     * @return 公司名称
     */
    String getCorpName(String corpId);

    /**
     * 删除企业配置
     *
     * @param corpId 企业ID
     */
    void delWeCorpAccount(String corpId);
}
