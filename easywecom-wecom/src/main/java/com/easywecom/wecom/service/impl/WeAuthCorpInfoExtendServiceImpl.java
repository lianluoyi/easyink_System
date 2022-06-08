package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.wecom.domain.WeAuthCorpInfoExtend;
import com.easywecom.wecom.mapper.WeAuthCorpInfoExtendMapper;
import com.easywecom.wecom.service.WeAuthCorpInfoExtendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名: WeAuthCorpInfoExtendServiceImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 9:46
 */
@Service
public class WeAuthCorpInfoExtendServiceImpl extends ServiceImpl<WeAuthCorpInfoExtendMapper, WeAuthCorpInfoExtend> implements WeAuthCorpInfoExtendService {
    private final RuoYiConfig ruoYiConfig;
    @Autowired
    public WeAuthCorpInfoExtendServiceImpl(RuoYiConfig ruoYiConfig) {
        this.ruoYiConfig = ruoYiConfig;
    }

    /**
     * 保存或更新
     *
     * @param entity 授权信息实体
     * @return {@link Boolean}
     */
    @Override
    public boolean saveOrUpdate(WeAuthCorpInfoExtend entity) {
        if (entity == null || StringUtils.isAnyBlank(entity.getCorpId(), entity.getSuiteId())) {
            return false;
        }
        LambdaUpdateWrapper<WeAuthCorpInfoExtend> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(WeAuthCorpInfoExtend::getCorpId, entity.getCorpId());
        queryWrapper.eq(WeAuthCorpInfoExtend::getSuiteId, entity.getSuiteId());
        int result = (this.baseMapper.selectCount(queryWrapper) <= 0) ? this.baseMapper.insert(entity) : this.baseMapper.update(entity, queryWrapper);
        return result > 0;
    }

    /**
     * 获取授权企业
     *
     * @param corpId  企业ID
     * @param suiteId 应用ID
     * @return {@link WeAuthCorpInfoExtend}
     */
    @Override
    public WeAuthCorpInfoExtend getOne(String corpId, String suiteId) {
        if (StringUtils.isAnyBlank(corpId, suiteId)) {
            return new WeAuthCorpInfoExtend();
        }
        LambdaQueryWrapper<WeAuthCorpInfoExtend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeAuthCorpInfoExtend::getCorpId, corpId);
        queryWrapper.eq(WeAuthCorpInfoExtend::getSuiteId, suiteId);
        queryWrapper.last(GenConstants.LIMIT_1);
        WeAuthCorpInfoExtend entity = this.getOne(queryWrapper);
        if (entity == null) {
            return new WeAuthCorpInfoExtend();
        }
        return entity;
    }

    /**
     * 是否为待开发应用
     * @param corpId  企业ID
     * @return true 是 false 否
     */
    @Override
    public boolean isCustomizedApp(String corpId) {
        WeAuthCorpInfoExtend corpInfoExtend = this.getOne(new LambdaQueryWrapper<WeAuthCorpInfoExtend>()
                .eq(WeAuthCorpInfoExtend::getCorpId, corpId)
                .eq(WeAuthCorpInfoExtend::getSuiteId, ruoYiConfig.getProvider().getDkSuite().getDkId()));
        if (corpInfoExtend != null && corpInfoExtend.getIsCustomizedApp() != null){
            return corpInfoExtend.getIsCustomizedApp();
        }
        return Boolean.FALSE;
    }

    /**
     * 是否不是待开发应用
     * @param corpId  企业ID
     * @return true 不是待开发 false 是
     */
    @Override
    public boolean isNotCustomizedApp(String corpId) {
        return !isCustomizedApp(corpId);
    }
}
