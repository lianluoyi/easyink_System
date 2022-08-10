package com.easywecom.wecom.service.impl;

import com.easywecom.common.constant.Constants;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名: WeInitServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-23 14:17
 */
@Slf4j
@Service
public class WeInitServiceImpl implements WeInitService {


    private final WeSensitiveActService weSensitiveActService;
    private final WeCategoryService weCategoryService;
    private final WeUserRoleService weUserRoleService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    private final WeCustomerTransferConfigService weCustomerTransferConfigService;

    @Autowired
    public WeInitServiceImpl(WeSensitiveActService weSensitiveActService, WeCategoryService weCategoryService,
                             WeUserRoleService weUserRoleService,
                             WeCorpAccountService weCorpAccountService,
                             WeCustomerExtendPropertyService weCustomerExtendPropertyService,
                             WeCustomerTransferConfigService weCustomerTransferConfigService) {
        this.weSensitiveActService = weSensitiveActService;
        this.weCategoryService = weCategoryService;
        this.weUserRoleService = weUserRoleService;
        this.weCorpAccountService = weCorpAccountService;
        this.weCustomerExtendPropertyService = weCustomerExtendPropertyService;
        this.weCustomerTransferConfigService = weCustomerTransferConfigService;
    }

    /**
     * 初始化企业配置
     *
     * @param corpId       企业ID
     * @param corpFullName 企业全称
     * @param isCustomApp  企业密钥
     */
    @Override
    public void initCorpConfig(String corpId, String corpFullName, boolean isCustomApp) {
        this.initCorpConfigSynchronization(corpId, corpFullName, isCustomApp);
    }

    /**
     * 同步初始化企业配置
     *
     * @param corpId       企业ID
     * @param corpFullName 企业全称
     * @param isCustomApp  企业密钥
     */
    @Override
    public void initCorpConfigSynchronization(String corpId, String corpFullName, boolean isCustomApp) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        corpFullName = StringUtils.isBlank(corpFullName) ? Constants.SUPER_ADMIN : corpFullName;
        //这边使用try/catch是为了防止影响上层的企业信息入库
        try {
            weCorpAccountService.initWeCorpAccount(corpId, corpFullName, isCustomApp);
            initDefaultSystemProperty(corpId, corpFullName);
        } catch (Exception e) {
            log.error("企业初始化配置异常,{}", ExceptionUtils.getStackTrace(e));
        }
    }


    @Override
    public void initDefaultSystemProperty(String corpId, String corpFullName) {
        //初始化企业敏感行为
        boolean initResult = weSensitiveActService.initWeSensitiveAct(corpId, corpFullName);
        if (initResult) {
            log.info("初始化成功企业敏感行为,{}({})", corpFullName, corpId);
        } else {
            log.error("初始化失败企业敏感行为,{}({})", corpFullName, corpId);
        }
        //初始化角色
        initResult = weUserRoleService.initDefaultRole(corpId, corpFullName);
        if (initResult) {
            log.info("初始化成功企业默认角色,{}({})", corpFullName, corpId);
        } else {
            log.error("初始化失败企业默认角色,{}({})", corpFullName, corpId);
        }
        //初始化素材库分组
        initResult = weCategoryService.initCategory(corpId, corpFullName);
        if (initResult) {
            log.info("初始化成功企业素材库分组,{}({})", corpFullName, corpId);
        } else {
            log.error("初始化失败企业素材库分组,{}({})", corpFullName, corpId);
        }
        //初始化系统默认属性
        initResult = weCustomerExtendPropertyService.initSysProperty(corpId, corpFullName);
        if (initResult) {
            log.info("初始化成功系统默认字段,{}({})", corpFullName, corpId);
        } else {
            log.info("初始化失败系统默认字段,{}({})", corpFullName, corpId);
        }
        //初始化继承设置
        initResult = weCustomerTransferConfigService.initTransferConfig(corpId);
        if (initResult) {
            log.info("初始化成功系统继承设置,({})", corpId);
        } else {
            log.info("初始化失败系统继承设置,({})", corpId);
        }
    }

    /**
     * 同步初始化企业配置
     *
     * @param corpId       企业ID
     * @param corpFullName 企业全称
     */
    @Override
    public void initCorpConfigSynchronization(String corpId, String corpFullName) {
        this.initCorpConfigSynchronization(corpId, corpFullName, false);
    }

}
