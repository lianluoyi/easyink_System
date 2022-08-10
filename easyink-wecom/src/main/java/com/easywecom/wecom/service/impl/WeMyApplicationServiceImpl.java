package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeMyApplication;
import com.easywecom.wecom.domain.vo.MyApplicationIntroductionVO;
import com.easywecom.wecom.mapper.WeMyApplicationMapper;
import com.easywecom.wecom.service.WeMyApplicationService;
import com.easywecom.wecom.strategy.appstrategy.AppConfigHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类名: WeMyApplicationServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 9:46
 */
@Service
public class WeMyApplicationServiceImpl extends ServiceImpl<WeMyApplicationMapper, WeMyApplication> implements WeMyApplicationService {


    /**
     * 我的应用列表
     *
     * @param corpId 企业ID
     * @return {@link List < WeMyApplication >}
     */
    @Override
    public List<MyApplicationIntroductionVO> listOfMyApplication(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return new ArrayList<>(0);
        }
        List<MyApplicationIntroductionVO> list = this.getBaseMapper().listOfMyApplication(corpId);
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return list;
    }

    /**
     * 我的应用列表
     *
     * @param corpId 企业ID
     * @return {@link List < MyApplicationIntroductionVO >}
     */
    @Override
    public List<MyApplicationIntroductionVO> listOfMyApplication2Sidebar(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return new ArrayList<>(0);
        }
        List<MyApplicationIntroductionVO> list = this.getBaseMapper().listOfMyApplication2Sidebar(corpId);
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return list;
    }

    /**
     * 我的应用列表
     *
     * @param appid 应用ID
     * @return {@link List < MyApplicationIntroductionVO >}
     */
    @Override
    public List<MyApplicationIntroductionVO> listOfMyApplication(Integer appid) {
        if (appid == null || appid <= 0) {
            return new ArrayList<>(0);
        }
        List<MyApplicationIntroductionVO> list = this.getBaseMapper().listOfMyApplicationByAppid(appid);
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return list;
    }

    /**
     * 更新应用配置
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @param config 配置
     */
    @Override
    public void updateMyApplicationConfig(String corpId, Integer appid, String config) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (appid == null || appid < 0) {
            throw new CustomException(ResultTip.TIP_MISS_APPID);
        }
        if (StringUtils.isBlank(config)) {
            config = "";
        }
        //应用配置处理
        config = AppConfigHandler.switchHandler(appid, config, corpId);
        LambdaQueryWrapper<WeMyApplication> query = new LambdaQueryWrapper<>();
        query.eq(WeMyApplication::getCorpId, corpId);
        query.eq(WeMyApplication::getAppid, appid);

        WeMyApplication weMyApplication = new WeMyApplication();
        weMyApplication.setConfig(config);

        super.update(weMyApplication, query);

    }

    /**
     * 移除应用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     */
    @Override
    public void deleteMyApplication(String corpId, Integer appid) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (appid == null || appid < 0) {
            throw new CustomException(ResultTip.TIP_MISS_APPID);
        }

        LambdaQueryWrapper<WeMyApplication> query = new LambdaQueryWrapper<>();
        query.eq(WeMyApplication::getCorpId, corpId);
        query.eq(WeMyApplication::getAppid, appid);

        WeMyApplication weMyApplication = this.getOne(query);
        //由于产品设计移除应用后配置需要保留所以接口只做逻辑删除
        if (weMyApplication == null || weMyApplication.getId() == null) {
            return;
        }
        weMyApplication.setEnable(Boolean.FALSE);
        this.update(weMyApplication, query);

    }

    /**
     * 安装应用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     */
    @Override
    public void installApplication(String corpId, Integer appid) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (appid == null || appid < 0) {
            throw new CustomException(ResultTip.TIP_MISS_APPID);
        }

        LambdaQueryWrapper<WeMyApplication> query = new LambdaQueryWrapper<>();
        query.eq(WeMyApplication::getAppid, appid);
        query.eq(WeMyApplication::getCorpId, corpId);

        WeMyApplication weMyApplication = this.getOne(query);
        //原来没有安装过应用则初始化一个应用
        if (weMyApplication == null || weMyApplication.getId() == null) {
            weMyApplication = WeMyApplication.builder()
                    .appid(appid)
                    .corpId(corpId)
                    .config("")
                    .enable(Boolean.TRUE)
                    .installTime(new Date())
                    .build();
        } else {
            //如果已安装过，且应用是启用状态则返回应用已安装提示
            if (Boolean.TRUE.equals(weMyApplication.getEnable())) {
                throw new CustomException(ResultTip.TIP_APP_INSTALLED);
            }
            //如果已安装且卸载了，则重新为启用状态
            weMyApplication.setEnable(Boolean.TRUE);
        }
        super.saveOrUpdate(weMyApplication, query);
    }

    /**
     * 获取我的应用详情
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link MyApplicationIntroductionVO}
     */
    @Override
    public MyApplicationIntroductionVO getMyApplicationDetail(String corpId, Integer appid) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (appid == null || appid < 0) {
            throw new CustomException(ResultTip.TIP_MISS_APPID);
        }

        return this.getBaseMapper().getMyApplication(corpId, appid);
    }
}
