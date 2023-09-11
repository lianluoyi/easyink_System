package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeEmpleCodeWarnConfig;
import com.easyink.wecom.domain.dto.emplecode.EmpleWarnConfigDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeWarnConfigVO;

/**
 * 获客链接告警设置Service层
 *
 * @author lichaoyu
 * @date 2023/8/23 9:37
 */
public interface WeEmpleCodeWarnConfigService extends IService<WeEmpleCodeWarnConfig> {

    /**
     * 更新或插入获客链接告警配置信息
     *
     * @param warnConfigDTO {@link EmpleWarnConfigDTO}
     * @return 结果
     */
    Integer saveOrUpdateConfig(EmpleWarnConfigDTO warnConfigDTO);

    /**
     * 获取获客链接告警配置信息
     *
     * @param corpId 企业ID
     * @return {@link WeEmpleCodeWarnConfig}
     */
    WeEmpleCodeWarnConfigVO getConfig(String corpId);
}
