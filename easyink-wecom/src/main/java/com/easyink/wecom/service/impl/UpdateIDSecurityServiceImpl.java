package com.easyink.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.ExceptionUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.CorpIdToOpenCorpIdResp;
import com.easyink.wecom.domain.entity.WeCorpUpdateId;
import com.easyink.wecom.domain.entity.WeCorpUpdateIdDataTable;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.service.UpdateIDSecurityService;
import com.easyink.wecom.service.UpdateUserIdService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.easyink.common.constant.GenConstants.LIMIT_1;
import static com.easyink.common.constant.WeCorpUpdateIdConstants.*;

/**
 * ClassName： UpdateIDSecurityServiceImpl
 *
 * @author wx
 * @date 2022/8/19 9:32
 */
@Service
@Slf4j
public class UpdateIDSecurityServiceImpl extends ServiceImpl<UpdateIDSecurityMapper, WeCorpUpdateId> implements UpdateIDSecurityService {
    final private UpdateIDSecurityMapper updateIDSecurityMapper;

    final private WeUpdateIDClient weUpdateIDClient;

    final private WeUserService weUserService;

    final private WeCustomerMapper weCustomerMapper;

    final private UpdateUserIdService updateUserIdService;

    final private WeCorpAccountService weCorpAccountService;

    final private RedisCache redisCache;

    final private WeAuthCorpInfoMapper weAuthCorpInfoMapper;

    final private RuoYiConfig ruoYiConfig;

    public UpdateIDSecurityServiceImpl(UpdateIDSecurityMapper updateIDSecurityMapper, WeUpdateIDClient weUpdateIDClient, WeUserService weUserService, WeCustomerMapper weCustomerMapper, UpdateUserIdService updateUserIdService, WeCorpAccountService weCorpAccountService, RedisCache redisCache, WeAuthCorpInfoMapper weAuthCorpInfoMapper, RuoYiConfig ruoYiConfig) {
        this.updateIDSecurityMapper = updateIDSecurityMapper;
        this.weUpdateIDClient = weUpdateIDClient;
        this.weUserService = weUserService;
        this.weCustomerMapper = weCustomerMapper;
        this.updateUserIdService = updateUserIdService;
        this.weCorpAccountService = weCorpAccountService;
        this.redisCache = redisCache;
        this.weAuthCorpInfoMapper = weAuthCorpInfoMapper;
        this.ruoYiConfig = ruoYiConfig;
    }

