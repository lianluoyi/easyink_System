package com.easyink.wecom.service.impl.idmapping;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.domain.dto.CorpIdToOpenCorpIdResp;
import com.easyink.wecom.domain.entity.WeUserIdMapping;
import com.easyink.wecom.mapper.WeUserIdMappingMapper;
import com.easyink.wecom.service.idmapping.WeUserIdMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类名: userid明文密文映射业务接口实现类
 *
 * @author : silver_chariot
 * @date : 2023/5/23 13:47
 **/
@Slf4j
@Service
@AllArgsConstructor
public class WeUserIdMappingServiceImpl extends ServiceImpl<WeUserIdMappingMapper, WeUserIdMapping> implements WeUserIdMappingService {

    private final WeUpdateIDClient weUpdateIDClient;
    private final WeUserIdMappingMapper weUserIdMappingMapper;
    private final RuoYiConfig ruoYiConfig;


    @Override
    public void buildAndSaveUserIdMapping(String corpId, Set<String> userIdSet) {
        if (CollectionUtils.isEmpty(userIdSet)) {
            return;
        }
        // 过滤调不是
        // 获取 映射表中已存在的userId
        List<String> existUserIds = list(new LambdaQueryWrapper<WeUserIdMapping>().eq(WeUserIdMapping::getCorpId, corpId)
                                                                                  .in(WeUserIdMapping::getUserId, userIdSet)).stream()
                                                                                                                             .map(WeUserIdMapping::getUserId)
                                                                                                                             .collect(Collectors.toList());
        // 获取数据库映射中不存在的userid
        List<String> newUserIds = userIdSet.stream()
                                           .filter(userId -> !existUserIds.contains(userId))
                                           .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newUserIds)) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[userId映射获取] userId 映射数据库中都存在,不请求企微API corpId:{} , :{}", corpId, userIdSet);
            return;
        }
        callApiThenSave(corpId, newUserIds);
    }

    /**
     * 请求api 并保存映射关系
     *
     * @param corpId 企微corpid
     * @param userId userid
     * @return 抓换后的密文userid
     */
    private String callApiThenSave(String corpId, String userId) {
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        List<WeUserIdMapping> weUserIdMappings = callApiThenSave(corpId, userIds);
        if (CollectionUtils.isEmpty(weUserIdMappings)) {
            return StringUtils.EMPTY;
        }
        return weUserIdMappings.get(0).getOpenUserId();
    }

    /**
     * 请求api 并保存映射关系
     *
     * @param corpId  企微corpid
     * @param userIds userid 集合
     * @return 获取完的id 映射
     */
    private List<WeUserIdMapping> callApiThenSave(String corpId, List<String> userIds) {
        if (StringUtils.isAnyBlank(corpId) || CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        // 请求企微API , 明文-> 密文接口
        CorpIdToOpenCorpIdResp resp = weUpdateIDClient.getUserIdMapping(corpId, userIds);
        if (resp == null || CollectionUtils.isEmpty(resp.getOpen_userid_list())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[userId映射获取] API 没有转换成功的userId,corpId:{} , userid:{} ,resp :{}", corpId, userIds, resp);
            return Collections.emptyList();
        }
        // 根据响应 构建映射实体并入库
        List<WeUserIdMapping> mappingList = resp.getOpen_userid_list()
                                                .stream()
                                                .map(a -> new WeUserIdMapping(corpId, a))
                                                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(mappingList)) {
            Integer res = weUserIdMappingMapper.batchInsertOrUpdate(mappingList);
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[userId映射获取]成功保存{}个userId 映射关系,corpId:{}", res, corpId);
        }
        return mappingList;
    }

    @Override
    public String getUserIdByOpenUserId(String corpId, String openUserId) {
        if (StringUtils.isAnyBlank(corpId, openUserId)) {
            return StringUtils.EMPTY;
        }
        // 判断是否是密文格式, 不是则不进行转换
        if (!isOpenUserId(openUserId)) {
            return openUserId;
        }
        WeUserIdMapping mapping = getOne(new LambdaQueryWrapper<WeUserIdMapping>()
                .eq(WeUserIdMapping::getCorpId, corpId)
                .eq(WeUserIdMapping::getOpenUserId, openUserId)
                .last(GenConstants.LIMIT_1));
        if (mapping == null || StringUtils.isBlank(mapping.getUserId())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[查询会话userId转换] 映射表不存在明文映射, corpId:{}, openUserId:{}", corpId, openUserId);
            return StringUtils.EMPTY;
        }
        // TODO 由于需要线上验收, 特此加日志,可后续删除
        log.info("[查询会话userId转换] 通过映射表转换成功,corpId:{}, openUserId:{} , userId: {}", corpId, openUserId, mapping.getUserId());
        return mapping.getUserId();
    }
    @Override
    public String getOpenUserIdByUserId(String corpId, String userId) {
        // 自建应用不转换
        if(ruoYiConfig.isInternalServer()) {
            return userId;
        }
        if (StringUtils.isAnyBlank(corpId, userId)) {
            return StringUtils.EMPTY;
        }
        // 如果是密文直接返回
        if (isOpenUserId(userId)) {
            return userId;
        }
        WeUserIdMapping mapping = getOne(new LambdaQueryWrapper<WeUserIdMapping>()
                .eq(WeUserIdMapping::getCorpId, corpId)
                .eq(WeUserIdMapping::getUserId, userId)
                .last(GenConstants.LIMIT_1));
        if (mapping == null || StringUtils.isBlank(mapping.getOpenUserId())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[userid 明转密] 映射表不存在密文映射, 直接请求api转换 corpId:{}, userId:{}", corpId, userId);
            return callApiThenSave(corpId, userId);
        }
        log.info("[userid 明转密] 通过映射表转换成功,corpId:{}, userId:{} , userId: {}", corpId, userId, mapping.getUserId());
        return mapping.getOpenUserId();
    }

    @Override
    public List<String> transferUserIdsForDK(String corpId, List<String> userIds) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIds) || ruoYiConfig.isInternalServer()) {
            return userIds;
        }
        // 获取 映射表中已存在的userId
        return list(new LambdaQueryWrapper<WeUserIdMapping>().eq(WeUserIdMapping::getCorpId, corpId)
                                                                                  .in(WeUserIdMapping::getOpenUserId, userIds)).stream()
                                                                                                                               .map(WeUserIdMapping::getUserId)
                                                                                                                               .collect(Collectors.toList());

    }

    @Override
    public void batchInsertOrUpdate(List<WeUserIdMapping> mappingList) {
        if(CollectionUtils.isEmpty(mappingList)){
            return;
        }

        this.weUserIdMappingMapper.batchInsertOrUpdate(mappingList);
    }

    @Override
    public void batchInsertOrUpdateThirdService(List<WeUserIdMapping> mappingList) {
        if(CollectionUtils.isEmpty(mappingList)){
            return;
        }
        this.weUserIdMappingMapper.batchInsertOrUpdateThirdService(mappingList);
    }


    /**
     * 判断是否是密文userId
     *
     * @param openUserId userid
     * @return true or false
     */
    private boolean isOpenUserId(String openUserId) {
        return openUserId.startsWith(WeConstans.USER_ID_PREFIX);
    }


}
