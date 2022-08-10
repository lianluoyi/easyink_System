package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.WeAuthCorpInfoExtend;

/**
 * 类名: WeAuthCorpInfoExtendService
 *
 * @author: 1*+
 * @date: 2021-09-09 9:45
 */
public interface WeAuthCorpInfoExtendService extends IService<WeAuthCorpInfoExtend> {
    /**
     * 是否为待开发应用
     * @param corpId  企业ID
     * @return true 是 false 否
     */
    boolean isCustomizedApp(String corpId);

    /**
     * 是否不是待开发应用
     * @param corpId  企业ID
     * @return true 不是待开发 false 是
     */
    boolean isNotCustomizedApp(String corpId);



    /**
     * 获取授权企业
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfoExtend}
     */
    WeAuthCorpInfoExtend getOne(String corpId, String suiteId);

}
