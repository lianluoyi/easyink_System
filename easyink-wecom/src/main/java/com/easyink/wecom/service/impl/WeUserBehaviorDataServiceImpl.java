package com.easyink.wecom.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.dto.statistics.CustomerActivityDTO;
import com.easyink.wecom.domain.dto.statistics.StatisticsDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import com.easyink.wecom.domain.vo.statistics.CustomerOverviewVO;
import com.easyink.wecom.mapper.WeUserBehaviorDataMapper;
import com.easyink.wecom.service.WeUserBehaviorDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 联系客户统计数据 Service业务层处理
 *
 * @author admin
 * @date 2021-02-24
 */
@Service
@Slf4j
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
        // 先获取基础数据的列表
        return this.baseMapper.getWeekCountData(wePageStateQuery);
    }

    @Override
    public List<WePageCountDTO> getMonthCountData(WePageStateQuery wePageStateQuery) {
        return this.baseMapper.getMonthCountData(wePageStateQuery);
    }

    /**
     * 获取统计时间
     * @param isToday 是否要统计到今日
     * @return 时间
     */
    private static String getStatTime(Boolean isToday) {
        if(Boolean.TRUE.equals(isToday)) {
            // 如果是实时数据则取今天
            return DateUtil.beginOfDay(new Date()).toString() ;
        }else {
            return DateUtil.beginOfDay(DateUtil.yesterday()).toString() ;
        }
    }



}
