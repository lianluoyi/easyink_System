package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeMyApplication;
import com.easywecom.wecom.domain.vo.MyApplicationIntroductionVO;

import java.util.List;

/**
 * 类名: WeMyApplicationService
 *
 * @author: 1*+
 * @date: 2021-09-09 9:45
 */
public interface WeMyApplicationService extends IService<WeMyApplication> {


    /**
     * 我的应用列表
     *
     * @param corpId 企业ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplication(String corpId);

    /**
     * 我的应用列表
     *
     * @param corpId 企业ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplication2Sidebar(String corpId);

    /**
     * 我的应用列表
     *
     * @param appid 应用ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplication(Integer appid);

    /**
     * 更新应用配置
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @param config 配置
     */
    void updateMyApplicationConfig(String corpId, Integer appid, String config);

    /**
     * 移除应用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     */
    void deleteMyApplication(String corpId, Integer appid);

    /**
     * 安装应用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     */
    void installApplication(String corpId, Integer appid);

    /**
     * 获取我的应用详情
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link MyApplicationIntroductionVO}
     */
    MyApplicationIntroductionVO getMyApplicationDetail(String corpId, Integer appid);
}
