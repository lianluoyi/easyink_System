package com.easyink.quartz.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.utils.MyDateUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.UserBehaviorDataDTO;
import com.easyink.wecom.domain.query.UserBehaviorDataQuery;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeDepartmentService;
import com.easyink.wecom.service.WeUserBehaviorDataService;
import com.easyink.wecom.service.WeUserService;
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

    private final WeUserService weUserService;

    @Autowired
    public UserBehaviorDataTak(WeCustomerClient weCustomerClient, WeUserBehaviorDataService weUserBehaviorDataService, WeUserClient weUserClient, WeCorpAccountService weCorpAccountService, WeDepartmentService weDepartmentService, WeUserService weUserService) {
        this.weCustomerClient = weCustomerClient;
        this.weUserBehaviorDataService = weUserBehaviorDataService;
        this.weUserClient = weUserClient;
        this.weCorpAccountService = weCorpAccountService;
        this.weDepartmentService = weDepartmentService;
        this.weUserService = weUserService;
    }

    public void getUserBehaviorData() {
        log.info("定时任务开始执行------>");
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        weCorpAccountList.forEach(weCorpAccount -> {
            try {
                if (weCorpAccount != null && StringUtils.isNotBlank(weCorpAccount.getCorpId())) {
                    getUserBehaviorDataByCorpId(weCorpAccount.getCorpId());
                }
            } catch (Exception e) {
                log.error("[获取用户数据]统计客户任务异常,corpid:{},e:{}", weCorpAccount.getCorpId(), ExceptionUtils.getStackTrace(e));
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
        // 获取根员工
        List<WeUser> visibleUser = weUserService.getVisibleUser(corpId);
        if (CollectionUtils.isEmpty(visibleUser)) {
            log.info("[UserBehaviorDataTak] 该企业不存在可见的部门和员工,停止执行,corpId:{}", corpId);
            return;
        }
        //删除存在的数据
        Long startTime = MyDateUtil.strToDate(-1, 0);
        Long endTime = MyDateUtil.strToDate(-1, 1);
        LambdaQueryWrapper<WeUserBehaviorData> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(WeUserBehaviorData::getCorpId, corpId);
        wrapper1.between(WeUserBehaviorData::getStatTime, DateUtil.date(startTime * 1000), DateUtil.date(endTime * 1000));
        int count = weUserBehaviorDataService.count(wrapper1);
        if (count > 0) {
            weUserBehaviorDataService.remove(wrapper1);
        }

        List<WeUserBehaviorData> dataList = new ArrayList<>();
        UserBehaviorDataQuery query = new UserBehaviorDataQuery();
        //前一天的数据
        query.setStart_time(startTime);
        query.setEnd_time(endTime);
        visibleUser.forEach(weUser -> {
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
