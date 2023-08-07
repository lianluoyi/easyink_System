package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GroupCodeConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.code.GroupCodeTypeEnum;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.model.GroupCodeShortUrlAppendInfo;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.QREncode;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupCode;
import com.easyink.wecom.domain.WeGroupCodeActual;
import com.easyink.wecom.domain.dto.FindWeGroupCodeDTO;
import com.easyink.wecom.domain.query.groupcode.GroupCodeDetailQuery;
import com.easyink.wecom.domain.vo.WeGroupCodeActualExistVO;
import com.easyink.wecom.domain.vo.groupcode.GroupCodeActivityFirstVO;
import com.easyink.wecom.domain.vo.groupcode.GroupCodeDetailVO;
import com.easyink.wecom.handler.shorturl.GroupCodeShortUrlHandler;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeGroupCodeActualMapper;
import com.easyink.wecom.mapper.WeGroupCodeMapper;
import com.easyink.wecom.mapper.WeGroupMapper;
import com.easyink.wecom.service.We3rdAppService;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeGroupCodeActualService;
import com.easyink.wecom.service.WeGroupCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户群活码Service业务层处理
 *
 * @author admin
 * @date 2020-10-07
 */
@Service
@Slf4j
public class WeGroupCodeServiceImpl extends ServiceImpl<WeGroupCodeMapper, WeGroupCode> implements WeGroupCodeService {
    private final WeGroupCodeActualService weGroupCodeActualService;
    private final WeGroupCodeActualMapper actualCodeMapper;
    private final WeCorpAccountService weCorpAccountService;
    private final RuoYiConfig ruoYiConfig;
    private final WeGroupCodeMapper weGroupCodeMapper;
    private final We3rdAppService we3rdAppService;
    private final WeGroupMapper weGroupMapper;
    private final GroupCodeShortUrlHandler groupCodeShortUrlHandler;

    @Lazy
    @Autowired
    public WeGroupCodeServiceImpl(WeGroupCodeActualService weGroupCodeActualService, WeGroupCodeActualMapper actualCodeMapper, WeCorpAccountService weCorpAccountService, RuoYiConfig ruoYiConfig, WeGroupCodeMapper weGroupCodeMapper, We3rdAppService we3rdAppService, WeGroupMapper weGroupMapper, GroupCodeShortUrlHandler groupCodeShortUrlHandler) {
        this.weGroupCodeActualService = weGroupCodeActualService;
        this.actualCodeMapper = actualCodeMapper;
        this.weCorpAccountService = weCorpAccountService;
        this.ruoYiConfig = ruoYiConfig;
        this.weGroupCodeMapper = weGroupCodeMapper;
        this.we3rdAppService = we3rdAppService;
        this.weGroupMapper = weGroupMapper;
        this.groupCodeShortUrlHandler = groupCodeShortUrlHandler;
    }

    /**
     * 保存客户群活码
     *
     * @param weGroupCode 客户群活码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WeGroupCode weGroupCode) {
        if (weGroupCode == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 判断创建类型是否存在
        GroupCodeTypeEnum.assertNotNull(weGroupCode.getCreateType());

        // 不同类型不同处理
        if (GroupCodeTypeEnum.GROUP_QR.getType().equals(weGroupCode.getCreateType())) {
            groupCodeAddHandle(weGroupCode);
        }
        if (GroupCodeTypeEnum.CORP_QR.getType().equals(weGroupCode.getCreateType())) {
            corpCodeAddHandle(weGroupCode);
        }
        // 二维码内容，即该二维码扫码后跳转的页面URL
        buildQrUrl(weGroupCode);
        //插入群活码
        insertWeGroupCode(weGroupCode);
    }

    /**
     * 设置群活码的短链链接
     *
     * @param weGroupCode 群活码实体
     */
    private void setAppLink(WeGroupCode weGroupCode) {
        if (weGroupCode == null) {
            return;
        }
        //生成短链
        GroupCodeShortUrlAppendInfo appendInfo = GroupCodeShortUrlAppendInfo.builder()
                                                                            .corpId(weGroupCode.getCorpId())
                                                                            .groupCodeId(weGroupCode.getId())
                                                                            .build();
        String appLink = groupCodeShortUrlHandler.createShortUrl(weGroupCode.getCorpId(), weGroupCode.getCodeUrl(), LoginTokenService.getUsername(),appendInfo);
        if (StringUtils.isNotBlank(appLink)) {
            weGroupCode.setAppLink(appLink);
        }
    }

