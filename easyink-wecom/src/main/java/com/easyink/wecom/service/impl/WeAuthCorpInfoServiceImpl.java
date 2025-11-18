package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.wecom.domain.WeAuthCorpInfo;
import com.easyink.wecom.domain.WeAuthCorpInfoExtend;
import com.easyink.wecom.domain.vo.CheckCorpIdVO;
import com.easyink.wecom.mapper.WeAuthCorpInfoMapper;
import com.easyink.wecom.service.WeAuthCorpInfoExtendService;
import com.easyink.wecom.service.WeAuthCorpInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类名: WeAuthCorpInfoServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 9:46
 */
@Service
public class WeAuthCorpInfoServiceImpl extends ServiceImpl<WeAuthCorpInfoMapper, WeAuthCorpInfo> implements WeAuthCorpInfoService {


    private final RuoYiConfig ruoYiConfig;
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;

    @Autowired
    public WeAuthCorpInfoServiceImpl(RuoYiConfig ruoYiConfig, WeAuthCorpInfoExtendService weAuthCorpInfoExtendService) {
        this.ruoYiConfig = ruoYiConfig;
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
    }


    /**
     * 企业已授权
     *
     * @param corpId  授权企业ID
     * @param suiteId 第三方应用ID
     * @return 已授权：true ， 未授权：false
     */
    @Override
    public boolean corpAuthorized(String corpId, String suiteId) {
        if (StringUtils.isBlank(corpId)) {
            return false;
        }

        if (StringUtils.isBlank(suiteId)) {
            return false;
        }

        LambdaQueryWrapper<WeAuthCorpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeAuthCorpInfo::getCorpId, corpId).eq(WeAuthCorpInfo::getSuiteId, suiteId).eq(WeAuthCorpInfo::getCancelAuth, false).last(GenConstants.LIMIT_1);
        int authCount = (int)this.count(queryWrapper);
        return authCount > 0;
    }

    @Override
    public boolean corpAuthorized(String corpId) {
        //默认使用webSuiteId
        return corpAuthorized(corpId, ruoYiConfig.getProvider().getWebSuite().getSuiteId());
    }

    @Override
    public List<WeAuthCorpInfo> listOfAuthorizedCorpInfo(String suiteId) {
        LambdaQueryWrapper<WeAuthCorpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeAuthCorpInfo::getSuiteId, suiteId).eq(WeAuthCorpInfo::getCancelAuth, false).groupBy(WeAuthCorpInfo::getCorpId);
        List<WeAuthCorpInfo> authCorpInfos = this.list(queryWrapper);
        //添加扩展实体
        for (WeAuthCorpInfo authCorpInfo : authCorpInfos) {
            WeAuthCorpInfoExtend corpInfoExtend = weAuthCorpInfoExtendService.getOne(new LambdaQueryWrapper<WeAuthCorpInfoExtend>().eq(WeAuthCorpInfoExtend::getCorpId, authCorpInfo.getCorpId()).eq(WeAuthCorpInfoExtend::getSuiteId, suiteId));
            authCorpInfo.setCorpInfoExtend(corpInfoExtend);
        }
        return authCorpInfos;
    }

    /**
     * 获取授权企业
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfo}
     */
    @Override
    public WeAuthCorpInfo getOne(String corpId, String suiteId) {
        if (StringUtils.isAnyBlank(corpId, suiteId)) {
            return new WeAuthCorpInfo();
        }
        LambdaQueryWrapper<WeAuthCorpInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeAuthCorpInfo::getCorpId, corpId);
        queryWrapper.eq(WeAuthCorpInfo::getSuiteId, suiteId);
        queryWrapper.eq(WeAuthCorpInfo::getCancelAuth, false);
        queryWrapper.last(GenConstants.LIMIT_1);
        WeAuthCorpInfo authCorpInfo = this.getOne(queryWrapper);
        if (authCorpInfo == null) {
            return new WeAuthCorpInfo();
        }
        //添加扩展实体
        WeAuthCorpInfoExtend corpInfoExtend = weAuthCorpInfoExtendService.getOne(new LambdaQueryWrapper<WeAuthCorpInfoExtend>().eq(WeAuthCorpInfoExtend::getCorpId, corpId).eq(WeAuthCorpInfoExtend::getSuiteId, suiteId));
        authCorpInfo.setCorpInfoExtend(corpInfoExtend);
        return authCorpInfo;
    }

    /**
     * 获取授权企业忽略是否取消
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfo}
     */
    @Override
    public WeAuthCorpInfo getAuthIgnoreCancel(String corpId, String suiteId) {
        if (StringUtils.isAnyBlank(corpId, suiteId)) {
            return new WeAuthCorpInfo();
        }
        WeAuthCorpInfo authCorpInfo = this.getOne(new LambdaQueryWrapper<WeAuthCorpInfo>().eq(WeAuthCorpInfo::getCorpId, corpId).eq(WeAuthCorpInfo::getSuiteId, suiteId).last(GenConstants.LIMIT_1));
        if (authCorpInfo == null) {
            return new WeAuthCorpInfo();
        }
        //添加扩展实体
        WeAuthCorpInfoExtend corpInfoExtend = weAuthCorpInfoExtendService.getOne(new LambdaQueryWrapper<WeAuthCorpInfoExtend>().eq(WeAuthCorpInfoExtend::getCorpId, corpId).eq(WeAuthCorpInfoExtend::getSuiteId, suiteId));
        authCorpInfo.setCorpInfoExtend(corpInfoExtend);
        return authCorpInfo;
    }

    /**
     * 获取授权企业
     *
     * @param corpId 企业ID
     * @return {@link WeAuthCorpInfo}
     */
    @Override
    public WeAuthCorpInfo getOne(String corpId) {
        return this.getOne(corpId, ruoYiConfig.getProvider().getWebSuite().getSuiteId());
    }

    /**
     * 是否是代开发企业
     *
     * @param corpId 企业ID
     * @return {@link CheckCorpIdVO}
     */
    @Override
    public CheckCorpIdVO isDkCorp(String corpId) {
        CheckCorpIdVO checkCorpIdVO = CheckCorpIdVO.builder().dkCorp(false).build();
        if (ruoYiConfig.isThirdServer()) {
            //如果企业ID是密文则直接是代开发
//            if (StringUtils.isBlank(corpId) || corpId.startsWith("wpI")) {
//                return CheckCorpIdVO.builder().dkCorp(true).build();
//            }
//
//            WeAuthCorpInfo authCorpInfo = this.getAuthIgnoreCancel(corpId, ruoYiConfig.getProvider().getDkSuite().getDkId());
//            if (authCorpInfo != null && authCorpInfo.getCorpInfoExtend() != null && authCorpInfo.getCorpInfoExtend().getIsCustomizedApp()) {
//                checkCorpIdVO = CheckCorpIdVO.builder().dkCorp(true).build();
//            }
            // V1.8.3目前没有三方应用,所有三方的所有都是代开发
            checkCorpIdVO.setDkCorp(true);
            return checkCorpIdVO;
        }
        return checkCorpIdVO;
    }

    /**
     * 保存或更新
     *
     * @param entity 授权信息实体
     * @return {@link Boolean}
     */
    @Override
    public boolean saveOrUpdate(WeAuthCorpInfo entity) {
        if (entity == null || StringUtils.isAnyBlank(entity.getCorpId(), entity.getSuiteId())) {
            return false;
        }

        LambdaUpdateWrapper<WeAuthCorpInfo> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(WeAuthCorpInfo::getCorpId, entity.getCorpId());
        queryWrapper.eq(WeAuthCorpInfo::getSuiteId, entity.getSuiteId());

        int result;
        result = (this.baseMapper.selectCount(queryWrapper) <= 0) ? this.baseMapper.insert(entity) : this.baseMapper.update(entity, queryWrapper);
        return result > 0;
    }
}
