package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.wecom.domain.WeGroupStatistic;
import com.easywecom.wecom.domain.dto.WePageCountDTO;
import com.easywecom.wecom.domain.query.WePageStateQuery;
import com.easywecom.wecom.mapper.WeGroupStatisticMapper;
import com.easywecom.wecom.service.WeGroupStatisticService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群聊数据统计数据
 * Service业务层处理
 *
 * @author admin
 * @date 2021-02-24
 */
@Service
public class WeGroupStatisticServiceImpl extends ServiceImpl<WeGroupStatisticMapper, WeGroupStatistic> implements WeGroupStatisticService {

    @Override
    public List<WeGroupStatistic> queryList(WeGroupStatistic weGroupStatistic) {
        LambdaQueryWrapper<WeGroupStatistic> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(weGroupStatistic.getChatId())) {
            lqw.eq(WeGroupStatistic::getChatId, weGroupStatistic.getChatId());
        }
        if (weGroupStatistic.getStatTime() != null) {
            lqw.eq(WeGroupStatistic::getStatTime, weGroupStatistic.getStatTime());
        }
        if (weGroupStatistic.getNewChatCnt() != null) {
            lqw.eq(WeGroupStatistic::getNewChatCnt, weGroupStatistic.getNewChatCnt());
        }
        if (weGroupStatistic.getChatTotal() != null) {
            lqw.eq(WeGroupStatistic::getChatTotal, weGroupStatistic.getChatTotal());
        }
        if (weGroupStatistic.getChatHasMsg() != null) {
            lqw.eq(WeGroupStatistic::getChatHasMsg, weGroupStatistic.getChatHasMsg());
        }
        if (weGroupStatistic.getNewMemberCnt() != null) {
            lqw.eq(WeGroupStatistic::getNewMemberCnt, weGroupStatistic.getNewMemberCnt());
        }
        if (weGroupStatistic.getMemberTotal() != null) {
            lqw.eq(WeGroupStatistic::getMemberTotal, weGroupStatistic.getMemberTotal());
        }
        if (weGroupStatistic.getMemberHasMsg() != null) {
            lqw.eq(WeGroupStatistic::getMemberHasMsg, weGroupStatistic.getMemberHasMsg());
        }
        if (weGroupStatistic.getMsgTotal() != null) {
            lqw.eq(WeGroupStatistic::getMsgTotal, weGroupStatistic.getMsgTotal());
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