    /**
     * 群二维码活码处理添加
     *
     * @param weGroupCode
     */
    private void groupCodeAddHandle(WeGroupCode weGroupCode) {
        checkGroupParams(weGroupCode);
        List<WeGroupCodeActual> actualList = weGroupCode.getActualList();
        //保存实际群码
        if (CollectionUtils.isNotEmpty(actualList)) {
            buildActualList(actualList, weGroupCode.getId());
            weGroupCodeActualService.saveBatch(actualList);
            String actualIds = actualList.stream()
                    .map(weGroupCodeActual -> weGroupCodeActual.getId().toString()).collect(Collectors.joining(StrUtil.COMMA));
            weGroupCode.setSeq(actualIds);
        }
    }

    /**
     * 校验群二维码方式的参数
     *
     * @param weGroupCode 客户群码
     */
    private void checkGroupParams(WeGroupCode weGroupCode) {
        checkBase(weGroupCode);
        if (CollectionUtils.isNotEmpty(weGroupCode.getActualList())) {
            int preSize = weGroupCode.getActualList().size();
            int postSize = weGroupCode.getActualList().stream()
                    .map(WeGroupCodeActual::getChatId).collect(Collectors.toSet()).size();
            if (preSize != postSize) {
                log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkGroupParams: 新增实际群码失败,该群聊二维码已重复存在");
                throw new CustomException(ResultTip.TIP_ACTUAL_GROUP_CODE_EXIST);
            }
            for (WeGroupCodeActual codeActual : weGroupCode.getActualList()) {
                if (StringUtils.isEmpty(codeActual.getChatId())) {
                    log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkGroupParams: 实际群码未选择客户群");
                    throw new CustomException(ResultTip.TIP_MISS_ACTUAL_GROUP);
                }
            }
        }
        //检查入群人数
        checkActualNum(weGroupCode.getActualList(), GroupCodeConstants.ACTUAL_GROUP_NUM_LIMIT, GroupCodeConstants.DEFAULT_GROUP_NUM,
                ResultTip.TIP_ACTUAL_GROUP_OVER_NUM_TWO_HUNDRED);
    }

