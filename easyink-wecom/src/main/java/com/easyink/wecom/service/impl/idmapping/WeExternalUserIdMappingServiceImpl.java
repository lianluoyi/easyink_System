package com.easyink.wecom.service.impl.idmapping;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.GenConstants;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.domain.dto.CorpIdToOpenCorpIdResp;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;
import com.easyink.wecom.domain.model.externaluser.OpenExternalUserIdAndExternalUserIdModel;
import com.easyink.wecom.mapper.WeExternalUseridMappingMapper;
import com.easyink.wecom.service.idmapping.WeExternalUserIdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.easyink.common.constant.WeConstans.EXTERNAL_USER_ID_PREFIX;
import static com.easyink.common.constant.WeConstans.USER_ID_PREFIX;

/**
 * 类名: 客户id映射业务处理接口实现类
 *
 * @author : silver_chariot
 * @date : 2023/5/29 18:45
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class WeExternalUserIdMappingServiceImpl extends ServiceImpl<WeExternalUseridMappingMapper, WeExternalUseridMapping> implements WeExternalUserIdMappingService {

    private final WeUpdateIDClient weUpdateIDClient;
    private final WeExternalUseridMappingMapper weExternalUseridMappingMapper;
    private final RuoYiConfig ruoYiConfig;

    @Override
    public void buildAndSave(String corpId, Set<String> externalUserIdSet) {
        if (CollectionUtils.isEmpty(externalUserIdSet)) {
            return;
        }
        // 获取 映射表中已存在的externalUSERiD
        List<String> existExternalUserIds = list(new LambdaQueryWrapper<WeExternalUseridMapping>().eq(WeExternalUseridMapping::getCorpId, corpId)
                                                                                                  .in(WeExternalUseridMapping::getExternalUserid, externalUserIdSet)).stream()
                                                                                                                                                                     .map(WeExternalUseridMapping::getExternalUserid)
                                                                                                                                                                     .collect(Collectors.toList());
        // 获取数据库映射中不存在的userid
        List<String> newExternalUserIds = externalUserIdSet.stream()
                                                           .filter(extenalUserId -> !existExternalUserIds.contains(extenalUserId))
                                                           .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newExternalUserIds)) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[externalUserId映射获取] userId 映射数据库中都存在,不请求企微API corpId:{} , set:{}", corpId, externalUserIdSet);
            return;
        }
        callApiThenSave(corpId, newExternalUserIds);
    }

    /**
     * 请求api 并保存映射关系
     *
     * @param corpId 企微corpid
     * @param externalUserId externalUserId
     * @return 抓换后的密文externalUserId
     */
    private String callApiThenSave(String corpId, String externalUserId) {
        List<String> externalUserIds = new ArrayList<>();
        externalUserIds.add(externalUserId);
        List<WeExternalUseridMapping> weUserIdMappings = callApiThenSave(corpId, externalUserIds);
        if (CollectionUtils.isEmpty(weUserIdMappings)) {
            return StringUtils.EMPTY;
        }
        return weUserIdMappings.get(0).getOpenExternalUserid();
    }

    /**
     * 请求api 并保存映射关系
     *
     * @param corpId          企微corpid
     * @param externalUserIds externalUserIds 集合
     * @return 获取完的id 映射
     */
    private List<WeExternalUseridMapping> callApiThenSave(String corpId, List<String> externalUserIds) {
        if(StringUtils.isEmpty(corpId) || CollectionUtils.isEmpty(externalUserIds)) {
            return Collections.emptyList();
        }
        // 请求企微API , 明文-> 密文接口
        CorpIdToOpenCorpIdResp resp = weUpdateIDClient.getNewExternalUserid(corpId, externalUserIds);
        if (resp == null || CollectionUtils.isEmpty(resp.getItems())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[externalUserId映射获取] API 没有转换成功的external  userId,corpId:{} , externaluserid:{} ,resp :{}", corpId, externalUserIds, resp);
            return Collections.emptyList();
        }
        // 根据响应 构建映射实体并入库
        List<WeExternalUseridMapping> mappings = resp.getItems()
                                                     .stream()
                                                     .map(a -> new WeExternalUseridMapping(corpId, a))
                                                     .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(mappings)) {
            Integer res = weExternalUseridMappingMapper.batchInsertOrUpdate(mappings);
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[externalUserId映射获取]成功保存{}个external userId 映射关系,corpId:{}", res, corpId);
        }
        return mappings;
    }

    @Override
    public String getUserIdByOpenUserId(String corpId, String openExternalUserId) {
        //TODO 验收通过了 以后需要加上缓存
        if (StringUtils.isAnyBlank(corpId, openExternalUserId)) {
            return StringUtils.EMPTY;
        }
        // 判断是否是密文格式, 不是则不进行转换
        if (!isOpenExternalUserId(openExternalUserId)) {
            return openExternalUserId;
        }
        WeExternalUseridMapping mapping = getOne(new LambdaQueryWrapper<WeExternalUseridMapping>()
                .eq(WeExternalUseridMapping::getCorpId, corpId)
                .eq(WeExternalUseridMapping::getOpenExternalUserid, openExternalUserId)
                .last(GenConstants.LIMIT_1));
        if (mapping == null || StringUtils.isBlank(mapping.getExternalUserid())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[查询会话externalUserId转换] 映射表不存在明文映射,corpId:{}, openUserId:{}", corpId, openExternalUserId);
            return StringUtils.EMPTY;
        }
        // TODO 由于需要线上验收, 特此加日志,可后续删除
        log.info("[查询会话externalUserId转换] 通过映射表转换成功,corpId:{}, openUserId:{} , externalUserId: {}", corpId, openExternalUserId, mapping.getExternalUserid());
        return mapping.getExternalUserid();
    }

    @Override
    public String getOpenExternalUserIdByExternalUserId(String corpId, String externalUserId) {
        // 自建应用不转换
        if(ruoYiConfig.isInternalServer()) {
            return externalUserId;
        }
        if (StringUtils.isAnyBlank(corpId, externalUserId)) {
            return StringUtils.EMPTY;
        }
        // 如果是密文直接返回
        if (isOpenExternalUserId(externalUserId)) {
            return externalUserId;
        }
        WeExternalUseridMapping mapping = getOne(new LambdaQueryWrapper<WeExternalUseridMapping>()
                .eq(WeExternalUseridMapping::getCorpId, corpId)
                .eq(WeExternalUseridMapping::getExternalUserid, externalUserId)
                .last(GenConstants.LIMIT_1));
        if (mapping == null || StringUtils.isBlank(mapping.getOpenExternalUserid())) {
            // TODO 由于需要线上验收, 特此加日志,可后续删除
            log.info("[external userid 明转密] 映射表不存在密文映射, 直接请求api转换 corpId:{}, externalUserId:{}", corpId, externalUserId);
            return  callApiThenSave(corpId, externalUserId);
        }
        log.info("[external userid 明转密] 通过映射表转换成功,corpId:{}, externalUserId:{} , openExternalUserId: {}", corpId, externalUserId, mapping.getOpenExternalUserid());
        return mapping.getOpenExternalUserid();
    }

    @Override
    public void batchInsertOrUpdate(List<WeExternalUseridMapping> mappingList) {
        if(CollectionUtils.isEmpty(mappingList)){
            return;
        }
        this.weExternalUseridMappingMapper.batchInsertOrUpdate(mappingList);
    }

    @Override
    public void batchInsertOrUpdateThirdService(List<WeExternalUseridMapping> mappingList) {
        if(CollectionUtils.isEmpty(mappingList)){
            return;
        }
        this.weExternalUseridMappingMapper.batchInsertOrUpdateThirdService(mappingList);
    }

    @Override
    public List<OpenExternalUserIdAndExternalUserIdModel> getOpenExternalUserIdByExternalUserIdBatch(List<String> batchQueryList, String corpId) {
        if(CollectionUtils.isEmpty(batchQueryList) || StringUtils.isBlank(corpId)){
            return new ArrayList<>();
        }
        return weExternalUseridMappingMapper.selectOriginMappingByOpenExternalUserIdBatch(batchQueryList, corpId);
    }


    /**
     * 判断是否是加密的客户id
     *
     * @param openExternalUserId 加密的客户id
     * @return true or false
     */
    private boolean isOpenExternalUserId(String openExternalUserId) {
        return openExternalUserId.startsWith(USER_ID_PREFIX) || openExternalUserId.startsWith(EXTERNAL_USER_ID_PREFIX);
    }
}