    /**
     * 单独企业升级处理
     *
     * @param corpId
     * @param agentId
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.NESTED)
    public void singleCorpHandle(String corpId, String agentId) {
        if (StringUtils.isAnyBlank(corpId, agentId)) {
            return;
        }
        final List<WeCorpUpdateIdDataTable> corpIdTableList = getDataTableList(GET_CORP_ID_LIST);
        final List<WeCorpUpdateIdDataTable> userIdTableList = getDataTableList(GET_USER_ID_LIST);
        final List<WeCorpUpdateIdDataTable> externalUserIdList = getDataTableList(GET_EXTERNAL_USER_ID_LIST);

        //开始处理
        Date startTime = new Date();
        String startTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, startTime);
        log.info("[授权企业ID安全性升级] 开始升级企业，corpId:{},开始时间：{}", corpId, startTimeStr);

        //获取openCorpId
        String openCorpId = weUpdateIDClient.getOpenCorpId(corpId).getOpen_corpid();

        //获取open_userIdList
        final List<String> users = weUserService.list(new LambdaQueryWrapper<WeUser>().eq(WeUser::getCorpId, corpId)).stream().map(WeUser::getUserId).collect(Collectors.toList());
        //userId：openUserId
        Map<String,String> openUserIdMap = weUpdateIDClient.getUserIdMapping(corpId, users).getOpen_userid_list().stream().collect(Collectors.toMap(CorpIdToOpenCorpIdResp.UserIdMapping::getUserid, CorpIdToOpenCorpIdResp.UserIdMapping::getOpen_userid));

        //获取open_external_userIdList
        final List<WeCustomer> weCustomers = weCustomerMapper.selectList(new LambdaQueryWrapper<WeCustomer>().eq(WeCustomer::getCorpId, corpId));
        final CorpIdToOpenCorpIdResp newExternalUser = weUpdateIDClient.getNewExternalUserid(corpId, weCustomers.stream().map(WeCustomer::getExternalUserid).collect(Collectors.toList()));
        //external_userId:openExternalUserId
        Map<String,String> openExternalUserIdMap = newExternalUser.getItems().stream().collect(Collectors.toMap(CorpIdToOpenCorpIdResp.ExternalUserMapping::getExternal_userid, CorpIdToOpenCorpIdResp.ExternalUserMapping::getNew_external_userid));

        //更新userId
        for(Map.Entry<String,String> userId : openUserIdMap.entrySet()){
            for(WeCorpUpdateIdDataTable item : userIdTableList){
                try {
                    updateIDSecurityMapper.updateUserIdAndExternalUserId(item, corpId, userId.getKey(), userId.getValue());
                } catch (DuplicateKeyException e) {
                    log.info("[授权企业ID安全性升级] 升级userId时，{}表，corpId:{},openCorpId:{},userId:{},openUserId:{},出现{}",item.getUpdateTableName(),corpId, openCorpId, userId.getKey(), userId.getValue(), ExceptionUtil.getExceptionMessage(e));
                }
            }
        }
        //处理userId用','连接的字段
        updateUserIdService.updateSOPFilterUserIds(openUserIdMap, corpId);
        updateUserIdService.updateMomentTaskUserIds(openUserIdMap, corpId);
        updateUserIdService.updateCustomerOriginal(openUserIdMap, openExternalUserIdMap, corpId);
        log.info("[授权企业ID安全性升级] userId升级成功, 更新了{}个userId",openUserIdMap.size());

        //更新external_userid
        for(Map.Entry<String,String> externalUserId : openExternalUserIdMap.entrySet()){
            for(WeCorpUpdateIdDataTable item: externalUserIdList){
                try{
                    updateIDSecurityMapper.updateUserIdAndExternalUserId(item, corpId, externalUserId.getKey(), externalUserId.getValue());
                } catch (DuplicateKeyException e) {
                    log.info("[授权企业ID安全性升级] 升级external_userId时，{}表，corpId:{},openCorpId:{},externalUserId:{},openExternalUserId:{},出现{}",item.getUpdateTableName(),corpId, openCorpId, externalUserId.getKey(), externalUserId.getValue(), ExceptionUtil.getExceptionMessage(e));
                }
            }
        }
        log.info("[授权企业ID安全性升级] external_id升级成功, 更新了{}个external_id",openExternalUserIdMap.size());

        //企业有多个待开发应用，所以需要单独更新当前使用的待开发应用id对应的corpId
        final WeAuthCorpInfo weAuthCorpInfo = weAuthCorpInfoMapper.selectOne(new LambdaQueryWrapper<WeAuthCorpInfo>()
                .eq(WeAuthCorpInfo::getCorpId, corpId)
                .eq(WeAuthCorpInfo::getSuiteId, ruoYiConfig.getProvider().getDkSuite().getDkId()));
        if(weAuthCorpInfo != null){
            try {
                weAuthCorpInfo.setCorpId(openCorpId);
                weAuthCorpInfoMapper.update(weAuthCorpInfo,new LambdaQueryWrapper<WeAuthCorpInfo>()
                        .eq(WeAuthCorpInfo::getCorpId, corpId)
                        .eq(WeAuthCorpInfo::getSuiteId, ruoYiConfig.getProvider().getDkSuite().getDkId()));
            }catch (DuplicateKeyException e) {
                log.info("[授权企业ID安全性升级] 升级we_auth_corp_info表时，已存在正确的密文corpId和suiteId,故不做处理");
            }
        }
        for(WeCorpUpdateIdDataTable item : corpIdTableList) {
            try {
                //删除数据库中存储密文oenCorpId的数据
                updateIDSecurityMapper.removeOpenCorpId(item.getUpdateTableName(), openCorpId);
                //更新数据库中存储明文corpId -> 密文openCorpId
                updateIDSecurityMapper.updateCorpId(item.getUpdateTableName(), openCorpId, corpId);
            } catch (DuplicateKeyException e) {
                log.info("[授权企业ID安全性升级] 升级corpId时，{}表，出现{}",item.getUpdateTableName(),ExceptionUtil.getExceptionMessage(e));
            }
        }
        log.info("[授权企业ID安全性升级] corpId升级成功, 原corpId为：{}, 现corpId:{}",corpId, openCorpId);


        //完成迁移
        weUpdateIDClient.finishOpenIdMigration(corpId, agentId, new Integer[]{MIGRATION_USER_ID_AND_CORP_ID,MIGRATION_EXTERNAL_USER_ID});
        Date endTime = new Date();
        String endTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endTime);
        String updateLog = "升级成功，耗时为:" + (endTime.getTime() - startTime.getTime()) + "ms";
        final WeCorpUpdateId weCorpUpdateId = new WeCorpUpdateId(corpId, openCorpId, openUserIdMap.size(), openExternalUserIdMap.size(), updateLog, startTimeStr, endTimeStr);
        updateIDSecurityMapper.insertOrUpdate(weCorpUpdateId);
        //移除该企业corpId相关Cache
        removeCorpRedisCache(corpId);
    }

    /**
     * 企业id安全性升级
     *
     * @param enableFullUpdate
     * @param corpId
     */
    @Override
    public void corpIdHandle(Boolean enableFullUpdate, String corpId) {
        UpdateIDSecurityServiceImpl bean = SpringUtils.getBean("updateIDSecurityServiceImpl");
        if (enableFullUpdate == null || (
                enableFullUpdate && StringUtils.isNotBlank(corpId))) {
            if(corpId.startsWith(CORP_START_WITH)){
                log.info("[授权企业ID安全性升级] corpId：{} 为密文不处理", corpId);
                return;
            }
            final WeCorpAccount account = SpringUtils.getBean(WeCorpAccountMapper.class).selectOne(new LambdaQueryWrapper<WeCorpAccount>().eq(WeCorpAccount::getCorpId, corpId).last(LIMIT_1));
            if (account == null) {
                log.info("[授权企业ID安全性升级] 不存在上述corpId：{} 的企业", corpId);
                return;
            }
            try {
                bean.singleCorpHandle(corpId, account.getAgentId());
            } catch (Exception e) {
                Date endTime = new Date();
                String endTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endTime);
                log.info("[授权企业ID安全性升级] corpId：{}升级失败", corpId);
                final WeCorpUpdateId weCorpUpdateId = new WeCorpUpdateId(corpId, StringUtils.EMPTY, ZERO_NUM, ZERO_NUM, UPDATE_CORP_FAIL + ExceptionUtil.getExceptionMessage(e), EMPTY_TIME, endTimeStr);
                updateIDSecurityMapper.insertOrUpdate(weCorpUpdateId);
            }
        }
    }

    /**
     * 拼装单个表数据
     *
     *
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名|条件
     * 要更新的表|修改的列名
     * 要更新的表|修改的列名|条件
     *
     * @param table
     * @return
     */
    public static WeCorpUpdateIdDataTable getDataTable(String table){
        //按照以上注释将对应字段分隔写入到实体
        /**
         * 数组长度
         * 数组长度为5，6时说明需要关联一个待corp的主表
         * 数组长度为2，3时说明corp在要更新的表中
         */
        Integer arrLengthIs2 = 2;
        Integer arrLengthIs3 = 3;
        Integer arrLengthIs5 = 5;
        Integer arrLengthIs6 = 6;

        //数组中表示要 更新的表 的数组下标
        int updateTableIndex = 0;
        //数组中表示要 带corp_id的主表 的数组下标
        int relTableIndex = 1;
        //数组长度小于4 要修改的列名的数组下标
        int arrLe4ColumnIndex = 1;
        //数组中表示 要更新的表关联外键名的数组下标
        int updateTableKeyIndex = 2;
        //数组中表示 带corp_id的主表关联的外键 的数组下标
        int relTableKeyIndex = 3;
        //数组长度大于4 要修改的列名 的数组下标
        int arrGe4ColumnIndex = 4;
        //数组长度为3时 表示条件的数组下标
        int arrLengthIs3CriterionIndex = 2;
        //数组长度为6时 表示条件的数组下标
        int arrLengthIs6CriterionIndex = 5;

        final String[] userIdFormat = table.split(StrUtil.COMMA);
        if(arrLengthIs2.equals(userIdFormat.length)){
            return new WeCorpUpdateIdDataTable(userIdFormat[updateTableIndex], userIdFormat[arrLe4ColumnIndex]);
        } else if (arrLengthIs3.equals(userIdFormat.length)) {
            return new WeCorpUpdateIdDataTable(userIdFormat[updateTableIndex], userIdFormat[arrLe4ColumnIndex], userIdFormat[arrLengthIs3CriterionIndex]);
        } else if (arrLengthIs5.equals(userIdFormat.length)) {
            return new WeCorpUpdateIdDataTable(userIdFormat[updateTableIndex], userIdFormat[relTableIndex], userIdFormat[updateTableKeyIndex], userIdFormat[relTableKeyIndex], userIdFormat[arrGe4ColumnIndex]);
        } else if (arrLengthIs6.equals(userIdFormat.length)) {
            return new WeCorpUpdateIdDataTable(userIdFormat[updateTableIndex], userIdFormat[relTableIndex], userIdFormat[updateTableKeyIndex], userIdFormat[relTableKeyIndex], userIdFormat[arrGe4ColumnIndex], userIdFormat[arrLengthIs6CriterionIndex]);
        }
        return null;
    }

    /**
     * 获取需要更新的表数据
     *
     * @param targetList
     * @return
     */
    public static List<WeCorpUpdateIdDataTable> getDataTableList(String targetList){
        if(StringUtils.isBlank(targetList)){
            return Collections.emptyList();
        }
        List<WeCorpUpdateIdDataTable> list = new ArrayList<>();
        if (GET_CORP_ID_LIST.equals(targetList)) {
            List<String> corpIdList = Arrays.asList(CORP_ID_LIST.replace(" ","").replace("\n","").split("-"));
            corpIdList.forEach(item->{
                if(StringUtils.isNotBlank(item)){
                    list.add(new WeCorpUpdateIdDataTable(item));
                }
            });
        } else if (GET_USER_ID_LIST.equals(targetList)) {
            final List<String> userIdList = Arrays.asList(USER_ID_LIST.replace(" ", "").replace("\n", "").split("-"));
            userIdList.forEach(item->{
                final WeCorpUpdateIdDataTable dataTable = getDataTable(item);
                if( dataTable != null){
                    list.add(dataTable);
                }
            });
        } else if(GET_EXTERNAL_USER_ID_LIST.equals(targetList)){
            final List<String> externalUserIdList = Arrays.asList(EXTERNAL_USER_ID_LIST.replace(" ", "").replace("\n", "").split("-"));
            externalUserIdList.forEach(item->{
                final WeCorpUpdateIdDataTable dataTable = getDataTable(item);
                if( dataTable != null){
                    list.add(dataTable);
                }
            });
        }
        return list;
    }

    /**
     * 移除corpId相关缓存
     *
     * @param corpId
     */
    @Override
    public void removeCorpRedisCache(String corpId) {
        //获取登录token
        Collection<String> keys = redisCache.scans(Constants.LOGIN_TOKEN_KEY + "*");
        for (String key : keys) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            try {
                LoginUser user = redisCache.getCacheObject(key);
                if (corpId.equals(user.getCorpId())) {
                    redisCache.deleteObject(key);
                }
            } catch (CustomException e) {
                //此处捕获获取corpId抛出的异常
            }
        }
        redisCache.deleteObject(WeConstans.WE_CORP_ACCOUNT + ":" + corpId);
        redisCache.deleteObject(RedisKeyConstants.CORP_BASIC_DATA + corpId);
        redisCache.deleteObject(RedisKeyConstants.CORP_REAL_TIME + corpId);
        redisCache.deleteObject(WeConstans.WE_AUTH_CORP_ACCESS_TOKEN + corpId);
        redisCache.deleteObject(WeConstans.WE_COMMON_ACCESS_TOKEN + ":" + corpId);
        redisCache.deleteObject(WeConstans.WE_CONTACT_ACCESS_TOKEN + ":" + corpId);
    }
}
