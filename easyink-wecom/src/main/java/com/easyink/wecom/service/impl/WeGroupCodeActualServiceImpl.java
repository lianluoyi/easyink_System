package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GroupCodeConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeGroupChatJoinClient;
import com.easyink.wecom.domain.WeGroupCodeActual;
import com.easyink.wecom.domain.dto.group.*;
import com.easyink.wecom.domain.vo.WeGroupCodeActualExistVO;
import com.easyink.wecom.mapper.WeGroupCodeActualMapper;
import com.easyink.wecom.service.We3rdAppService;
import com.easyink.wecom.service.WeGroupCodeActualService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名： 实际群码Service业务层处理
 *
 * @author 佚名
 * @date 2021/11/11 12:15
 */
@Service
public class WeGroupCodeActualServiceImpl extends ServiceImpl<WeGroupCodeActualMapper, WeGroupCodeActual> implements WeGroupCodeActualService {
    @Autowired
    private WeGroupCodeActualMapper weGroupCodeActualMapper;
    @Autowired
    private We3rdAppService we3rdAppService;
    @Autowired
    private WeGroupChatJoinClient weGroupChatJoinClient;

    /**
     * 查询实际群码
     *
     * @param id 实际群码ID
     * @return 实际群码
     */
    @Override
    public WeGroupCodeActual selectWeGroupCodeActualById(Long id) {
        return weGroupCodeActualMapper.selectWeGroupCodeActualById(id);
    }

    /**
     * 根据群聊id获取对应群二维码
     *
     * @param chatId 群聊id
     * @return 结果
     */
    @Override
    public WeGroupCodeActual selectActualCodeByChatId(String chatId) {
        return weGroupCodeActualMapper.selectWeGroupCodeActualByChatId(chatId);
    }

    /**
     * 查询实际群码列表
     *
     * @param weGroupCodeActual 实际群码
     * @return 实际群码
     */
    @Override
    public List<WeGroupCodeActual> selectWeGroupCodeActualList(WeGroupCodeActual weGroupCodeActual) {
        return weGroupCodeActualMapper.selectWeGroupCodeActualList(weGroupCodeActual);
    }

    /**
     * 新增实际群码
     *
     * @param weGroupCodeActual 实际群码
     * @return 结果
     */
    @Override
    public int insertWeGroupCodeActual(WeGroupCodeActual weGroupCodeActual) {
        buildActualCode(weGroupCodeActual);
        return weGroupCodeActualMapper.insertWeGroupCodeActual(weGroupCodeActual);
    }

    /**
     * 修改实际群码
     *
     * @param weGroupCodeActual 实际群码
     * @return 结果
     */
    @Override
    public int updateWeGroupCodeActual(WeGroupCodeActual weGroupCodeActual) {
        buildActualCode(weGroupCodeActual);
        return this.baseMapper.updateWeGroupCodeActual(weGroupCodeActual);
    }

    /**
     * 批量删除实际群码
     *
     * @param ids 需要删除的实际群码ID
     * @return 结果
     */
    @Override
    public int deleteWeGroupCodeActualByIds(Long[] ids) {
        return weGroupCodeActualMapper.deleteWeGroupCodeActualByIds(ids);
    }

    /**
     * 检测实际码chatId是否唯一
     *
     * @param actualList 实际码
     * @return 结果
     */
    @Override
    public WeGroupCodeActualExistVO checkChatIdUnique(List<WeGroupCodeActual> actualList, Long groupId) {
        return weGroupCodeActualMapper.checkChatIdUnique(actualList, groupId);
    }

    @Override
    public int checkChatIdOnly(String chatId, Long id) {
        return weGroupCodeActualMapper.checkChatIdOnly(chatId, id);
    }


    /**
     * 通过群id增加实际群活码扫码入群人数
     *
     * @param chatId          群id
     * @param memberChangeCnt 人数
     */
    @Override
    public void updateScanTimesByChatId(String chatId, Integer memberChangeCnt) {
        final Integer minCnt = 0;
        //人数大于0才更新
        if (memberChangeCnt != null && minCnt.compareTo(memberChangeCnt) < 0) {
            weGroupCodeActualMapper.updateScanTimesByChatId(chatId, memberChangeCnt);
        }
        WeGroupCodeActual weGroupCodeActual = weGroupCodeActualMapper.selectOne(new LambdaQueryWrapper<WeGroupCodeActual>().eq(WeGroupCodeActual::getChatId, chatId));
        //更新status状态
        if (weGroupCodeActual.getScanCodeTimesLimit() <= weGroupCodeActual.getScanCodeTimes()) {
            weGroupCodeActual.setStatus(WeConstans.WE_CUSTOMER_MSG_RESULT_DEFALE);
            weGroupCodeActualMapper.updateById(weGroupCodeActual);
        }
    }