    /**
     * 企业微信活码处理添加
     *
     * @param weGroupCode
     */
    private void corpCodeAddHandle(WeGroupCode weGroupCode) {
        // 获取当前应用类型
        String serverType = we3rdAppService.getServerType().getServerType();

        checkCorpParams(weGroupCode, serverType);

        Optional.ofNullable(weGroupCode.getActualList())
                .ifPresent(list -> {
                    if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
                        weGroupCodeActualService.addThirdWeGroupCodeCorpActualBatch(list, weGroupCode.getId());
                    }
                    if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
                        // 这里不需要调用远程接口添加
                        weGroupCodeActualService.addInnerWeGroupCodeCorpActualBatch(list, weGroupCode.getId(), Boolean.FALSE, weGroupCode.getCorpId());
                    }
                    // 设置排序字段
                    String actualIds = weGroupCode.getActualList().stream()
                            .map(weGroupCodeActual -> weGroupCodeActual.getId().toString()).collect(Collectors.joining(StrUtil.COMMA));
                    weGroupCode.setSeq(actualIds);
                });

    }

    /**
     * 校验企业微信活码方式的参数
     *
     * @param weGroupCode 客户群码
     * @param serverType  应用类型
     */
    private void checkCorpParams(WeGroupCode weGroupCode, String serverType) {
        checkBase(weGroupCode);
        // 自建应用和第三方应用不同的校验
        if (CollectionUtils.isNotEmpty(weGroupCode.getActualList())) {
            for (WeGroupCodeActual corpActual : weGroupCode.getActualList()) {
                if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
                    // 校验客户群
                    if (StringUtils.isEmpty(corpActual.getChatIds())) {
                        log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkCorpParams: 客户群为空");
                        throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
                    }
                    // 校验企业微信活码关联群是否超过最大值5
                    if (corpActual.getChatIds().split(StrUtil.COMMA).length > GroupCodeConstants.CORP_ACTUAL_CODE_REF_GROUP_LIMIT) {
                        throw new CustomException(ResultTip.TIP_ACTUAL_GROUP_REF_GROUP_LIMIT_SIZE);
                    }
                }
                // 校验二维码
                if (StringUtils.isEmpty(corpActual.getActualGroupQrCode())) {
                    log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkCorpParams: 企业微信活码为空");
                    throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
                }
            }
            // 校验人数
            checkActualNum(weGroupCode.getActualList(), GroupCodeConstants.CORP_ACTUAL_GROUP_NUM_LIMIT,
                    GroupCodeConstants.DEFAULT_CORP_GROUP_NUM, ResultTip.TIP_ACTUAL_GROUP_OVER_NUM_ONE_THOUSAND);
        }
    }

    /**
     * 根据群活码id查询实际码列表
     *
     * @param groupCodeId 群活码id
     * @return 结果
     */
    @Override
    public List<WeGroupCodeActual> selectActualList(Long groupCodeId) {
        return actualCodeMapper.selectActualList(groupCodeId);
    }

    /**
     * 查询客户群活码列表
     *
     * @param weGroupCode 客户群活码
     * @return 客户群活码
     */
    @Override
    public List<WeGroupCode> selectWeGroupCodeList(FindWeGroupCodeDTO weGroupCode) {
        if (weGroupCode == null || StringUtils.isEmpty(weGroupCode.getCorpId())) {
            log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.selectWeGroupCodeList: corpId不能为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(weGroupCode.getBeginTime())) {
            weGroupCode.setBeginTime(DateUtils.parseBeginDay(DateUtils.timeFormatTrans(weGroupCode.getBeginTime(), DateUtils.YYYYMMDD, DateUtils.YYYY_MM_DD)));
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(weGroupCode.getEndTime())) {
            weGroupCode.setEndTime(DateUtils.parseEndDay(DateUtils.timeFormatTrans(weGroupCode.getEndTime(), DateUtils.YYYYMMDD, DateUtils.YYYY_MM_DD)));
        }
        List<WeGroupCode> weGroupCodeList = baseMapper.selectWeGroupCodeList(weGroupCode);
        // 这里设置是为了新课进群和老客进群的总群码的统计
        for (WeGroupCode item : weGroupCodeList) {
            List<WeGroupCodeActual> actualList = actualCodeMapper.selectActualList(item.getId());
            item.setActualList(actualList);
        }
        return weGroupCodeList;
    }

    @Override
    public List<WeGroupCode> selectExpireCode(String corpId) {
        StringUtils.checkCorpId(corpId);
        return weGroupCodeMapper.listOfExpireGroupCode(corpId);
    }

    /**
     * 新增客户群活码
     *
     * @param weGroupCode 客户群活码
     */
    private void insertWeGroupCode(WeGroupCode weGroupCode) {
        if (weGroupCode == null || StringUtils.isBlank(weGroupCode.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        baseMapper.insertWeGroupCode(weGroupCode);
    }

    /**
     * 修改客户群活码
     *
     * @param weGroupCode 客户群活码
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeGroupCode(WeGroupCode weGroupCode) {
        if (weGroupCode == null || StringUtils.isBlank(weGroupCode.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return baseMapper.updateWeGroupCode(weGroupCode);
    }

    /**
     * 构造实际群码参数
     *
     * @param actualList  实际群码
     * @param groupCodeId 群活码id
     */
    private void buildActualList(List<WeGroupCodeActual> actualList, Long groupCodeId) {
        for (WeGroupCodeActual weGroupCodeActual : actualList) {
            weGroupCodeActual.setGroupCodeId(groupCodeId);
            if (weGroupCodeActual.getEffectTime() == null) {
                weGroupCodeActual.setEffectTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE));
            }
        }
        //检查其它活码是否有重复的群
        WeGroupCodeActualExistVO weGroupCodeActualExistVO = weGroupCodeActualService.checkChatIdUnique(actualList, groupCodeId);
        if (weGroupCodeActualExistVO.getCount() > 0) {
            throw new CustomException(ResultTip.TIP_ACTUAL_GROUP_CODE_EXIST);
        }
    }

    /**
     * 批量删除客户群活码
     *
     * @param ids 需要删除的客户群活码ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int remove(Long[] ids) {

        // 查询所有码
        List<WeGroupCode> weGroupCodeList = this.baseMapper.selectBatchIds(Arrays.asList(ids));
        // 根据类型分组进行删除对应实际码
        Map<Boolean, List<WeGroupCode>> groupCodeTypeMap = weGroupCodeList.stream()
                .collect(Collectors.partitioningBy(item -> GroupCodeTypeEnum.GROUP_QR.getType().equals(item.getCreateType())));
        // true,群二维码处理
        if (CollectionUtils.isNotEmpty(groupCodeTypeMap.get(Boolean.TRUE))) {
            groupCodeDeleteHandle(groupCodeTypeMap.get(Boolean.TRUE));
        }
        // false, 企业微信活码处理
        if (CollectionUtils.isNotEmpty(groupCodeTypeMap.get(Boolean.TRUE))) {
            corpCodeDeleteHandle(groupCodeTypeMap.get(Boolean.FALSE), LoginTokenService.getLoginUser().getCorpId());
        }
        // 删除主表
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除群二维码类型
     *
     * @param weGroupCodeList
     */
    private void groupCodeDeleteHandle(List<WeGroupCode> weGroupCodeList) {
        if (CollectionUtils.isNotEmpty(weGroupCodeList)) {
            LambdaQueryWrapper<WeGroupCodeActual> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(WeGroupCodeActual::getGroupCodeId, weGroupCodeList.stream().map(WeGroupCode::getId).collect(Collectors.toList()));
            actualCodeMapper.delete(wrapper);
        }
    }

    /**
     * 删除企业微信活码类型
     *
     * @param weGroupCodeList
     * @param corpId
     */
    private void corpCodeDeleteHandle(List<WeGroupCode> weGroupCodeList, String corpId) {
        // 根据应用类型进行删除
        if (CollectionUtils.isNotEmpty(weGroupCodeList)) {
            // 获取当前应用类型
            String serverType = we3rdAppService.getServerType().getServerType();
            List<Long> removeIds = weGroupCodeList.stream().map(WeGroupCode::getId).collect(Collectors.toList());
            if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
                weGroupCodeActualService.removeThirdWeGroupCodeActualByIds(removeIds);
            }
            if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
                weGroupCodeActualService.removeInnerWeGroupCodeActualByIds(removeIds, corpId);
            }
        }
    }


    /**
     * 检测活码名称是否被占用
     *
     * @param weGroupCode 活码对象
     * @return 结果
     */
    @Override
    public boolean isNameOccupied(WeGroupCode weGroupCode) {
        StringUtils.checkCorpId(weGroupCode.getCorpId());
        Long currentId = Optional.ofNullable(weGroupCode.getId()).orElse(-1L);
        LambdaQueryWrapper<WeGroupCode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeGroupCode::getActivityName, weGroupCode.getActivityName()).eq(WeGroupCode::getDelFlag, WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE).eq(WeGroupCode::getCorpId, weGroupCode.getCorpId());
        List<WeGroupCode> res = baseMapper.selectList(queryWrapper);
        return !res.isEmpty() && !currentId.equals(res.get(0).getId());
    }

    /**
     * 通过员工活码获取群活码，用于新客自动拉群。
     *
     * @param state  员工活码state
     * @param corpId 企业Id
     * @return 群活码URL
     */
    @Override
    public String selectGroupCodeUrlByEmplCodeState(String state, String corpId) {
        StringUtils.checkCorpId(corpId);
        return baseMapper.selectGroupCodeUrlByEmplCodeState(state, corpId);
    }

    /**
     * 校验是否超过上限,给定缺省值
     *
     * @param actualList 实际码列表
     * @param limitNum   上限值
     * @param defaultNum 默认值
     * @param resultTip  异常结果枚举
     */
    private void checkActualNum(List<WeGroupCodeActual> actualList, Integer limitNum, Integer defaultNum, ResultTip resultTip) {
        actualList.forEach(weGroupCodeActual -> {
            if (weGroupCodeActual.getScanCodeTimesLimit() == null) {
                weGroupCodeActual.setScanCodeTimesLimit(defaultNum);
            }
            if (weGroupCodeActual.getScanCodeTimesLimit() > limitNum) {
                throw new CustomException(resultTip);
            }
        });
    }


    /**
     * 更新客户群活码
     *
     * @param weGroupCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int edit(WeGroupCode weGroupCode) {
        // 不同类型不同处理
        if (GroupCodeTypeEnum.GROUP_QR.getType().equals(weGroupCode.getCreateType())) {
            groupCodeEditHandle(weGroupCode);
        }
        if (GroupCodeTypeEnum.CORP_QR.getType().equals(weGroupCode.getCreateType())) {
            corpCodeEditHandle(weGroupCode);
        }
        // 二维码内容，即该二维码扫码后跳转的页面URL
        buildQrUrl(weGroupCode);
        // 生成活码短链
        setAppLink(weGroupCode);
        // 修改客户群活码
        return updateWeGroupCode(weGroupCode);
    }

    /**
     * 查询客户群码详情 -> 实际是查实际码的列表
     *
     * @param groupCodeDetailQuery
     * @param createType
     * @return
     */
    @Override
    public List<GroupCodeDetailVO> getGroupCodeDetail(GroupCodeDetailQuery groupCodeDetailQuery, Integer createType) {
        List<GroupCodeDetailVO> resulList;
        resulList = actualCodeMapper.selectGroupActualListWithGroupQr(groupCodeDetailQuery);
        if (GroupCodeTypeEnum.CORP_QR.getType().equals(createType)) {
            // 设置群详情
            for (GroupCodeDetailVO detailVO : resulList) {
                String[] split = detailVO.getChatIds().split(StrUtil.COMMA);
                List<String> chatIds = Arrays.asList(split);
                List<WeGroup> weGroups = weGroupMapper.selectBatchIds(chatIds);
                detailVO.setGroupDetailVOList(weGroups);
            }
        }
        return resulList;
    }

    /**
     * 获取可用的企业微信实际码
     *
     * @param id        客户群id
     * @param groupCode
     * @return
     */
    @Override
    public GroupCodeActivityFirstVO doGetActual(Long id, WeGroupCode groupCode) {

        List<WeGroupCodeActual> actualCodeList = this.selectActualList(id);
        WeGroupCodeActual groupCodeActual = null;
        for (WeGroupCodeActual item : actualCodeList) {
            // 获取第一个可用的实际码
            if (WeConstans.WE_GROUP_CODE_ENABLE.equals(item.getStatus()) &&
                    item.getScanCodeTimesLimit() > item.getScanCodeTimes()) {
                groupCodeActual = item;
                break;
            } else {
                // 修改使用状态
                updateStatusDisableIfNecessory(item.getId());
            }
        }
        GroupCodeActivityFirstVO activityFirstVO = null;
        if (groupCodeActual != null) {
            // 判断如果是企业微信活码扫码则使用次数+1
            addScanCodeTimesIfNecessory(groupCode.getCreateType(), groupCodeActual.getId());

            activityFirstVO = new GroupCodeActivityFirstVO();
            activityFirstVO.setActivityName(groupCode.getActivityName());
            activityFirstVO.setTipMsg(groupCode.getTipMsg());
            activityFirstVO.setGuide(groupCode.getGuide());
            activityFirstVO.setActualQRCode(groupCodeActual.getActualGroupQrCode());
            activityFirstVO.setIsOpenTip(groupCode.getShowTip().toString());
            activityFirstVO.setServiceQrCode(groupCode.getCustomerServerQrCode());
            activityFirstVO.setGroupName(groupCodeActual.getChatGroupName());
            return activityFirstVO;
    }
        return activityFirstVO;
    }

    @Override
    public String getCodeAppLink(Long id) {
        if (id == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 获取活码信息
        WeGroupCode weGroupCode = getById(id);
        if (weGroupCode == null) {
            throw new CustomException(ResultTip.TIP_NO_AVAILABLE_GROUP_CODE);
        }
        // 如果已经生成过则直接返回
        if (org.apache.commons.lang3.StringUtils.isNotBlank(weGroupCode.getAppLink())) {
            return weGroupCode.getAppLink();
        }
        // 没有则 生成一个群活码小程序短链
        GroupCodeShortUrlAppendInfo appendInfo = GroupCodeShortUrlAppendInfo.builder()
                                                                            .groupCodeId(weGroupCode.getId())
                                                                            .corpId(weGroupCode.getCorpId())
                                                                            .build();
        String shortUrl = groupCodeShortUrlHandler.createShortUrl(weGroupCode.getCorpId(), weGroupCode.getCodeUrl(), LoginTokenService.getUsername(), appendInfo);
        if (org.apache.commons.lang3.StringUtils.isBlank(shortUrl)) {
            throw new CustomException(ResultTip.TIP_ERROR_CREATING_APP_lINK);
        }
        //保存到活码
        weGroupCode.setAppLink(shortUrl);
        updateById(weGroupCode);
        return shortUrl;
    }

    /**
     * 修改活码状态为禁用
     *
     * @param id
     */
    private void updateStatusDisableIfNecessory(Long id) {
        actualCodeMapper.updateStatus(id, WeConstans.WE_GROUP_CODE_DISABLE);
    }

    /**
     * 添加扫码次数如果是企业微信活码添加
     *
     * @param createType 客户群码类型
     * @param id         实际码id
     */
    private void addScanCodeTimesIfNecessory(Integer createType, Long id) {
        if (GroupCodeTypeEnum.CORP_QR.getType().equals(createType)) {
            actualCodeMapper.addScanCodeTimes(id);
        }
    }

    /**
     * 群二维码活码处理修改
     *
     * @param weGroupCode
     */
    private void groupCodeEditHandle(WeGroupCode weGroupCode) {
        checkGroupParams(weGroupCode);
        // 更新实际码
        List<WeGroupCodeActual> actualList = weGroupCode.getActualList();
        if (CollUtil.isNotEmpty(weGroupCode.getDelActualIdList())) {
            weGroupCodeActualService.removeByIds(weGroupCode.getDelActualIdList());
        }
        if (CollUtil.isNotEmpty(actualList)) {
            buildActualList(actualList, weGroupCode.getId());
            weGroupCodeActualService.saveOrUpdateBatch(actualList);
            String actualIds = actualList.stream().map(weGroupCodeActual -> weGroupCodeActual.getId().toString()).collect(Collectors.joining(StrUtil.COMMA));
            weGroupCode.setSeq(actualIds);
        }
    }

    /**
     * 企业微信活码处理修改
     *
     * @param weGroupCode
     */
    private void corpCodeEditHandle(WeGroupCode weGroupCode) {
        // 获取当前应用类型
        String serverType = we3rdAppService.getServerType().getServerType();
        checkCorpParams(weGroupCode, serverType);

        Optional.ofNullable(weGroupCode.getActualList())
                .ifPresent(list -> {
                    // 设置归属客户群码id
                    list.forEach(item -> item.setGroupCodeId(weGroupCode.getId()));
                    // 保存
                    if (ServerTypeEnum.THIRD.getType().equals(serverType)) {
                        weGroupCodeActualService.editThirdWeGroupCodeCorpActualBatch(list);
                        // 删除的企业微信活码
                        if (CollectionUtils.isNotEmpty(weGroupCode.getDelActualIdList())) {
                            weGroupCodeActualService.removeThirdWeGroupCodeActualByIds(weGroupCode.getDelActualIdList());
                        }
                    }
                    if (ServerTypeEnum.INTERNAL.getType().equals(serverType)) {
                        // 新增的只需要保存,修改的需要调用接口
                        weGroupCodeActualService.editInnerWeGroupCodeCorpActualBatch(list, weGroupCode.getId(), Boolean.FALSE, weGroupCode.getCorpId());
                        // 删除的企业微信活码
                        if (CollectionUtils.isNotEmpty(weGroupCode.getDelActualIdList())) {
                            weGroupCodeActualService.removeInnerWeGroupCodeActualByIds(weGroupCode.getDelActualIdList(), weGroupCode.getCorpId());
                        }
                    }
                    // 设置排序字段
                    String actualIds = weGroupCode.getActualList().stream()
                            .map(weGroupCodeActual -> weGroupCodeActual.getId().toString()).collect(Collectors.joining(StrUtil.COMMA));
                    weGroupCode.setSeq(actualIds);
                });
    }

    /**
     * 客户群活码基础信息校验
     *
     * @param weGroupCode
     */
    private void checkBase(WeGroupCode weGroupCode) {
        if (weGroupCode == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (StringUtils.isEmpty(weGroupCode.getActivityName()) || StringUtils.isEmpty(weGroupCode.getActivityDesc())) {
            log.error("com.easyink.wecom.service.impl.WeGroupCodeServiceImpl.checkBase: 活码名称或者活码描述为空");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 活码名唯一性检查
        if (isNameOccupied(weGroupCode)) {
            throw new CustomException(ResultTip.TIP_GROUP_CODE_NAME_OCCUPIED);
        }


    }

    /**
     * 构建跳转的二维码
     *
     * @param weGroupCode
     */
    private void buildQrUrl(WeGroupCode weGroupCode) {
        // 二维码内容，即该二维码扫码后跳转的页面URL
        WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(weGroupCode.getCorpId());
        String content = weCorpAccount.getH5DoMainName() + "/#/groupCode?id=" + weGroupCode.getId();
        try {
            String fileName;
            if (ruoYiConfig.getFile().isStartCosUpload()) {
                fileName = FileUploadUtils.upload2Cos(QREncode.getQRCodeMultipartFile(content, weGroupCode.getAvatarUrl()), ruoYiConfig.getFile().getCos());
                weGroupCode.setCodeUrl(ruoYiConfig.getFile().getCos().getCosImgUrlPrefix() + fileName);
            } else {
                fileName = FileUploadUtils.upload(RuoYiConfig.getProfile(), QREncode.getQRCodeMultipartFile(content, weGroupCode.getAvatarUrl()));
                weGroupCode.setCodeUrl(weCorpAccount.getH5DoMainName() + Constants.RESOURCE_PREFIX + WeConstans.SLASH + fileName);
            }
        } catch (IOException e) {
            log.error("上传客户群活码异常: ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public String getCodeUrlByIdAndCorpId(Long id, String corpId) {
        if (id == null || org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.getCodeUrlByIdAndCorpId(id, corpId);
    }
}
