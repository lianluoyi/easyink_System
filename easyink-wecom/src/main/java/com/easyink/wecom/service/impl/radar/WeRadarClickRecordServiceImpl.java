package com.easyink.wecom.service.impl.radar;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.model.RadarShortUrlAppendInfo;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.radar.*;
import com.easyink.wecom.domain.entity.radar.WeRadarClickRecord;
import com.easyink.wecom.domain.vo.radar.*;
import com.easyink.wecom.mapper.radar.WeRadarClickRecordMapper;
import com.easyink.wecom.service.radar.WeRadarClickRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClassName： WeRadarClickRecordServiceImpl
 *
 * @author wx
 * @date 2022/7/19 19:56
 */
@Service
@Slf4j
public class WeRadarClickRecordServiceImpl extends ServiceImpl<WeRadarClickRecordMapper, WeRadarClickRecord> implements WeRadarClickRecordService {


    /**
     * 保存雷达记录
     *
     * @param clickRecordDTO
     */
    @Override
    public void saveClickRecord(RadarClickRecordDTO clickRecordDTO) {
        final WeRadarClickRecord radarClickRecord = clickRecordDTO.buildData();
        this.save(radarClickRecord);
    }

    @Override
    public RadarRecordTotalVO getTotal(Long radarId) {
        String nowDate = DateUtils.getDate();
        return this.baseMapper.getTotal(radarId, nowDate);
    }

