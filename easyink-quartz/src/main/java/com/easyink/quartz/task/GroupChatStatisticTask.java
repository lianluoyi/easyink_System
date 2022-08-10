package com.easyink.quartz.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.easyink.common.constant.GroupConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.utils.MyDateUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupStatistic;
import com.easyink.wecom.domain.dto.GroupChatStatisticDTO;
import com.easyink.wecom.domain.query.GroupChatStatisticQuery;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeGroupService;
import com.easyink.wecom.service.WeGroupStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @description 群聊数据统计
 * @date 2021/2/24 0:42
 **/
@Slf4j
@Component("GroupChatStatisticTask")
public class GroupChatStatisticTask {

    private final WeCustomerClient weCustomerClient;

    private final WeGroupService weGroupService;

    private final WeGroupStatisticService weGroupStatisticService;

    private final WeCorpAccountService weCorpAccountService;

    @Autowired
    public GroupChatStatisticTask(WeCustomerClient weCustomerClient, WeGroupService weGroupService, WeGroupStatisticService weGroupStatisticService, WeCorpAccountService weCorpAccountService) {
        this.weCustomerClient = weCustomerClient;
        this.weGroupService = weGroupService;
        this.weGroupStatisticService = weGroupStatisticService;
        this.weCorpAccountService = weCorpAccountService;

    }

    private static final int OFFSET = 500;


    public void getGroupChatData() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                processGroupChatData(weCorpAccount.getCorpId());
            }
        });

    }

    @Transactional(rollbackFor = Exception.class)
    void processGroupChatData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }

        //删除本日原先的数据
        LambdaQueryWrapper<WeGroupStatistic> wrapper = new LambdaQueryWrapper<>();
        Long startTime = MyDateUtil.strToDate(-1, 0);
        Long endTime = MyDateUtil.strToDate(-1, 1);
        wrapper.eq(WeGroupStatistic::getCorpId, corpId);
        wrapper.between(WeGroupStatistic::getStatTime, DateUtil.date(startTime * 1000), DateUtil.date(endTime * 1000));
        int count = weGroupStatisticService.count(wrapper);
        //删除所有数据
        if (count > 0) {
            weGroupStatisticService.remove(wrapper);
        }

        //判断是否大于500，判断是否分批处理
        int weGroupCount = weGroupService.count(
                new LambdaQueryWrapper<WeGroup>()
                        .eq(WeGroup::getCorpId, corpId)
        );
        //定义批量操作
        double num = 1;
        if (weGroupCount > OFFSET) {
            num = Math.ceil((double) weGroupCount / OFFSET);
        }
        int temp = 0;
        List<Integer> list = new ArrayList<>(2);
        list.add(GroupConstants.OWNER_LEAVE);
        list.add(GroupConstants.OWNER_LEAVE_EXTEND);
        for (int i = 0; i < num; i++) {
            QueryWrapper<WeGroup> wrapper1 = new QueryWrapper<>();
            wrapper1.select("DISTINCT owner");
            wrapper1.eq("corp_id", corpId);
            wrapper1.notIn("status", list);
            wrapper1.last("limit " + temp + "," + OFFSET);
            List<WeGroup> weGroupList = weGroupService.list(wrapper1);

            if (CollUtil.isNotEmpty(weGroupList)) {
                List<WeGroupStatistic> weGroupStatisticList = new ArrayList<>();
                GroupChatStatisticQuery query = new GroupChatStatisticQuery();
                //前一天的数据
                query.setDay_begin_time(startTime);
                query.setDay_end_time(endTime);

                weGroupList.forEach(weGroup -> {

                    GroupChatStatisticQuery.OwnerFilter ownerFilter = new GroupChatStatisticQuery.OwnerFilter();
                    List<String> idList = new ArrayList<>();
                    idList.add(weGroup.getOwner());
                    ownerFilter.setUserid_list(idList);
                    query.setOwner_filter(ownerFilter);
                    try {
                        //状态不是离职继承中的，离职继承中的群聊无法查询
                        //根据群主获取群聊概述
                        GroupChatStatisticDTO groupChatStatistic = weCustomerClient.getGroupChatStatistic(query, corpId);
                        List<GroupChatStatisticDTO.GroupchatStatisticData> items = groupChatStatistic.getItems();
                        if (CollUtil.isNotEmpty(items)) {
                            items.forEach(groupChatStatisticData -> {
                                WeGroupStatistic weGroupStatistic = new WeGroupStatistic();
                                GroupChatStatisticDTO.StatisticData data = groupChatStatisticData.getData();
                                BeanUtils.copyPropertiesignoreOther(data, weGroupStatistic);
                                weGroupStatistic.setChatId(groupChatStatisticData.getOwner());
                                //返回数据不包含时间，所以使用开始时间做stat_time
                                weGroupStatistic.setStatTime(DateUtil.date(startTime * 1000));
                                weGroupStatistic.setCorpId(corpId);
                                weGroupStatisticList.add(weGroupStatistic);
                            });
                        }
                    } catch (Exception e) {
                        log.error("群聊数据拉取失败: ownerFilter:【{}】,ex:【{}】", JSON.toJSONString(ownerFilter), ExceptionUtils.getStackTrace(e));
                    }
                });


                weGroupStatisticService.saveBatch(weGroupStatisticList);
            }

            temp += OFFSET;
        }
    }
}
