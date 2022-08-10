package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.wecom.domain.WeApplicationCenter;
import com.easyink.wecom.domain.WeMyApplication;
import com.easyink.wecom.domain.dto.SetApplicationUseScopeDTO;
import com.easyink.wecom.domain.vo.ApplicationIntroductionVO;
import com.easyink.wecom.domain.vo.WeApplicationDetailVO;
import com.easyink.wecom.mapper.WeApplicationCenterMapper;
import com.easyink.wecom.service.WeApplicationCenterService;
import com.easyink.wecom.service.WeMyApplicationService;
import com.easyink.wecom.service.WeMyApplicationUseScopeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名: WeApplicationCenterServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 9:46
 */
@Service
public class WeApplicationCenterServiceImpl extends ServiceImpl<WeApplicationCenterMapper, WeApplicationCenter> implements WeApplicationCenterService {


    @Autowired
    private WeMyApplicationService weMyApplicationService;
    @Autowired
    private WeMyApplicationUseScopeService weMyApplicationUseScopeService;

    /**
     * 获取应用中心列表
     *
     * @param type 应用类型
     * @param name 应用名
     * @return {@link List<ApplicationIntroductionVO>}
     */
    @Override
    public List<ApplicationIntroductionVO> listOfEnableApplication(Integer type, String name) {
        LambdaQueryWrapper<WeApplicationCenter> query = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            query.like(WeApplicationCenter::getName, name);
        }
        if (type > 0) {
            query.eq(WeApplicationCenter::getType, type);
        }
        query.eq(WeApplicationCenter::getEnable, true);
        List<WeApplicationCenter> weApplicationCenterList = super.list(query);

        List<ApplicationIntroductionVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(weApplicationCenterList)) {
            result = weApplicationCenterList.stream().map(po -> {
                ApplicationIntroductionVO vo = new ApplicationIntroductionVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 获取应用详情
     *
     * @param appid 应用ID
     * @param corpId 企业ID
     * @return {@link WeApplicationCenter}
     */
    @Override
    public WeApplicationDetailVO getApplicationDetail(Integer appid, String corpId) {
        WeApplicationDetailVO weApplicationDetailVO;
        if (appid == null || appid < 0) {
            weApplicationDetailVO = WeApplicationDetailVO.copy(WeApplicationCenter.builder().appid(-1).name("不存在的应用").build());
            weApplicationDetailVO.setInstalled(Boolean.FALSE);
            return weApplicationDetailVO;
        }

        LambdaQueryWrapper<WeApplicationCenter> query = new LambdaQueryWrapper<>();
        query.eq(WeApplicationCenter::getAppid, appid);
        query.last(GenConstants.LIMIT_1);
        WeApplicationCenter po = super.getOne(query);
        if (po == null) {
            weApplicationDetailVO = WeApplicationDetailVO.copy(WeApplicationCenter.builder().appid(-1).name("不存在的应用").build());
            weApplicationDetailVO.setInstalled(Boolean.FALSE);
            return weApplicationDetailVO;
        }
        weApplicationDetailVO = WeApplicationDetailVO.copy(po);

        if (StringUtils.isBlank(corpId)) {
            //如果企业ID为空则返回未安装
            weApplicationDetailVO.setInstalled(Boolean.FALSE);
            weApplicationDetailVO.setConfig("");
        } else {
            //判断该应用在该企业是否已安装
            LambdaQueryWrapper<WeMyApplication> query1 = new LambdaQueryWrapper<>();
            query1.eq(WeMyApplication::getAppid, appid);
            query1.eq(WeMyApplication::getCorpId, corpId);
            query1.eq(WeMyApplication::getEnable, Boolean.TRUE);
            query1.last(GenConstants.LIMIT_1);
            WeMyApplication installWeMyApplication = weMyApplicationService.getOne(query1);
            if (installWeMyApplication != null) {
                //由于前端在应用详情中如果是已安装需要可以直接进入配置的URL，所以这边增加配置返回
                weApplicationDetailVO.setConfig(installWeMyApplication.getConfig());
                weApplicationDetailVO.setInstalled(Boolean.TRUE);
            }
        }
        List<SetApplicationUseScopeDTO.UseScope> scopeList = weMyApplicationUseScopeService.getUseScope(corpId, appid);
        weApplicationDetailVO.setScopeList(scopeList);
        return weApplicationDetailVO;
    }
}