    /**
     * 查询雷达数据统计（折线图）
     *
     * @param radarAnalyseDTO
     * @return
     */
    @Override
    public RadarAnalyseVO getTimeRangeAnalyseCount(SearchRadarAnalyseDTO radarAnalyseDTO) {
        verifyParam(radarAnalyseDTO);

        //查询两个时间内的所有日期
        List<Date> dates;
        if (ObjectUtils.isNotEmpty(radarAnalyseDTO.getBeginTime())) {
            Date startTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, radarAnalyseDTO.getBeginTime());
            Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, radarAnalyseDTO.getEndTime());
            dates = DateUtils.findDates(startTime, endTime);
        } else {
            Date startTime = DateUtils.dateSubDay(DateUtils.getNowDate(), RadarConstants.RadarAnalyseCount.DEFAULT_DAY - 1);
            Date endTime = DateUtils.getNowDate();
            dates = DateUtils.findDates(startTime, endTime);
        }
        List<RadarAnalyseCountVO> list = this.baseMapper.selectCountList(radarAnalyseDTO);


        Map<String, RadarAnalyseCountVO> dataMap = new HashMap<>(list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            for (RadarAnalyseCountVO item : list) {
                dataMap.put(item.getCreateDate(), item);
            }
        }
        List<RadarAnalyseCountVO> resultList = new ArrayList<>();
        RadarAnalyseCountVO analyseCountVO;
        for (Date date : dates) {
            String time = DateUtils.dateTime(date);
            analyseCountVO = dataMap.get(time);
            if (analyseCountVO == null) {
                analyseCountVO = RadarAnalyseCountVO.builder()
                        .createDate(time)
                        .clickPersonNum(RadarConstants.RadarAnalyseCount.ZERO)
                        .sumClickNum(RadarConstants.RadarAnalyseCount.ZERO).build();
            }
            resultList.add(analyseCountVO);
        }
        return RadarAnalyseVO.builder()
                .list(resultList)
                .total(dates.size()).build();
    }

    /**
     * 查询渠道排序
     *
     * @param radarId   雷达ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD
     * @param endTime   结束时间，格式为YYYY-MM-DD
     * @return {@link RadarChannelSortVO}
     */
    @Override
    public List<RadarChannelSortVO> getChannelSort(Long radarId, String beginTime, String endTime) {
        if (ObjectUtils.isEmpty(radarId)) {
            throw new CustomException(RadarConstants.ParamVerify.PARAM_NULL);
        }
        return this.baseMapper.selectChannelSort(radarId, beginTime, endTime);
    }

    /**
     * 获取客户点击记录
     *
     * @param customerRecordDTO
     * @return
     */
    @Override
    public List<RadarCustomerRecordVO> getCustomerClickRecord(SearchCustomerRecordDTO customerRecordDTO) {
        if (ObjectUtils.isEmpty(customerRecordDTO)) {
            throw new CustomException(RadarConstants.ParamVerify.PARAM_NULL);
        }
        return this.baseMapper.getCustomerClickRecord(customerRecordDTO);
    }

    /**
     * 获取渠道点击记录
     *
     * @param channelRecordDTO
     * @return
     */
    @Override
    public List<RadarChannelRecordVO> getChannelClickRecord(SearchChannelRecordDTO channelRecordDTO) {
        if (ObjectUtils.isEmpty(channelRecordDTO)) {
            throw new CustomException(RadarConstants.ParamVerify.PARAM_NULL);
        }
        return this.baseMapper.getChannelClickRecord(channelRecordDTO);
    }

    /**
     * 获取渠道点击记录详情
     *
     * @param channelRecordDetailDTO
     * @return
     */
    @Override
    public List<RadarCustomerRecordVO> getChannelClickRecordDetail(SearchChannelRecordDetailDTO channelRecordDetailDTO) {
        if (ObjectUtils.isEmpty(channelRecordDetailDTO)) {
            throw new CustomException(RadarConstants.ParamVerify.PARAM_NULL);
        }
        return this.baseMapper.getChannelClickRecordDetail(channelRecordDetailDTO);
    }

    @Override
    public List<RadarCustomerClickRecordDetailVO> getCustomerClickRecordDetail(SearchCustomerRecordDetailDTO customerRecordDTO) {
        if (ObjectUtils.isEmpty(customerRecordDTO)) {
            throw new CustomException(RadarConstants.ParamVerify.PARAM_NULL);
        }
        List<RadarCustomerClickRecordDetailVO> list = this.baseMapper.getCustomerClickRecordDetail(customerRecordDTO);
        list.forEach(item -> {
            item.setRecordText(RadarConstants.RadarCustomerClickRecord.getRecordText(item.getCustomerName(), item.getUserName(), item.getDetail(), item.getChannelType(), item.getChannelName()));
        });
        return list;
    }

    @Override
    public void createRecord(RadarShortUrlAppendInfo appendInfo, WeCustomer customer, String openId, WeUser user) {
        if (appendInfo == null || appendInfo.getRadarId() == null || customer == null || StringUtils.isAnyBlank(customer.getUnionid())) {
            log.info("[保存雷达记录]参数缺失,append:{},customer:{},openId:{},user:{}", appendInfo, customer, openId, user);
            return;
        }
        String channelName;
        Integer channelType;
        // 如果是系統渠道取系統渠道名和類型
        if (RadarChannelEnum.isSysChannel(appendInfo.getChannelType())) {
            channelType = appendInfo.getChannelType();
            channelName = RadarChannelEnum.getChannelByType(appendInfo.getChannelType());
        } else {
            //自定义渠道,取detail为自定义渠道名称
            channelName = appendInfo.getDetail();
            channelType = RadarChannelEnum.CUSTOMIZE.getTYPE();
        }
        // 设置点击时间
        Date time = new Date();
        String dateStr = DateUtils.getDate();
        // 获取活码发送用户详情
        WeRadarClickRecord record = WeRadarClickRecord.builder()
                .createTime(time)
                .openId(openId)
                .unionId(customer.getUnionid())
                .createDate(dateStr)
                .userId(user == null ? StringUtils.EMPTY : user.getUserId())
                .userName(user == null ? StringUtils.EMPTY : user.getName())
                .radarId(appendInfo.getRadarId())
                .externalUserId(customer.getExternalUserid())
                .externalUserName(customer.getName())
                .externalUserHeadImage(customer.getAvatar())
                .channelName(channelName)
                .channelType(channelType)
                .build();
        this.save(record);
        log.info("[保存雷达记录] 保存成功,record:{}", record);
    }




    /**
     * 校验参数
     *
     * @param radarAnalyseDTO
     */
    private void verifyParam(SearchRadarAnalyseDTO radarAnalyseDTO) {
        if (ObjectUtils.isEmpty(radarAnalyseDTO)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (ObjectUtils.isNotEmpty(radarAnalyseDTO.getBeginTime()) && ObjectUtils.isNotEmpty(radarAnalyseDTO.getEndTime())) {
            //校验开始时间和结束时间格式
            if (Boolean.TRUE.equals(!DateUtils.isMatchFormat(radarAnalyseDTO.getBeginTime(), DateUtils.YYYY_MM_DD)) || Boolean.TRUE.equals(!DateUtils.isMatchFormat(radarAnalyseDTO.getEndTime(), DateUtils.YYYY_MM_DD))) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
        }
    }
}
