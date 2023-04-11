package com.easyink.wecom.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeLockSidebarConfig;
import com.easyink.wecom.domain.dto.LockSidebarConfigDTO;
import com.easyink.wecom.domain.vo.WeLockSidebarConfigVO;

/**
 * 络客侧边栏配置(WeLockSidebarConfig)表服务接口
 *
 * @author wx
 * @since 2023-03-14 15:39:09
 */
public interface WeLockSidebarConfigService extends IService<WeLockSidebarConfig> {

    /**
     * 编辑络客侧边栏配置
     *
     * @param dto   {@link LockSidebarConfigDTO}
     */
    void edit(LockSidebarConfigDTO dto);

    /**
     * 获取络客侧边栏配置
     *
     * @param appId appId
     * @return
     */
    WeLockSidebarConfigVO getConfig(String appId);
}
