package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.wecom.domain.WeUserBehaviorData;
import com.easywecom.wecom.domain.dto.WePageCountDTO;
import com.easywecom.wecom.domain.query.WePageStateQuery;
import com.easywecom.wecom.mapper.WeUserBehaviorDataMapper;
import com.easywecom.wecom.service.WeUserBehaviorDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 联系客户统计数据 Service业务层处理
 *
 * @author admin
 * @date 2021-02-24
 */
@Service
public class WeUserBehaviorDataServiceImpl extends ServiceImpl<WeUserBehaviorDataMapper, WeUserBehaviorData> implements WeUserBehaviorDataService {

    @Override
    public List<WeUserBehaviorData> queryList(WeUserBehaviorData weUserBehaviorData) {
        LambdaQueryWrapper<WeUserBehaviorData> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(weUserBehaviorData.getUserId())) {
            lqw.eq(WeUserBehaviorData::getUserId, weUserBehaviorData.getUserId());
        }
        if (weUserBehaviorData.getStatTime() != null) {
            lqw.eq(WeUserBehaviorData::getStatTime, weUserBehaviorData.getStatTime());
        }
        if (weUserBehaviorData.getNewApplyCnt() != null) {
            lqw.eq(WeUserBehaviorData::getNewApplyCnt, weUserBehaviorData.getNewApplyCnt());
        }
        if (weUserBehaviorData.getNewContactCnt() != null) {
            lqw.eq(WeUserBehaviorData::getNewContactCnt, weUserBehaviorData.getNewContactCnt());
        }
        if (weUserBehaviorData.getChatCnt() != null) {
            lqw.eq(WeUserBehaviorData::getChatCnt, weUserBehaviorData.getChatCnt());
        }
        if (weUserBehaviorData.getMessageCnt() != null) {
            lqw.eq(WeUserBehaviorData::getMessageCnt, weUserBehaviorData.getMessageCnt());
        }
        if (weUserBehaviorData.getReplyPercentage() != null) {
            lqw.eq(WeUserBehaviorData::getReplyPercentage, weUserBehaviorData.getReplyPercentage());
        }
        if (weUserBehaviorData.getAvgReplyTime() != null) {
            lqw.eq(WeUserBehaviorData::getAvgReplyTime, weUserBehaviorData.getAvgReplyTime());
        }
        if (weUserBehaviorData.getNegativeFeedbackCnt() != null) {
            lqw.eq(WeUserBehaviorData::getNegativeFeedbackCnt, weUserBehaviorData.getNegativeFeedbackCnt());
        }
        return this.list(lqw);
    }

    @Override
    public WePageCountDTO getCountDataByDayNew(String corpId, String beginTime, String endTime) {
        return this.baseMapper.getCountDataByDayNew(corpId, beginTime, endTime);
    }

    @Override
    public List<WePageCountDTO> getDayCountData(WePageStateQuery wePageStateQuery) {
        return this.baseMapper.getDayCountData(wePageStateQuery);
    }

    @Override
    public List<WePageCountDTO> getWeekCountData(WePageStateQuery wePageStateQuery) {
        return this.baseMapper.getWeekCountData(wePageStateQuery);
    }

    @Override
    public List<WePageCountDTO> getMonthCountData(WePageStateQuery wePageStateQuery) {
        return this.baseMapper.getMonthCountData(wePageStateQuery);
    }
}
