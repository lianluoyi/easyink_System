package com.easywecom.quartz.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.utils.MyDateUtil;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.client.WeCustomerClient;
import com.easywecom.wecom.client.WeUserClient;
import com.easywecom.wecom.domain.WeUserBehaviorData;
import com.easywecom.wecom.domain.dto.UserBehaviorDataDTO;
import com.easywecom.wecom.domain.query.UserBehaviorDataQuery;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeDepartmentService;
import com.easywecom.wecom.service.WeUserBehaviorDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @description 联系客户统计
 * @date 2021/2/24 0:41
 **/
@Slf4j
@Component("UserBehaviorDataTak")
public class UserBehaviorDataTak {

    private final WeCustomerClient weCustomerClient;

    private final WeUserBehaviorDataService weUserBehaviorDataService;

    private final WeUserClient weUserClient;

    private final WeCorpAccountService weCorpAccountService;
    private final WeDepartmentService weDepartmentService;

    @Autowired
    public UserBehaviorDataTak(WeCustomerClient weCustomerClient, WeUserBehaviorDataService weUserBehaviorDataService, WeUserClient weUserClient, WeCorpAccountService weCorpAccountService, WeDepartmentService weDepartmentService) {
        this.weCustomerClient = weCustomerClient;
        this.weUserBehaviorDataService = weUserBehaviorDataService;
        this.weUserClient = weUserClient;
        this.weCorpAccountService = weCorpAccountService;
        this.weDepartmentService = weDepartmentService;
    }

    public void getUserBehaviorData() {
        log.info("定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                getUserBehaviorDataByCorpId(weCorpAccount.getCorpId());
            }
        });
        log.info("定时任务执行完成------>");
    }

    @Transactional(rollbackFor = Exception.class)
    void getUserBehaviorDataByCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            log.error("corpId不允许为空。");
            return;
        }
        // 获取根部门
        List<Long> visibleRoots = weDepartmentService.getVisibleRootDepartment(corpId);
        if(CollectionUtils.isEmpty(visibleRoots)) {
            log.error("同步成员,找不到根部门，停止同步,corpID:{}",corpId);
            return;
        }
        List<WeUser > weUsers= new ArrayList<>();
        for(Long department : visibleRoots) {
            List<WeUser> tempList = weUserClient.simpleList(department, WeConstans.DEPARTMENT_SUB_WEUSER, corpId).getWeUsers();
            if(CollectionUtils.isNotEmpty(tempList) ) {
                weUsers.addAll(tempList);
            }
        }
        //删除存在的数据
        if (CollUtil.isNotEmpty(weUsers)) {
            Long startTime = MyDateUtil.strToDate(-1, 0);
            Long endTime = MyDateUtil.strToDate(-1, 1);
            LambdaQueryWrapper<WeUserBehaviorData> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(WeUserBehaviorData::getCorpId, corpId);
            wrapper1.between(WeUserBehaviorData::getStatTime, DateUtil.date(startTime * 1000), DateUtil.date(endTime * 1000));
            int count = weUserBehaviorDataService.count(wrapper1);
            if (count > 0) {
                weUserBehaviorDataService.remove(wrapper1);
            }

            if (CollUtil.isNotEmpty(weUsers)) {
                List<WeUserBehaviorData> dataList = new ArrayList<>();
                UserBehaviorDataQuery query = new UserBehaviorDataQuery();
                //前一天的数据
                query.setStart_time(startTime);
                query.setEnd_time(endTime);
                weUsers.forEach(weUser -> {
                    List<String> idList = new ArrayList<>();
                    idList.add(weUser.getUserId());
                    query.setUserid(idList);
                    try {
                        //根据员工id获取员工的数据概览
                        UserBehaviorDataDTO userBehaviorData = weCustomerClient.getUserBehaviorData(query, corpId);
                        List<UserBehaviorDataDTO.BehaviorData> behaviorDataList = userBehaviorData.getBehaviorData();
                        for (UserBehaviorDataDTO.BehaviorData data : behaviorDataList) {
                            WeUserBehaviorData weUserBehaviorData = new WeUserBehaviorData();
                            BeanUtils.copyPropertiesignoreOther(data, weUserBehaviorData);
                            weUserBehaviorData.setUserId(weUser.getUserId());
                            weUserBehaviorData.setCorpId(corpId);
                            dataList.add(weUserBehaviorData);
                        }
                    } catch (ForestRuntimeException e) {
                        log.error("员工数据拉取失败: corpId:{},userId:【{}】,ex:【{}】", corpId, weUser.getUserId(), ExceptionUtils.getStackTrace(e));
                    }
                });
                weUserBehaviorDataService.saveBatch(dataList);
            }
        }
    }
}
