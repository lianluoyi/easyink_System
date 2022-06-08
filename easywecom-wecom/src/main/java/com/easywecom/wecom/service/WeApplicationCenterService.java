package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeApplicationCenter;
import com.easywecom.wecom.domain.vo.ApplicationIntroductionVO;
import com.easywecom.wecom.domain.vo.WeApplicationDetailVO;

import java.util.List;

/**
 * 类名: WeApplicationCenterService
 *
 * @author: 1*+
 * @date: 2021-09-09 9:45
 */
public interface WeApplicationCenterService extends IService<WeApplicationCenter> {

    /**
     * 获取应用中心列表
     *
     * @param type 应用类型
     * @param name 应用名
     * @return {@link List<ApplicationIntroductionVO>}
     */
    List<ApplicationIntroductionVO> listOfEnableApplication(Integer type, String name);

    /**
     * 获取应用详情
     *
     * @param appid  应用ID
     * @param corpId 企业ID
     * @return {@link WeApplicationCenter}
     */
    WeApplicationDetailVO getApplicationDetail(Integer appid, String corpId);


}
