package com.easyink.wecom.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.WeGroupStatistic;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import com.easyink.wecom.mapper.WeGroupStatisticMapper;
import com.easyink.wecom.service.WeGroupStatisticService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        List<WePageCountDTO> baseList = this.baseMapper.getWeekCountData(wePageStateQuery);
        // 再获取 群聊总数和群成员总数的列表
        List<WePageCountDTO> chatList = this.baseMapper.getGroupCntWeekDate(wePageStateQuery);
        // 把群聊总数设置到基础列表中
        setChatData(baseList, chatList);
        return baseList;
    }

    @Override
    public List<WePageCountDTO> getMonthCountData(WePageStateQuery wePageStateQuery) {
        // 获取基础的统计数据
        List<WePageCountDTO> baseList = this.baseMapper.getMonthCountData(wePageStateQuery);
        // 获取群聊数和群成员数的统计数据
        Date yesterday = DateUtil.beginOfDay(DateUtil.yesterday());
        List<WePageCountDTO> chatList = this.baseMapper.getMonthChatCntDate(yesterday, wePageStateQuery.getCorpId());
        setChatData(baseList, chatList);
        return baseList;
    }

    /**
     * 设置群聊总数和群成员总数
     *
     * @param baseList 其他统计数据列表
     * @param chatList 群聊总数和群成员总数列表
     */
    public void setChatData(List<WePageCountDTO> baseList, List<WePageCountDTO> chatList) {
        for (WePageCountDTO baseDto : baseList) {
            for (WePageCountDTO chatDto : chatList) {
                if (chatDto != null && chatDto.getXTime() != null && chatDto.getXTime()
                                                         .equals(baseDto.getXTime())) {
                    baseDto.setChatTotal(chatDto.getChatTotal());
                    baseDto.setMemberTotal(chatDto.getMemberTotal());
                }
            }
        }
    }


    @Override
    public Integer getGroupMemberCnt(String corpId, Date date) {
        if (StringUtils.isEmpty(corpId) || date == null) {
            return 0;
        }
        Date beginTime = DateUtil.beginOfDay(date);
        Date endTime = DateUtil.endOfDay(date);
        return this.baseMapper.getGroupMemberCnt(corpId, beginTime, endTime);
    }

}
