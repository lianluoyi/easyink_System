package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.WeCorpThirdPartyConfig;
import com.easyink.wecom.domain.dto.form.push.WeCorpThirdPartyConfigDTO;

import java.util.List;

/**
 * 企业第三方服务推送配置Service接口
 *
 * @author easyink
 * @date 2024-01-01
 */
public interface WeCorpThirdPartyConfigService extends IService<WeCorpThirdPartyConfig> {

    /**
     * 根据企业ID获取配置
     *
     * @param corpId 企业ID
     * @return 配置信息
     */
    WeCorpThirdPartyConfig getByCorpId(String corpId);

    /**
     * 新增或修改配置
     *
     * @param dto       配置DTO
     * @param loginUser
     * @return 操作结果
     */
    boolean saveOrUpdateConfig(WeCorpThirdPartyConfigDTO dto, LoginUser loginUser);

    /**
     * 删除配置
     *
     * @param corpIdList 企业id列表
     * @param corpId 企业ID
     * @return 操作结果
     */
    boolean deleteConfig(List<String> corpIdList, String corpId);

    /**
     * 启用/禁用配置
     *
     * @param corpId 企业ID
     * @param status 状态
     * @return 操作结果
     */
    boolean updateStatus(String corpId, Integer status);

}
