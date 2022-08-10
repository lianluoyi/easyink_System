package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.enums.WeSensitiveActEnum;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.domain.WeSensitiveAct;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.WeSensitiveActMapper;
import com.easywecom.wecom.service.WeSensitiveActService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @date 2021/1/12 17:41
 */
@Slf4j
@Service
public class WeSensitiveActServiceImpl extends ServiceImpl<WeSensitiveActMapper, WeSensitiveAct> implements WeSensitiveActService {

    @Override
    public WeSensitiveAct selectWeSensitiveActById(Long id) {
        return getById(id);
    }

    @Override
    public List<WeSensitiveAct> selectWeSensitiveActList(WeSensitiveAct weSensitiveAct) {
        if (ObjectUtils.isEmpty(weSensitiveAct) || StringUtils.isBlank(weSensitiveAct.getCorpId())){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<WeSensitiveAct> lambda = Wrappers.lambdaQuery();
        if (weSensitiveAct != null && StringUtils.isNotBlank(weSensitiveAct.getActName())) {
            lambda.eq(WeSensitiveAct::getActName, weSensitiveAct.getActName());
        }
        lambda.eq(WeSensitiveAct::getCorpId, weSensitiveAct.getCorpId());
        lambda.orderByAsc(WeSensitiveAct::getOrderNum)
                .orderByDesc(WeSensitiveAct::getUpdateTime)
                .orderByDesc(WeSensitiveAct::getCreateTime);
        return list(lambda);
    }

    @Override
    public boolean insertWeSensitiveAct(WeSensitiveAct weSensitiveAct) {
        weSensitiveAct.setCreateBy(LoginTokenService.getUsername());
        weSensitiveAct.setCreateTime(DateUtils.getNowDate());
        return saveOrUpdate(weSensitiveAct);
    }

    @Override
    public boolean initWeSensitiveAct(String corpId, String createBy) {
        try {
            if (StringUtils.isBlank(corpId)) {
                return false;
            }
            if (StringUtils.isBlank(createBy)) {
                createBy = "admin";
            }
            List<WeSensitiveAct> list = new ArrayList<>();
            WeSensitiveAct weSensitiveAct1 = getBaseMapper().selectOne(new LambdaQueryWrapper<WeSensitiveAct>().eq(WeSensitiveAct::getCorpId, corpId).eq(WeSensitiveAct::getActName, WeSensitiveActEnum.DELETE.getInfo()));
            WeSensitiveAct weSensitiveAct2 = getBaseMapper().selectOne(new LambdaQueryWrapper<WeSensitiveAct>().eq(WeSensitiveAct::getCorpId, corpId).eq(WeSensitiveAct::getActName, WeSensitiveActEnum.SEND_REDPACK.getInfo()));
            WeSensitiveAct weSensitiveAct3 = getBaseMapper().selectOne(new LambdaQueryWrapper<WeSensitiveAct>().eq(WeSensitiveAct::getCorpId, corpId).eq(WeSensitiveAct::getActName, WeSensitiveActEnum.SEND_CARD.getInfo()));
            if (ObjectUtils.isEmpty(weSensitiveAct1)) {
                WeSensitiveAct weSensitiveAct = new WeSensitiveAct();
                weSensitiveAct.setCorpId(corpId);
                weSensitiveAct.setCreateBy(createBy);
                weSensitiveAct.setUpdateBy(createBy);
                weSensitiveAct.setActName(WeSensitiveActEnum.DELETE.getInfo());
                list.add(weSensitiveAct);
            }
            if (ObjectUtils.isEmpty(weSensitiveAct2)) {
                WeSensitiveAct weSensitiveAct = new WeSensitiveAct();
                weSensitiveAct.setCorpId(corpId);
                weSensitiveAct.setCreateBy(createBy);
                weSensitiveAct.setUpdateBy(createBy);
                weSensitiveAct.setActName(WeSensitiveActEnum.SEND_REDPACK.getInfo());
                list.add(weSensitiveAct);
            }
            if (ObjectUtils.isEmpty(weSensitiveAct3)) {
                WeSensitiveAct weSensitiveAct = new WeSensitiveAct();
                weSensitiveAct.setCorpId(corpId);
                weSensitiveAct.setCreateBy(createBy);
                weSensitiveAct.setUpdateBy(createBy);
                weSensitiveAct.setActName(WeSensitiveActEnum.SEND_CARD.getInfo());
                list.add(weSensitiveAct);
            }
            if (CollectionUtils.isEmpty(list)){
                return true;
            }
            return saveOrUpdateBatch(list);
        } catch (Exception e) {
            log.error("初始化敏感行为异常 ex:{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean initWeSensitiveAct(String corpId) {
        return initWeSensitiveAct(corpId, "admin");
    }

    @Override
    public boolean updateWeSensitiveAct(WeSensitiveAct weSensitiveAct) {
        weSensitiveAct.setUpdateBy(LoginTokenService.getUsername());
        weSensitiveAct.setUpdateTime(DateUtils.getNowDate());
        return saveOrUpdate(weSensitiveAct);
    }

    @Override
    public boolean deleteWeSensitiveActByIds(Long[] ids) {
        return removeByIds(Lists.newArrayList(ids));
    }
}