    @Override
    public List<WeGroupCodeActual> selectByGroupCodeId(Long groupCodeId) {
        if (groupCodeId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return weGroupCodeActualMapper.selectByGroupCodeId(groupCodeId);
    }

    /**
     * 批量添加企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param corpId
     * @return
     */
    @Override
    public List<WeGroupCodeActual> addBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, String corpId) {
        checkBatch(weGroupCodeActualList, corpId);
        // 判断应用类型
        String serverType = we3rdAppService.getServerType().getServerType();
        // 保存
        if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
            this.addThirdWeGroupCodeCorpActualBatch(weGroupCodeActualList, groupCodeId);
        }
        if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
            this.addInnerWeGroupCodeCorpActualBatch(weGroupCodeActualList, groupCodeId, Boolean.TRUE, corpId);
        }
        return weGroupCodeActualList;
    }

    /**
     * 批量添加企业微信活码校验
     *
     * @param weGroupCodeActualList 活码列表
     * @param corpId                企业id
     */
    private void checkBatch(List<WeGroupCodeActual> weGroupCodeActualList, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (CollectionUtils.isEmpty(weGroupCodeActualList)) {
            log.error("com.easyink.wecom.service.impl.WeGroupCodeActualServiceImpl.checkAddBatch: 添加的企业微信列表为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    /**
     * 新增待开发应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WeGroupCodeActual> addThirdWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId) {
        checkThirdParamsAdd(weGroupCodeActualList);
        if (groupCodeId == null) {
            groupCodeId = 0L;
        }
        Long finalGroupCodeId = groupCodeId;
        weGroupCodeActualList.forEach(item -> {
            item.setGroupCodeId(finalGroupCodeId);
            item.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE));
        });
        this.saveOrUpdateBatch(weGroupCodeActualList);
        return weGroupCodeActualList;
    }

    /**
     * 新增自建应用企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WeGroupCodeActual> addInnerWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, Boolean remoteCall, String corpId) {
        checkInnerParamsAdd(weGroupCodeActualList, corpId, remoteCall);
        // 这里主表id为null的情况: 1:在主表不存在的基础上,单独新增企业微信活码,默认设置0,后续保存主表的同时只需要更新主表id
        if (groupCodeId == null) {
            groupCodeId = 0L;
        }
        Long finalGroupCodeId = groupCodeId;
        // 默认二维码添加入群方式和客户群活码id
        weGroupCodeActualList.forEach(item -> {
            item.setScene(GroupCodeConstants.GROUP_SCENE);
            item.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE));
            item.setGroupCodeId(finalGroupCodeId);
        });
        // 调用企业微信接口添加
        if (remoteCall) {
            for (WeGroupCodeActual corpActual : weGroupCodeActualList) {
                AddJoinWayConfigDTO addJoinWayConfigDTO = new AddJoinWayConfigDTO().copyFromWeGroupCodeActual(corpActual);
                AddJoinWayResult addJoinWayResult = weGroupChatJoinClient.addJoinWayConfig(addJoinWayConfigDTO, corpId);
                GetJoinWayResult getJoinWayResult = weGroupChatJoinClient.getJoinWayConfig(new GetJoinWayConfigDTO(addJoinWayResult.getConfig_id()), corpId);
                // 保存二维码和configId
                corpActual.setActualGroupQrCode(getJoinWayResult.getJoin_way().getQr_code());
                corpActual.setConfigId(addJoinWayResult.getConfig_id());
            }
        }
        // 本地添加
        this.saveOrUpdateBatch(weGroupCodeActualList);
        return weGroupCodeActualList;
    }

    /**
     * 校验待开发应用参数
     *
     * @param weGroupCodeActualList
     */
    private void checkThirdParamsAdd(List<WeGroupCodeActual> weGroupCodeActualList) {
        checkParams(weGroupCodeActualList);
        for (WeGroupCodeActual corpActual : weGroupCodeActualList) {
            // 校验二维码
            if (StringUtils.isEmpty(corpActual.getActualGroupQrCode())) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkAddCorp: 企业微信活码为空");
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

    /**
     * 校验自建应用参数
     *
     * @param weGroupCodeActualList
     * @param corpId
     * @param remoteCall
     */
    private void checkInnerParamsAdd(List<WeGroupCodeActual> weGroupCodeActualList, String corpId, Boolean remoteCall) {
        checkParams(weGroupCodeActualList);
        StringUtils.checkCorpId(corpId);
        for (WeGroupCodeActual corpActual : weGroupCodeActualList) {
            // 校验客户群
            if (StringUtils.isEmpty(corpActual.getChatIds())) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeActualServiceImpl.checkInnerParams: 客户群为空");
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            // 同客户群主表信息只做保存无需调用接口新增的列表数据才需要校验二维码参数
            if (!remoteCall && StringUtils.isEmpty(corpActual.getActualGroupQrCode())) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeActualServiceImpl.checkInnerParams: 企业微信活码为空");
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

    /**
     * 添加待开发应用企业微信活码校验
     *
     * @param weGroupCodeActualList 活码列表
     */
    private void checkParams(List<WeGroupCodeActual> weGroupCodeActualList) {
        if (CollectionUtils.isEmpty(weGroupCodeActualList)) {
            log.error("com.easyink.wecom.service.impl.WeGroupCodeActualServiceImpl.checkThirdAdd: 添加的企业微信列表为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        for (WeGroupCodeActual corpActual : weGroupCodeActualList) {
            // 校验人数
            if (corpActual.getScanCodeTimesLimit() == null) {
                corpActual.setScanCodeTimesLimit(GroupCodeConstants.DEFAULT_CORP_GROUP_NUM);
            }
            if (corpActual.getScanCodeTimesLimit() > GroupCodeConstants.CORP_ACTUAL_GROUP_NUM_LIMIT) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkAddCorp: 进群人数上限超过最大值");
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

    /**
     * 批量修改企业微信活码
     *
     * @param weGroupCodeActualList
     * @param groupCodeId
     * @param corpId
     * @return
     */
    @Override
    public int editBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, String corpId) {
        checkBatch(weGroupCodeActualList, corpId);
        // 判断应用类型
        String serverType = we3rdAppService.getServerType().getServerType();
        // 保存
        int result = 0;
        if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
            result = this.editThirdWeGroupCodeCorpActualBatch(weGroupCodeActualList);
        }
        if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
            result = this.editInnerWeGroupCodeCorpActualBatch(weGroupCodeActualList, groupCodeId, Boolean.TRUE, corpId);
        }
        return result;
    }

    /**
     * 修改待开发应用企业微信活码
     *
     * @param weGroupCodeActualList 实际码列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int editThirdWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList) {
        checkThirdParamsEdit(weGroupCodeActualList);
        weGroupCodeActualList.forEach(item -> item.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE)));
        this.saveOrUpdateBatch(weGroupCodeActualList);
        return 1;
    }


    /**
     * 修改自建应用企业微信活码
     *
     * @param weGroupCodeActualList 实际码列表
     * @param groupCodeId           客户群码id
     * @param remoteCall            是否远程调用企业微信接口
     * @param corpId                企业id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int editInnerWeGroupCodeCorpActualBatch(List<WeGroupCodeActual> weGroupCodeActualList, Long groupCodeId, Boolean remoteCall, String corpId) {
        checkInnerParamsEdit(weGroupCodeActualList, remoteCall, corpId);
        weGroupCodeActualList.forEach(item ->
                item.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE))
        );
        // 调用企业微信接口修改
        // 对新增和修改的进行分组 true:新增 false:修改
        Map<Boolean, List<WeGroupCodeActual>> collect = weGroupCodeActualList.stream()
                .collect(Collectors.partitioningBy(corpActual -> corpActual.getConfigId() == null));
        // true: 添加
        if (CollectionUtils.isNotEmpty(collect.get(Boolean.TRUE))) {
            this.addInnerWeGroupCodeCorpActualBatch(collect.get(Boolean.TRUE), groupCodeId, Boolean.FALSE, corpId);
        }
        if (remoteCall) {
            // false: 修改
            for (WeGroupCodeActual corpActual : collect.get(Boolean.FALSE)) {
                // 修改已存在
                weGroupChatJoinClient.updateJoinWayConfig(new UpdateJoinWayConfigDTO().copyFromWeGroupCodeActual(corpActual), corpId);
            }
        }

        this.saveOrUpdateBatch(weGroupCodeActualList);
        return 1;
    }

    /**
     * 校验自建应用参数修改
     *
     * @param weGroupCodeActualList 实际码列表
     * @param remoteCall            是否远程调用企业微信接口
     * @param corpId                企业id
     */
    private void checkInnerParamsEdit(List<WeGroupCodeActual> weGroupCodeActualList, Boolean remoteCall, String corpId) {
        StringUtils.checkCorpId(corpId);
        checkParams(weGroupCodeActualList);
    }

    /**
     * 校验待开发应用参数修改
     *
     * @param weGroupCodeActualList
     */
    private void checkThirdParamsEdit(List<WeGroupCodeActual> weGroupCodeActualList) {
        checkParams(weGroupCodeActualList);
        for (WeGroupCodeActual corpActual : weGroupCodeActualList) {
            // 校验二维码
            if (StringUtils.isEmpty(corpActual.getActualGroupQrCode())) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkAddCorp: 企业微信活码不能为空");
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

    /**
     * 批量删除企业微信活码
     *
     * @param removeIds
     * @param corpId
     * @return
     */
    @Override
    public int removeBatch(List<Long> removeIds, String corpId) {
        checkRemoveBatch(removeIds, corpId);
        // 判断应用类型
        String serverType = we3rdAppService.getServerType().getServerType();
        // 保存
        int result = 0;
        if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
            result = this.removeThirdWeGroupCodeActualByIds(removeIds);
        }
        if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
            result = this.removeInnerWeGroupCodeActualByIds(removeIds, corpId);
        }
        return result;
    }

    /**
     * 校验批量删除参数
     *
     * @param removeIds 要删除的ids
     * @param corpId    企业id
     */
    private void checkRemoveBatch(List<Long> removeIds, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (CollectionUtils.isEmpty(removeIds)) {
            log.error("com.easyink.wecom.service.impl.WeGroupCodeActualServiceImpl.checkRemoveBatch: removeIds为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    /**
     * 删除待开发应用企业微信活码
     *
     * @param removeIds 企业微信实际活码id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeThirdWeGroupCodeActualByIds(List<Long> removeIds) {
        return weGroupCodeActualMapper.deleteBatchIds(removeIds);
    }

    /**
     * 删除自建应用企业微信活码
     *
     * @param removeIds 企业微信实际活码id
     * @param corpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeInnerWeGroupCodeActualByIds(List<Long> removeIds, String corpId) {
        StringUtils.checkCorpId(corpId);
        // 查询获取configList
        List<WeGroupCodeActual> corpActualList = weGroupCodeActualMapper.selectBatchIds(removeIds);
        List<String> configIdList = corpActualList.stream().map(WeGroupCodeActual::getConfigId).collect(Collectors.toList());

        // 调用企业微信接口删除
        configIdList.forEach(configId -> {
            weGroupChatJoinClient.delJoinWayConfig(new DelJoinWayConfigDTO(configId), corpId);
        });

        // 删除本地数据库
        return this.removeThirdWeGroupCodeActualByIds(removeIds);
    }


    /**
     * 构造实际群码
     *
     * @param weGroupCodeActual 实际群码
     */
    private void buildActualCode(WeGroupCodeActual weGroupCodeActual) {
        //为空设置默认有效期
        if (weGroupCodeActual.getEffectTime() == null) {
            weGroupCodeActual.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE));
        }
        //有效期不为空且大于当前时间
        if (weGroupCodeActual.getEffectTime() != null && new Date().compareTo(weGroupCodeActual.getEffectTime()) < 0) {
            weGroupCodeActual.setStatus(WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE);
        } else if (weGroupCodeActual.getEffectTime() != null && new Date().compareTo(weGroupCodeActual.getEffectTime()) > 0) {
            //有效期不为空且小于当前时间
            weGroupCodeActual.setStatus(WeConstans.WE_CUSTOMER_MSG_RESULT_DEFALE);
        }
        // 实际码对应客户群变化时，检查其唯一性
        int countNum = checkChatIdOnly(weGroupCodeActual.getChatId(), weGroupCodeActual.getId());
        if (countNum > 0) {
            throw new CustomException(ResultTip.TIP_ACTUAL_GROUP_CODE_EXIST);
        }
    }
}
