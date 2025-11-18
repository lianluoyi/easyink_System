package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.emple.CustomerAssistantConstants;
import com.easyink.common.constant.emple.EmployCodeConstants;
import com.easyink.common.constant.redeemcode.RedeemCodeConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.enums.*;
import com.easyink.common.enums.code.WelcomeMsgTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.lock.LockUtil;
import com.easyink.common.shorturl.CustomerEmpleCodeShortUrlAppendInfo;
import com.easyink.common.shorturl.model.EmpleCodeShortUrlAppendInfo;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.client.WeExternalContactClient;
import com.easyink.wecom.core.emplycode.CustomerCodeValidator;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.dto.common.Attachment;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.common.Attachments;
import com.easyink.wecom.domain.dto.common.Text;
import com.easyink.wecom.domain.dto.customer.GetExternalDetailResp;
import com.easyink.wecom.domain.dto.emplecode.*;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easyink.wecom.domain.enums.SelectTagScopeTypeEnum;
import com.easyink.wecom.domain.model.customer.CustomerExtendedProperties;
import com.easyink.wecom.domain.model.customer.CustomerId;
import com.easyink.wecom.domain.model.emplecode.State;
import com.easyink.wecom.domain.model.emplecode.TagGroupInfoModel;
import com.easyink.wecom.domain.model.emplecode.TagInfoModel;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.emplecode.SelectTagVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeByNameVO;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSelectTagScope;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;
import com.easyink.wecom.handler.CustomerEmployCodeShortUrlHandler;
import com.easyink.wecom.handler.shorturl.EmpleCodeShortUrlHandler;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.mapper.redeemcode.WeRedeemCodeMapper;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.publishevent.welcomemsg.SendWelcomeMsgSuccessEvent;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.radar.WeRadarService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeActivityService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import com.easyink.wecom.utils.AttachmentService;
import com.easyink.wecom.utils.ExtraMaterialUtils;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 员工活码Service业务层处理
 *
 * @author Society my sister Li
 * @date 2021-11-02
 */
@Slf4j
@Service
@AllArgsConstructor
public class WeEmpleCodeServiceImpl extends ServiceImpl<WeEmpleCodeMapper, WeEmpleCode> implements WeEmpleCodeService {
    private final WeEmpleCodeChannelMapper weEmpleCodeChannelMapper;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final AttachmentService attachmentService;
    private final WeEmpleCodeTagService weEmpleCodeTagService;
    private final WeEmpleCodeUseScopService weEmpleCodeUseScopService;
    private final WeExternalContactClient weExternalContactClient;
    private final RedisCache redisCache;
    private final WeEmpleCodeMaterialService weEmpleCodeMaterialService;
    private final WeMaterialService weMaterialService;
    private final WeGroupCodeService weGroupCodeService;
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeGroupCodeActualService weGroupCodeActualService;
    private final WeRedeemCodeMapper weRedeemCodeMapper;
    private final WeRedeemCodeActivityService weRedeemCodeActivityService;
    private final WeUserService weUserService;
    private final EmpleCodeShortUrlHandler empleCodeShortUrlHandler;
    private final CustomerEmployCodeShortUrlHandler customerEmployCodeShortUrlHandler;
    private final RuoYiConfig ruoYiConfig;
    private final WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;
    private final WeUserMapper weUserMapper;
    private final WechatOpenService wechatOpenService;
    private final WeCustomerTempEmpleCodeSettingService weCustomerTempEmpleCodeSettingService;
    private final WeCustomerTempEmpleCodeSelectTagScopeMapper selectTagScopeMapper;
    private final WeCustomerTempEmpleCodeSelectTagScopeService selectTagScopeService;
    private final WeTagMapper tagMapper;
    private final WeTagGroupMapper tagGroupMapper;
    private final WeMsgTlpMaterialService weMsgTlpMaterialService;
    private final WeCustomerClient weCustomerClient;
    private final WeCorpAccountService weCorpAccountService;
    private final WeCustomerService weCustomerService;
    private final WeRedeemCodeService weRedeemCodeService;
    private final EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeTagService weTagService;
    @Resource(name = "welcomeMsgTaskExecutor")
    private ThreadPoolTaskExecutor welcomeMsgTaskExecutor;


    private static final String BIRTHDAY = "出生日期";
    public static final String DESC = "描述";
    public static final String PHONE = "电话";
    private static final String EMAIL = "邮箱";
    private static final String ADDRESS = "地址";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarkContent {
        // 标签
        /**
         * 是否打标签
         */
        private boolean isMarkTag;

        /**
         * 打标签的标签id列表
         */
        private List<String> tagIdList;


        // 备注
        /**
         * 是否打标签
         */
        private boolean isMarkRemark;
        /**
         * 打备注类型
         */
        private Integer remarkType;
        /**
         * 打备注名
         */
        private String remarkName;

        /**
         * 扩展属性
         */
        private List<CustomerExtendedProperties> extendedProperties = new ArrayList<>();

    }
    /**
     * 根据素材列表设置素材顺序
     *
     * @param materialList materialList
     * @return String[]
     */
    private static String[] getMaterialSort(List<AddWeMaterialDTO> materialList) {
        if (CollectionUtils.isEmpty(materialList)) {
            return new String[]{};
        }
        List<Long> collect = materialList.stream().map(AddWeMaterialDTO::getId).collect(Collectors.toList());
        return StringUtils.join(collect, ",").split(",");
    }

    @Override
    public WeEmpleCodeVO selectWeEmpleCodeById(Long id, String corpId) {
        if (StringUtils.isBlank(corpId) || id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeEmpleCodeVO weEmpleCodeVO = this.baseMapper.selectWeEmpleCodeById(id, corpId);
        if (weEmpleCodeVO == null) {
            return null;
        }
        //组装数据
        bulidWeEmpleCodeVOData(weEmpleCodeVO);
        return weEmpleCodeVO;
    }

    @Override
    public List<WeEmpleCodeVO> selectWeEmpleCodeList(FindWeEmpleCodeDTO weEmployCode) {
        if (StringUtils.isBlank(weEmployCode.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        //将yyyy-MM-dd转为yyyy-MM-dd HH:mm:ss
        if (StringUtils.isNotBlank(weEmployCode.getBeginTime())) {
            weEmployCode.setBeginTime(DateUtils.parseBeginDay(weEmployCode.getBeginTime()));
        }
        if (StringUtils.isNotBlank(weEmployCode.getEndTime())) {
            weEmployCode.setEndTime(DateUtils.parseEndDay(weEmployCode.getEndTime()));
        }
        List<WeEmpleCodeVO> weEmployCodeList = this.baseMapper.selectWeEmpleCodeList(weEmployCode);
        if (CollectionUtils.isEmpty(weEmployCodeList)) {
            return weEmployCodeList;
        }
        List<Long> employCodeIdList = weEmployCodeList.stream().map(WeEmpleCode::getId).collect(Collectors.toList());
        // 获取创建人姓名列表
        List<String> createByList = weEmployCodeList.stream().map(WeEmpleCode::getCreateBy).distinct().collect(Collectors.toList());
        // 为创建人设置主部门名称
        setCreateByMainDepartMent(createByList, weEmployCode.getCorpId(), weEmployCodeList);
        // 查询使用人
        List<WeEmpleCodeUseScop> useScopeList = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListByIds(employCodeIdList, weEmployCode.getCorpId());
        // 查询使用部门(查询使用人时需要用businessId关联we_user表，活码使用部门时不传入businessId)
        List<WeEmpleCodeUseScop> departmentScopeList = weEmpleCodeUseScopService.selectDepartmentWeEmpleCodeUseScopListByIds(employCodeIdList);

        weEmployCodeList.forEach(employCode -> {
            // 设置活码使用人/部门对象
            setUserData(employCode, useScopeList, departmentScopeList);
        });
        return weEmployCodeList;
    }

    @Override
    public List<WeEmpleCodeVO> selectGroupWeEmpleCodeList(FindWeEmpleCodeDTO weEmployCode) {
        if (StringUtils.isBlank(weEmployCode.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 获取群活码基础信息
        List<WeEmpleCodeVO> weEmployCodeList = this.selectWeEmpleCodeList(weEmployCode);
        // 转换群活码id列表
        List<Long> employCodeIdList = weEmployCodeList.stream().map(WeEmpleCode::getId).collect(Collectors.toList());
        // 查询已打标签
        List<WeEmpleCodeTag> tagList = weEmpleCodeTagService.selectWeEmpleCodeTagListByIds(employCodeIdList);
        // 设置群活码标签和群活码添加人数
        weEmployCodeList.forEach(employCode -> {
            employCode.setWeEmpleCodeTags(tagList.stream().filter(tag -> tag.getEmpleCodeId().equals(employCode.getId())).collect(Collectors.toList()));
            buildGroupCodeAddCnt(employCode);
        });
        return weEmployCodeList;
    }

    /**
     * 设置群活码进群人数
     *
     * @param employCode {@link WeEmpleCodeVO}
     */
    private void buildGroupCodeAddCnt(WeEmpleCodeVO employCode) {
        if (employCode == null) {
            return;
        }
        //查询群活码
        int count = weEmpleCodeAnalyseService.getAddCountByState(employCode.getState());
        employCode.setCusNumber(count);
    }

    /**
     * 为创建人设置主部门名称
     *
     * @param createByList     创建人名称列表
     * @param corpId           企业ID
     * @param weEmployCodeList 活码列表信息
     */
    private void setCreateByMainDepartMent(List<String> createByList, String corpId, List<WeEmpleCodeVO> weEmployCodeList) {
        if (CollectionUtils.isEmpty(createByList) || CollectionUtils.isEmpty(weEmployCodeList) || StringUtils.isBlank(corpId)) {
            return;
        }
        // 根据创建人姓名列表，获取对应的主部门名称
        List<WeEmpleCodeVO> createByUserDepartment = weUserMapper.selectUserMainDepartmentByUsername(createByList, corpId);
        // 设置部门名称
        weEmployCodeList.forEach(item -> {
            for (WeEmpleCodeVO weEmpleCodeVO : createByUserDepartment) {
                if (item.getCreateBy().equals(weEmpleCodeVO.getUseUserName())) {
                    item.setMainDepartmentName(weEmpleCodeVO.getMainDepartmentName());
                }
            }
        });
    }

    @DataScope
    @Override
    public List<WeEmpleCodeVO> selectAssistantList(FindAssistantDTO dto) {
        if (StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        List<WeEmpleCodeVO> assistantList = this.baseMapper.selectAssistantList(dto);
        if (CollectionUtils.isEmpty(assistantList)) {
            return assistantList;
        }
        List<Long> assistantIdList = assistantList.stream().map(WeEmpleCode::getId).collect(Collectors.toList());
        // 查询使用人
        List<WeEmpleCodeUseScop> useScopeList = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListByIds(assistantIdList, dto.getCorpId());
        // 查询使用部门(查询使用人时需要用businessId关联we_user表，活码使用部门时不传入businessId)
        List<WeEmpleCodeUseScop> departmentScopeList = weEmpleCodeUseScopService.selectDepartmentWeEmpleCodeUseScopListByIds(assistantIdList);
        assistantList.forEach(employCode -> {
            // 设置获客链接使用人/部门对象
            setUserData(employCode, useScopeList, departmentScopeList);

        });
        return assistantList;
    }

    /**
     * 组装数据（员工活码=>素材数据，新客建群=>添加人数、群活码数据、群实际数据）
     *
     * @param employCode employCode
     */
    private void bulidWeEmpleCodeVOData(WeEmpleCodeVO employCode) {
        if (!isEmplyCodeCreate(employCode.getSource())) {
            //查询群活码
            int count = weEmpleCodeAnalyseService.getAddCountByState(employCode.getState());
            employCode.setCusNumber(count);

            String[] materialSort = employCode.getMaterialSort();
            if (materialSort != null && materialSort.length != 0) {
                Long groupCodeId = Long.parseLong(materialSort[0]);
                WeGroupCode groupCode = weGroupCodeService.getById(groupCodeId);
                employCode.setWeGroupCode(groupCode);
                List<WeGroupCodeActual> list = weGroupCodeActualService.selectByGroupCodeId(groupCodeId);
                employCode.setGroupList(list);
            } else {
                employCode.setWeGroupCode(new WeGroupCode());
                employCode.setGroupList(new ArrayList<>());
            }
            if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(employCode.getWelcomeMsgType())) {
                buildEmployCodeMaterial(employCode, employCode.getCorpId());
            }
        } else {
            //查询素材
            buildEmployCodeMaterial(employCode, employCode.getCorpId());
        }
    }

    private void buildExtraMaterial(List<AddWeMaterialDTO> materialList, String corpId) {
        materialList.forEach(item -> {
            if (AttachmentTypeEnum.RADAR.getMessageType().equals(item.getMediaType())) {
                item.setRadar(SpringUtils.getBean(WeRadarService.class).getRadar(corpId, item.getExtraId()));
            } else if (AttachmentTypeEnum.FORM.getMessageType().equals(item.getMediaType())) {
                item.setForm(ExtraMaterialUtils.getForm(item.getExtraId()));
            }
        });
    }

    /**
     * 根据附件排序查找添加素材
     *
     * @param employCode
     * @param corpId
     */
    @Override
    public void buildEmployCodeMaterial(WeEmpleCodeVO employCode, String corpId) {
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(employCode.getWelcomeMsgType())) {
            if (!ArrayUtils.isEmpty(employCode.getMaterialSort())) {
                List<AddWeMaterialDTO> materialList = weMaterialService.getListByMaterialSort(employCode.getMaterialSort(), corpId);
                buildExtraMaterial(materialList, corpId);
                employCode.setMaterialList(materialList);
            } else {
                employCode.setMaterialList(Collections.emptyList());
            }
        } else {
            final WeRedeemCodeActivityVO redeemCodeActivity = weRedeemCodeActivityService.getRedeemCodeActivity(corpId, Long.valueOf(employCode.getCodeActivityId()));
            employCode.setCodeActivity(Optional.ofNullable(redeemCodeActivity).orElseGet(WeRedeemCodeActivityVO::new));

            List<AddWeMaterialDTO> successMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeSuccessMaterialSort(), corpId);
            buildExtraMaterial(successMaterialList, corpId);
            employCode.setCodeSuccessMaterialList(successMaterialList);

            List<AddWeMaterialDTO> failMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeFailMaterialSort(), corpId);
            buildExtraMaterial(failMaterialList, corpId);
            employCode.setCodeFailMaterialList(failMaterialList);

            List<AddWeMaterialDTO> repeatMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeRepeatMaterialSort(), corpId);
            buildExtraMaterial(repeatMaterialList, corpId);
            employCode.setCodeRepeatMaterialList(repeatMaterialList);
        }
    }

    @Override
    @Transactional
    public void refreshCode(List<Long> ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        List<Long> handleIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            handleIds.addAll(ids);
        } else {
            handleIds.addAll(this.baseMapper.selectDepartTypeEmpleCodeIdList());
        }
        if (CollectionUtils.isEmpty(handleIds)) {
            throw new CustomException("没有可处理的数据");
        }

        log.info("[刷新活码处理], 开始处理: size: {}", handleIds.size());
        for (Long handleId : handleIds) {
            WeEmpleCodeVO weEmpleCodeVO = this.selectWeEmpleCodeById(handleId, corpId);
            AddWeEmpleCodeDTO weEmpleCode = new AddWeEmpleCodeDTO();
            BeanUtils.copyProperties(weEmpleCodeVO, weEmpleCode);
            this.updateWeEmpleCode(weEmpleCode);
            log.info("[刷新活码处理] 处理任务: {} 完成", handleId);
        }
        log.info("[刷新活码处理] 结束处理");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String getCustomerLink(Long id) {
        if(id == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING) ;
        }
        // 获取活码信息
        WeEmpleCode weEmpleCode = getById(id);
        if(weEmpleCode == null) {
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }
        // 如果已经生成过则直接返回
        if(StringUtils.isNotBlank(weEmpleCode.getCustomerLink())) {
            return weEmpleCode.getCustomerLink();
        }

        String longUrl = genCustomerEmployCodedLongUrl(id, weEmpleCode.getCorpId());
        // 没有则 生成一个活码小程序短链
        CustomerEmpleCodeShortUrlAppendInfo appendInfo = CustomerEmpleCodeShortUrlAppendInfo.builder()
                .id(id)
                .corpId(weEmpleCode.getCorpId())
                .build();
        String shortUrl = customerEmployCodeShortUrlHandler.createShortUrl(weEmpleCode.getCorpId(), longUrl, LoginTokenService.getUsername(), appendInfo);
        if(StringUtils.isBlank(shortUrl)) {
            throw new CustomException(ResultTip.TIP_ERROR_CREATING_APP_lINK);
        }
        //保存到活码
        weEmpleCode.setCustomerLink(shortUrl);
        updateById(weEmpleCode);
        return shortUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String genCustomerEmployQrCode(GenCustomerEmployQrcodeDTO genCustomerEmployQrcodeDTO) {

        Long id = SnowFlakeUtil.nextId();

        // 1.查询原活码活码范围设置, 请求企微生成活码
        WeEmpleCode weEmpleCode = getById(genCustomerEmployQrcodeDTO.getEmployCodeId());
        if(weEmpleCode == null){
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }
        List<WeEmpleCodeUseScop> weEmpleCodeUseScops = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListById(weEmpleCode.getId(), weEmpleCode.getCorpId());

        WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
        //根据类型生成相应的活码
        weContactWay.setType(weEmpleCode.getCodeType());
        weContactWay.setScene(WeConstans.QR_CODE_EMPLE_CODE_SCENE);
        weContactWay.setSkip_verify(WeEmployCodeSkipVerifyEnum.isPassByNow(weEmpleCode.getSkipVerify(), weEmpleCode.getEffectTimeOpen(), weEmpleCode.getEffectTimeClose()));
        State state = State.valueOf(genCustomerEmployQrCodeState(id));
        weContactWay.setState(state.getState());
        weContactWay.setUser(weEmpleCodeUseScops.stream().map(WeEmpleCodeUseScop::getBusinessId).toArray(String[]::new));
        WeExternalContactDTO contactDTO = getQrCodeFromClient(weContactWay, weEmpleCode.getCorpId());
        log.info("[生成客户专属活码] 活码信息:{}", JSON.toJSONString(contactDTO));

        // 2.保存客户专属活码信息
        weCustomerTempEmpleCodeSettingService.save(buildCustomerEmployCodeInfo(contactDTO, genCustomerEmployQrcodeDTO, state, weEmpleCode.getId(),weEmpleCode.getCorpId()));
        return contactDTO.getQr_code();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCustomerEmployCodeTagSelectScope(TagSelectScopeDTO tagSelectScopeDTO, LoginUser loginUser) {
        // 校验参数
        CustomerCodeValidator.validateSelectTag(tagSelectScopeDTO);

        selectTagScopeService.remove(new LambdaQueryWrapper<WeCustomerTempEmpleCodeSelectTagScope>()
                .eq(WeCustomerTempEmpleCodeSelectTagScope::getOriginEmpleCodeId, tagSelectScopeDTO.getOriginEmpleId())
                .eq(WeCustomerTempEmpleCodeSelectTagScope::getCorpId, loginUser.getCorpId())
        );

        // 没有传可选标签,则默认按原来的全部标签显示, 这里不处理
        if (CollectionUtils.isEmpty(tagSelectScopeDTO.getSelectTagIdList()) && CollectionUtils.isEmpty(tagSelectScopeDTO.getSelectGroupIdList())) {
            return;
        }



        List<WeCustomerTempEmpleCodeSelectTagScope> saveList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(tagSelectScopeDTO.getSelectTagIdList())){
            tagSelectScopeDTO.getSelectTagIdList().forEach(tagId -> {
                WeCustomerTempEmpleCodeSelectTagScope tagScope = new WeCustomerTempEmpleCodeSelectTagScope();
                tagScope.setCorpId(loginUser.getCorpId());
                tagScope.setOriginEmpleCodeId(Long.valueOf(tagSelectScopeDTO.getOriginEmpleId()));
                tagScope.setType(SelectTagScopeTypeEnum.TAG.getCode());
                tagScope.setValue(tagId);
                tagScope.setCreateBy(loginUser.getUserId());
                saveList.add(tagScope);
            });
        }
        if(CollectionUtils.isNotEmpty(tagSelectScopeDTO.getSelectGroupIdList())){
            tagSelectScopeDTO.getSelectGroupIdList().forEach(groupId -> {
                WeCustomerTempEmpleCodeSelectTagScope tagScope = new WeCustomerTempEmpleCodeSelectTagScope();
                tagScope.setCorpId(loginUser.getCorpId());
                tagScope.setOriginEmpleCodeId(Long.valueOf(tagSelectScopeDTO.getOriginEmpleId()));
                tagScope.setType(SelectTagScopeTypeEnum.TAG_GROUP.getCode());
                tagScope.setValue(groupId);
                tagScope.setCreateBy(loginUser.getUserId());
                saveList.add(tagScope);
            });
        }
        // 保存活码对应的标签列表
        selectTagScopeService.saveBatch(saveList);
    }

    @Override
    public SelectTagVO customerEmployCodeTagSelectScopeDetail(String empleCodeId, LoginUser loginUser) {
        CustomerCodeValidator.validateSelectTagDetail(empleCodeId);

        List<WeCustomerTempEmpleCodeSelectTagScope> selectTagScopes = selectTagScopeMapper.selectList(new LambdaQueryWrapper<WeCustomerTempEmpleCodeSelectTagScope>()
                .eq(WeCustomerTempEmpleCodeSelectTagScope::getCorpId, loginUser.getCorpId())
                .eq(WeCustomerTempEmpleCodeSelectTagScope::getOriginEmpleCodeId, Long.valueOf(empleCodeId)));
        if (CollectionUtils.isEmpty(selectTagScopes)) {
            return new SelectTagVO();
        }
        List<String> totalGroupIdList = selectTagScopes.stream().filter(it -> it.getType().equals(SelectTagScopeTypeEnum.TAG_GROUP.getCode())).map(WeCustomerTempEmpleCodeSelectTagScope::getValue).collect(Collectors.toList());
        List<String> tagIdList = selectTagScopes.stream().filter(it -> it.getType().equals(SelectTagScopeTypeEnum.TAG.getCode())).map(WeCustomerTempEmpleCodeSelectTagScope::getValue).collect(Collectors.toList());
        // 查询分组id和名称的映射

        Map<String, String> tagMapping = CollectionUtils.isEmpty(tagIdList) ? new HashMap<>() : tagMapper.selectList(new LambdaQueryWrapper<WeTag>()
                .eq(WeTag::getCorpId, loginUser.getCorpId())
                .in(WeTag::getTagId, tagIdList)
                .eq(WeTag::getStatus, Constants.NORMAL_CODE)
        ).stream().collect(Collectors.toMap(WeTag::getTagId, WeTag::getName, (v1, v2) -> v1));;

        Map<String, String> tagGroupMapping = CollectionUtils.isEmpty(totalGroupIdList) ? new HashMap<>() : tagGroupMapper.selectList(new LambdaQueryWrapper<WeTagGroup>()
                .eq(WeTagGroup::getCorpId, loginUser.getCorpId())
                .in(WeTagGroup::getGroupId, totalGroupIdList)
                .eq(WeTagGroup::getStatus, Constants.NORMAL_CODE)
        ).stream().collect(Collectors.toMap(WeTagGroup::getGroupId, WeTagGroup::getGroupName, (v1, v2) -> v1));
        List<TagGroupInfoModel> selectGroupList = new ArrayList<>();
        List<TagInfoModel> selectTagList = new ArrayList<>();
        // 查询标签id和名称的映射
        for (String groupId : totalGroupIdList) {
            String groupName = tagGroupMapping.get(groupId);
            // 排除被删除的
            if(groupName == null){
                continue;
            }
            selectGroupList.add(new TagGroupInfoModel(groupId, groupName));
        }
        for (String tagId : tagIdList) {
            String tagName = tagMapping.get(tagId);
            // 排除被删除的
            if (tagName == null) {
                continue;
            }
            selectTagList.add(new TagInfoModel(tagId, tagName));
        }

        return new SelectTagVO(selectGroupList, selectTagList);
    }

    @Override
    public void sendUserEmpleCodeWelcomeMsg(State state, String welcomeCode, CustomerId customerId, State originState) {
        // 构建欢迎语消息
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        EmplyCodeWelcomeMsgInfo messageMap = selectWelcomeMsgByState(state.getState(), customerId.getCorpId());
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            buildCommonWelcomeMsg(messageMap, customerId.getCorpId(), customerId.getExternalUserid());
            //给好友发送消息
            welcomeMsgTaskExecutor.submit(() -> {
                try {
                    sendMessageToNewExternalUserId(weWelcomeMsgBuilder, messageMap, getCustomerName(customerId.getCorpId(),customerId.getExternalUserid()), customerId, originState);
                } catch (Exception e) {
                    log.error("[员工活码欢迎语] 发送欢迎语异常：e:{}", ExceptionUtils.getStackTrace(e));
                }
            });
        } else if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType())) {
            handleRedeemCodeWelcomeMsg(customerId, weWelcomeMsgBuilder, messageMap, getCustomerName(customerId.getCorpId(), customerId.getExternalUserid()));
        }
    }

    @Override
    public void sendCustomerTempEmpleCodeWelcomeMsg(State state, String welcomeCode, CustomerId customerId) {
        try {
            WeCustomerTempEmpleCodeSetting customerTempEmpleCodeSetting = weCustomerTempEmpleCodeSettingService.getByState(state.getState(), customerId.getCorpId());
            if (customerTempEmpleCodeSetting == null || customerTempEmpleCodeSetting.getOriginEmpleCodeId() == null) {
                log.info("[客户专属活码] 发送欢迎语失败, 原活码id为空");
                return;
            }
            // 将存储的原活码id作为state作为参数, 发送欢迎语
            sendUserEmpleCodeWelcomeMsg(State.valueOf(String.valueOf(customerTempEmpleCodeSetting.getOriginEmpleCodeId())), welcomeCode, customerId, state);
        } catch (Exception e) {
            log.info("[客户专属活码] 发送欢迎语异常: {}",ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void customerTempEmpleCodeCallBackHandle(State state, CustomerId customerId) {
        log.info("客户专属活码处理开始>>>>>>>>>>>>>>>");
        try {
            WeCustomerTempEmpleCodeSetting weCustomerTempEmpleCodeSetting = weCustomerTempEmpleCodeSettingService.getByState(state.getState(), customerId.getCorpId());
            if(weCustomerTempEmpleCodeSetting == null){
                log.info("[客户专属活码处理], 不存在客户专属活码设置, 不处理, state: {}", state);
                return;
            }
            EmplyCodeWelcomeMsgInfo emplyeCodeSetting = selectWelcomeMsgByState(String.valueOf(weCustomerTempEmpleCodeSetting.getOriginEmpleCodeId()), customerId.getCorpId());
            if (emplyeCodeSetting == null) {
                log.info("[客户专属活码处理], 客户专属活码对应的员工共活码不存在, 不处理, 原活码id: {}", weCustomerTempEmpleCodeSetting.getOriginEmpleCodeId());
                return;
            }

            updateCustomerEmpleCodeData(emplyeCodeSetting.getEmpleCodeId(), customerId, state.getState());

            // 构建客户专属员工活码给客户打标签和打备注上下文
            MarkContent markContent = buildMarkTagContextByCustomerEmpleCode(state.getState(), customerId);

            //为外部联系人添加员工活码标签
            markEmplyCodeTag(customerId, emplyeCodeSetting.getEmpleCodeId(), markContent);

            // 打标签后休眠1S , 避免出现打标签后又打备注提示接口调用频繁 Tower 任务: 客户扫活码加好友之后没有自动备注 ( https://tower.im/teams/636204/todos/69053 )
            ThreadUtil.safeSleep(1000L);

            //判断是否需要设置备注
            editEmplyCodeExternalUser(customerId, markContent);
        } catch (Exception e) {
            log.error("[客户专属活码处理] 未知异常 e={}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 给员工活码加入的客户设置信息
     *
     * @param customerId         客户id
     * @param markContent     客户数据上下文
     */
    private void editEmplyCodeExternalUser(CustomerId customerId,
                                           MarkContent markContent) {
        if (markContent == null || customerId.invalid()) {
            log.error("[员工活码打备注] 客户参数异常, customerId={}, markTagContent: {}", customerId, markContent);
            return;
        }

        log.info("[员工活码打备注] 开始更新客户数据");
        // 查询外部联系人的信息
        WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getExternalUserid, customerId.getExternalUserid())
                .eq(WeCustomer::getCorpId, customerId.getCorpId())
        );

        WeCustomer updateWeCustomer = new WeCustomer();
        updateWeCustomer.setCorpId(customerId.getCorpId());
        updateWeCustomer.setUserId(customerId.getUserId());
        updateWeCustomer.setExternalUserid(customerId.getExternalUserid());
        updateWeCustomer.setRemark(buildNewRemark(markContent, weCustomer == null? "" : weCustomer.getName()));
        // 处理自定义字段
        updateWeCustomer.setExtendProperties(buildExtendedProperties(customerId, markContent.getExtendedProperties()));
        // 处理自定义字段里的系统字段, 如手机号和描述
        updateWeCustomer.setPhone(buildProperties(markContent.getExtendedProperties(), PHONE));
        updateWeCustomer.setDesc(buildProperties(markContent.getExtendedProperties(), DESC));
        updateWeCustomer.setAddress(buildProperties(markContent.getExtendedProperties(), ADDRESS));
        if(CollectionUtils.isNotEmpty(markContent.getExtendedProperties())){
            Optional<CustomerExtendedProperties> exist = markContent.getExtendedProperties().stream().filter(it -> CustomerExtendPropertyEnum.LOCATION.getType().equals(it.getType())).findFirst();
            try {
                exist.ifPresent(updateWeCustomer::fillLocationInfo);
            } catch (Exception e) {
                log.info("[员工活码打备注] 设置客户地址参数异常,userId: {}, externalUserid: {} e: {}", customerId.getUserId(), customerId.getExternalUserid(), ExceptionUtils.getStackTrace(e));
            }
        }
        String birthdayValue = buildProperties(markContent.getExtendedProperties(), BIRTHDAY);
        try {
            if(StringUtils.isNotBlank(birthdayValue)){
                updateWeCustomer.setBirthday(DateUtil.parseDate(birthdayValue));
            }
        } catch (Exception e) {
            log.info("[员工活码打备注] 出生日期格式不正确: {}", birthdayValue);
        }
        updateWeCustomer.setEmail(buildProperties(markContent.getExtendedProperties(), EMAIL));
        try {
            weCustomerService.updateWeCustomerInfo(updateWeCustomer);
        } catch (Exception e) {
            log.error("[员工活码打备注] 未知异常: {} customerId={}", ExceptionUtils.getStackTrace(e) ,customerId);
        }
    }
    /**
     * 从设置的扩展属性中提取客户属性字段
     * @param extendedProperties 扩展属性
     * @param name 属性name
     * @return 属性值
     */
    private String buildProperties(List<CustomerExtendedProperties> extendedProperties, String name) {
        for (CustomerExtendedProperties properties : extendedProperties) {
            if(CustomerExtendPropertyEnum.SYS_DEFAULT.getType().equals(properties.getType()) && properties.getName().equals(name)){
                return properties.getValue();
            }
        }
        return null;
    }


    /**
     * 构建扩展属性关系列表
     * @param customerId 客户id
     * @param extendedProperties 选择的扩展属性
     * @return 扩展属性关系列表
     */
    private List<BaseExtendPropertyRel> buildExtendedProperties(CustomerId customerId, List<CustomerExtendedProperties> extendedProperties) {
        if (CollectionUtils.isEmpty(extendedProperties)) {
            return new ArrayList<>();
        }

        List<BaseExtendPropertyRel> extendPropertyRelList = extendedProperties.stream().map(it -> {
            BaseExtendPropertyRel baseExtendPropertyRel = new BaseExtendPropertyRel();
            baseExtendPropertyRel.setCorpId(customerId.getCorpId());
            baseExtendPropertyRel.setExternalUserid(customerId.getExternalUserid());
            baseExtendPropertyRel.setUserId(customerId.getUserId());
            baseExtendPropertyRel.setExtendPropertyId(it.getId());
            baseExtendPropertyRel.setPropertyValue(it.getValue());
            baseExtendPropertyRel.setPropertyType(it.getType());
            baseExtendPropertyRel.setPropertyName(it.getName());
            return baseExtendPropertyRel;
        }).collect(Collectors.toList());
        return extendPropertyRelList;
    }

    /**
     * 构建备注
     * @param markContent 打标记上下文
     * @param nickName 企微号昵称
     * @return 新备注
     */
    private String buildNewRemark(MarkContent markContent, String nickName) {
        if (!markContent.isMarkRemark() || StringUtils.isBlank(markContent.getRemarkName())) {
            log.info("[员工活码打备注] 不打备注 isMarkRemark: {}, remarkType: {},remarkName: {}", markContent.isMarkRemark(), markContent.getRemarkType(), markContent.getRemarkName());
            return "";
        }
        String newRemark;
        if (WeEmployCodeRemarkTypeEnum.BEFORT_NICKNAME.getRemarkType().equals(markContent.getRemarkType())) {
            newRemark = markContent.getRemarkName() + "-" + nickName;
        } else {
            newRemark = nickName + "-" + markContent.getRemarkName();
        }
        return newRemark;
    }

    /**
     * 设置员工活码标签
     *
     * @param customerId    weFlowerCustomerRel
     * @param empleCodeId         员工活码主键ID
     * @param markContent             标记上下文
     */
    private void markEmplyCodeTag(CustomerId customerId, String empleCodeId, MarkContent markContent) {
        if (customerId.invalid() || org.apache.commons.lang3.StringUtils.isBlank(empleCodeId)) {
            log.warn("[员工活码打标签] 参数异常, empleCodeId={},customerId={}", empleCodeId, customerId);
            return;
        }
        if (!markContent.isMarkTag()) {
            log.info("[员工活码打标签] 未开启打标签, empleCodeId: {}", empleCodeId);
            return;
        }
        try {
            // 获取员工详情
            WeUser weUser = weUserService.getUserDetail(customerId.getCorpId(), customerId.getUserId());
            if (weUser == null) {
                log.info("[员工活码打标签] 员工活码,查询不到员工信息,corpId:{},userId:{}", customerId.getCorpId(), customerId.getUserId());
                return;
            }
            //查询活码对应标签
            List<String> tagIdList = markContent.getTagIdList();
            //存在则打标签
            if (CollectionUtils.isNotEmpty(tagIdList)) {
                log.info("[员工活码打标签] 开始批量打标签！");
                //查询这个tagId对应的groupId
                // 获取有效的标签名称
                weCustomerService.singleMarkLabel(customerId.getCorpId(), customerId.getUserId(), customerId.getExternalUserid(), tagIdList, weUser.getName());
                recordCodeTag(customerId.getCorpId(), weUser, customerId.getExternalUserid(), tagIdList, empleCodeId);
            }
        } catch (Exception e) {
            log.error("[员工活码打标签] 未知异常, empleCodeId={}, e={}", empleCodeId, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 记录扫描员工活码打标签的信息动态
     *
     * @param corpId 公司id
     * @param weUser 员工信息
     * @param externalUserId 客户id
     * @param tagIdList 标签id列表
     * @param empleCodeId 活码id
     */
    public void recordCodeTag(String corpId, WeUser weUser, String externalUserId, List<String> tagIdList,String empleCodeId){
        List<String> tagNameList = weTagService.getTagNameByIds(tagIdList);
        if (StringUtils.isAnyBlank(corpId, externalUserId) || CollUtil.isEmpty(tagNameList)||Objects.isNull(weUser)) {
            log.info("记录工活码打标签的信息动态时员工，客户id，公司id,标签列表,活码id不能为空，userId：{}，externalUserId：{}，corpId：{}，addTagIds: {},empleCodeId:{}", weUser, externalUserId, corpId, tagNameList,empleCodeId);
            return;
        }
        WeEmpleCode weEmpleCode= baseMapper.selectById(empleCodeId);
        if (Objects.isNull(weEmpleCode)){
            log.info("记录工活码打标签的信息动态时,查询员工活码信息异常");
            return;
        }
        TagRecordUtil tagRecordUtil=new TagRecordUtil();
        String content = tagRecordUtil.buildCodeContent(weEmpleCode.getScenario(),weUser.getName());
        String detail = String.join(",", tagNameList);
        //保存信息动态
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId,weUser.getUserId(),externalUserId,content,detail);
    }
    /**
     * 根据客户员工代码构建标记标签上下文
     * 此方法用于构建客户专属的标记内容，主要处理标签和备注信息
     *
     * @param state 客户状态，用于获取临时员工代码设置
     * @param customerId 客户ID，包含企业ID，用于获取临时员工代码设置
     * @return 返回一个构建好的MarkContent对象，包含标记标签和备注信息
     */
    private MarkContent buildMarkTagContextByCustomerEmpleCode(String state, CustomerId customerId) {
        WeCustomerTempEmpleCodeSetting weCustomerTempEmpleCodeSetting = weCustomerTempEmpleCodeSettingService.getByState(state, customerId.getCorpId());
        MarkContent markContent = new MarkContent();

        // 如果客户专属活码有配置标签,则忽略原活码的设置, 否则使用原活码的设置
        boolean needMarkCustomerTag = StringUtils.isNotBlank(weCustomerTempEmpleCodeSetting.getTagIds());
        if (needMarkCustomerTag) {
            markContent.setTagIdList(Arrays.asList(weCustomerTempEmpleCodeSetting.getTagIds().split(",")));
            markContent.setMarkTag(true);
        }

        // 备注
        markContent.setMarkRemark(weCustomerTempEmpleCodeSetting.getRemarkOpen());
        markContent.setRemarkType(weCustomerTempEmpleCodeSetting.getRemarkType() == null ? WeEmployCodeRemarkTypeEnum.NO.getRemarkType() : weCustomerTempEmpleCodeSetting.getRemarkType());
        markContent.setRemarkName(weCustomerTempEmpleCodeSetting.getRemarkName());

        // 构建扩展属性
        List<CustomerExtendedProperties> customerExtendedProperties = JSON.parseArray(weCustomerTempEmpleCodeSetting.getCustomerExtendInfo(), CustomerExtendedProperties.class);
        markContent.setExtendedProperties(customerExtendedProperties);
        return markContent;
    }
    @Override
    public void empleCodeCallBackHandle(State state, CustomerId customerId) {
        log.info("员工活码处理开始>>>>>>>>>>>>>>>");
        try {
            EmplyCodeWelcomeMsgInfo emplyeCodeSetting = selectWelcomeMsgByState(state.getState(), customerId.getCorpId());
            if (emplyeCodeSetting == null) {
                return;
            }

            // 构建员工活码给客户打标签和打备注上下文
            updateCustomerEmpleCodeData(emplyeCodeSetting.getEmpleCodeId(), customerId, state.getState());

            //为外部联系人添加员工活码标签
            MarkContent markContent = buildMarkTagContextByEmpleCode(emplyeCodeSetting.getEmpleCodeId(),
                    emplyeCodeSetting.getTagFlag(),
                    emplyeCodeSetting.getRemarkType(),
                    emplyeCodeSetting.getRemarkName()
            );
            markEmplyCodeTag(customerId, emplyeCodeSetting.getEmpleCodeId(), markContent);
            // 打标签后休眠1S , 避免出现打标签后又打备注提示接口调用频繁 Tower 任务: 客户扫活码加好友之后没有自动备注 ( https://tower.im/teams/636204/todos/69053 )
            ThreadUtil.safeSleep(1000L);
            //判断是否需要设置备注
            editEmplyCodeExternalUser(customerId, markContent);
        } catch (Exception e) {
            log.error("[员工活码处理开始] 未知异常 e={}", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void sendCustomerAssistantWelcomeMsg(State state, String welcomeCode, CustomerId customerId) {
        WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder = WeWelcomeMsg.builder().welcome_code(welcomeCode);
        EmplyCodeWelcomeMsgInfo emplyCodeWelcomeMsgInfo = selectWelcomeMsgByState(state.getState(), customerId.getCorpId());
        if (emplyCodeWelcomeMsgInfo == null) {
            // 将"hk_"前缀截取掉，得到渠道id作为state信息
            String channelId = state.getState().replace(CustomerAssistantConstants.STATE_PREFIX, StringUtils.EMPTY);
            // 根据渠道id获取渠道对应的获客链接id信息
            WeEmpleCodeChannel channel = weEmpleCodeChannelMapper.getChannelById(channelId);
            // 若根据渠道id未查询到信息，则表示不是从获客链接添加的客户，停止处理
            if (channel == null) {
                log.info("[获客链接欢迎语] 未查询到该state对应的获客链接信息，state:{},corpId:{},userId:{},externalUserId:{}", state.getState(), customerId.getCorpId(), customerId.getUserId(), customerId.getExternalUserid());
                return;
            }
            emplyCodeWelcomeMsgInfo = selectWelcomeMsgById(String.valueOf(channel.getEmpleCodeId()), customerId.getCorpId());
        }
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(emplyCodeWelcomeMsgInfo.getWelcomeMsgType())) {
            buildCustomerAssistantWelcomeMsg(emplyCodeWelcomeMsgInfo, customerId.getCorpId());
            //给好友发送消息
            EmplyCodeWelcomeMsgInfo finalMessageMap = emplyCodeWelcomeMsgInfo;
            welcomeMsgTaskExecutor.submit(() -> {
                try {
                    sendMessageToNewExternalUserId(weWelcomeMsgBuilder, finalMessageMap, getCustomerName(customerId.getCorpId(), customerId.getExternalUserid()), customerId, state);

                } catch (Exception e) {
                    log.error("[获客链接欢迎语] 异步发送欢迎语消息异常：ex:{}", ExceptionUtils.getStackTrace(e));
                }
            });

        } else if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(emplyCodeWelcomeMsgInfo.getWelcomeMsgType())) {
            handleRedeemCodeWelcomeMsg(customerId, weWelcomeMsgBuilder, emplyCodeWelcomeMsgInfo, getCustomerName(customerId.getCorpId(), customerId.getExternalUserid()));
        }
    }

    /**
     * 构建员工活码给客户打标签和打备注上下文
     * @param empleCodeId 活码id
     * @param tagFlag 是否打标签标识
     * @param remarkType 打备注类型
     * @param remarkName 备注名称
     * @return  MarkContent
     */
    private MarkContent buildMarkTagContextByEmpleCode(String empleCodeId, boolean tagFlag, Integer remarkType, String remarkName) {
        List<String> tagIdList = weEmpleCodeTagService.list(new LambdaQueryWrapper<WeEmpleCodeTag>()
                        .eq(WeEmpleCodeTag::getEmpleCodeId, empleCodeId))
                .stream().map(WeEmpleCodeTag::getTagId).filter(Objects::nonNull).collect(Collectors.toList());

        return MarkContent.builder()
                .isMarkTag(tagFlag)
                .tagIdList(tagIdList)
                .isMarkRemark(!WeEmployCodeRemarkTypeEnum.NO.getRemarkType().equals(remarkType))
                .remarkType(remarkType)
                .remarkName(remarkName)
                .extendedProperties(new ArrayList<>())
                .build();
    }

    /**
     * 更新客户活码数据信息
     *
     * @param empleCodeId 活码ID
     * @param customerId 客户ID对象，包含企业ID、用户ID和外部用户ID
     * @param state 客户状态
     */
    private void updateCustomerEmpleCodeData(String empleCodeId, CustomerId customerId, String state) {
        //更新活码数据统计
        weEmpleCodeAnalyseService.saveWeEmpleCodeAnalyse(customerId.getCorpId(), customerId.getUserId(), customerId.getExternalUserid(), empleCodeId, true);
        // 更新Redis中的数据
        empleStatisticRedisCache.addNewCustomerCnt(customerId.getCorpId(), DateUtils.dateTime(new Date()), Long.valueOf(empleCodeId), customerId.getUserId());

        //查询外部联系人与通讯录关系数据, 更新活码添加方式
        WeFlowerCustomerRel weFlowerCustomerRel = weFlowerCustomerRelService
                .getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getUserId, customerId.getUserId())
                        .eq(WeFlowerCustomerRel::getExternalUserid, customerId.getExternalUserid())
                        .eq(WeFlowerCustomerRel::getCorpId, customerId.getCorpId())
                        .eq(WeFlowerCustomerRel::getStatus, 0)
                );
        weFlowerCustomerRel.setState(state);
        SensitiveFieldProcessor.processForSave(weFlowerCustomerRel);
        weFlowerCustomerRelService.updateById(weFlowerCustomerRel);
    }
    /**
     * 处理兑换码欢迎语
     *
     * @param customerId            {@link CustomerId}
     * @param weWelcomeMsgBuilder {@link WeWelcomeMsg.WeWelcomeMsgBuilder}
     * @param messageMap          {@link EmplyCodeWelcomeMsgInfo}
     * @param customerName        {@link WeFlowerCustomerRel}
     */
    private void handleRedeemCodeWelcomeMsg(CustomerId customerId, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, EmplyCodeWelcomeMsgInfo messageMap, String customerName) {
        RLock rLock = null;
        boolean isHaveLock;
        try {
            final String redeemCodeKey = RedeemCodeConstants.getRedeemCodeKey(customerId.getCorpId(), messageMap.getCodeActivityId());
            rLock = LockUtil.getLock(redeemCodeKey);
            isHaveLock = rLock.tryLock(RedeemCodeConstants.CODE_WAIT_TIME, RedeemCodeConstants.CODE_LEASE_TIME, TimeUnit.SECONDS);
            if (isHaveLock) {
                buildRedeemCodeActivityWelcomeMsg(messageMap, customerId.getCorpId(), customerId.getExternalUserid());
                //同步发送消息
                sendMessageToNewExternalUserId(weWelcomeMsgBuilder, messageMap, customerName, customerId, null);
                log.info("[活动欢迎语] 活动欢迎语处理完成，活动id:{},corpId:{}}", messageMap.getCodeActivityId(), customerId.getCorpId());
            }
        } catch (InterruptedException e) {
            log.error("[活动欢迎语] 活动欢迎语获取锁失败,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), messageMap.getCodeActivityId(), customerId.getCorpId());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[活动欢迎语] 拼装活动欢迎语 或 同步发送欢迎语消息异常,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), messageMap.getCodeActivityId(), customerId.getCorpId());
        } finally {
            if(rLock != null && rLock.isHeldByCurrentThread()){
                rLock.unlock();
            }
        }
    }

    /**
     * 获取客户名称
     *
     * @param corpId         企业Id
     * @param externalUserid 客户id
     * @return 客户名称
     */
    public String getCustomerName(String corpId, String externalUserid) {
        GetExternalDetailResp resp = weCustomerClient.getV2(externalUserid, corpId);
        if (resp == null || resp.getExternalContact() == null || StringUtils.isBlank(resp.getExternalContact()
                .getName())) {
            return StringUtils.EMPTY;
        }
        return resp.getExternalContact().getName();
    }

    /**
     * 给好友发送消息
     */
    private void sendMessageToNewExternalUserId(WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder,
                                                EmplyCodeWelcomeMsgInfo messageMap, String remark,
                                                CustomerId customerId, State originState) {
        log.debug("[发送欢迎语] 欢迎语活码配置：{}", JSON.toJSONString(messageMap));
        // 1.构建欢迎语
        // 存在欢迎语则替换标签文本进行构建
        String replyText = weMsgTlpMaterialService.replyTextIfNecessary(messageMap.getWelcomeMsg(), remark, messageMap.getRedeemCode(), customerId);
        Optional.ofNullable(replyText).ifPresent(text -> weWelcomeMsgBuilder.text(Text.builder().content(text).build()));

        // 2.构建附件
        List<Attachment> attachmentList = new ArrayList<>();
        // 2.1 新客先添加入群二维码
        if (EmployCodeSourceEnum.NEW_GROUP.getSource().equals(messageMap.getSource())) {
            // 新客拉群创建的员工活码欢迎语图片(群活码图片)
            String codeUrl = messageMap.getGroupCodeUrl();
            if (StringUtils.isNotBlank(codeUrl)) {
                buildWelcomeMsgImg(customerId.getCorpId(), codeUrl, getGroupCodeFilename(codeUrl, customerId.getCorpId()), attachmentList);
            }
        }
        // 欢迎语发送素材
        if (CollectionUtils.isNotEmpty(messageMap.getMaterialList())) {
            //数量超出上限抛异常
            if (messageMap.getMaterialList().size() > WeConstans.MAX_ATTACHMENT_NUM) {
                throw new CustomException(ResultTip.TIP_ATTACHMENT_OVER);
            }
            buildWeEmplyWelcomeMsg(messageMap.getSource(), messageMap.getScenario(), customerId.getUserId(), customerId.getCorpId(), messageMap.getMaterialList(), attachmentList);
        }

        // 3.调用企业微信接口发送欢迎语
        weCustomerService.sendWelcomeMsg(weWelcomeMsgBuilder.attachments(attachmentList).build(), customerId.getCorpId());
        SpringUtil.getApplicationContext().publishEvent(new SendWelcomeMsgSuccessEvent(
                customerId,
                messageMap,
                originState
        ));
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(messageMap.getWelcomeMsgType()) && messageMap.isHaveCodeActivityMsg()) {
            // 4.更新兑换码的发送状态
            if (StringUtils.isNotBlank(messageMap.getRedeemCode())) {
                log.debug("[发送欢迎语] 更新活动欢迎语");
                weRedeemCodeService.updateRedeemCode(WeRedeemCodeDTO.builder()
                        .activityId(Long.valueOf(messageMap.getCodeActivityId()))
                        .code(messageMap.getRedeemCode())
                        .corpId(customerId.getCorpId())
                        .receiveUserId(customerId.getExternalUserid()).build());
            }
        }
    }

    /**
     * 构建活码欢迎语资源
     * @param source 员工活码来源 {@link EmployCodeSourceEnum}
     * @param scenario 场景
     * @param userId 员工id
     * @param corpId 企业id
     * @param weMaterialList 媒体资源列表
     * @param attachmentList 发送资源列表
     */
    private void buildWeEmplyWelcomeMsg(Integer source, String scenario, String userId, String corpId, List<AddWeMaterialDTO> weMaterialList, List<Attachment> attachmentList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(weMaterialList)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        Attachments attachments;
        for (AddWeMaterialDTO weMaterialVO : weMaterialList) {
            AttachmentTypeEnum typeEnum = AttachmentTypeEnum.mappingFromGroupMessageType(weMaterialVO.getMediaType());
            if (typeEnum == null) {
                log.error("type is error!!!, type: {}", weMaterialVO.getMediaType());
                continue;
            }
            AttachmentParam param = AttachmentParam.costFromWeMaterialByType(source, scenario, userId, corpId, weMaterialVO, typeEnum);
            attachments = attachmentService.buildAttachment(param, corpId);
            //            attachments = weMsgTlpMaterialService.buildByWelcomeMsgType(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), typeEnum, corpId);
            if (attachments != null) {
                attachmentList.add(attachments);
            } else {
                log.error("type error!! mediaType={}", weMaterialVO.getMediaType());
            }
        }
    }


    /**
     * 获取群聊二维码的图片文件名称
     *
     * @param codeUrl 群二维码URL
     * @param corpId 企业ID
     * @return 图片文件名称
     */
    private String getGroupCodeFilename(String codeUrl, String corpId) {
        if (StringUtils.isAnyBlank(codeUrl, corpId)) {
            log.info("[入群欢迎语] 获取不到发送的群二维码信息，codeUrl:{}, corpId:{}", codeUrl, corpId);
            return StringUtils.EMPTY;
        }
        if (!codeUrl.startsWith(Constants.RESOURCE_PREFIX)) {
            return codeUrl.replaceAll(ruoYiConfig.getFile().getCos().getCosImgUrlPrefix(), StringUtils.EMPTY);
        }
        // 获取企业配置的H5Domain域名信息
        WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (weCorpAccount == null) {
            return StringUtils.EMPTY;
        }
        return codeUrl.replaceAll(weCorpAccount.getH5DoMainName() + WeConstans.SLASH + Constants.RESOURCE_PREFIX, StringUtils.EMPTY);
    }

    /**
     * 构建欢迎语的图片部分
     *
     * @param picUrl         图片链接
     * @param fileName       图片名称
     * @param attachmentList
     */
    private void buildWelcomeMsgImg(String corpId, String picUrl, String fileName, List<Attachment> attachmentList) {

        AttachmentParam param = AttachmentParam.builder().picUrl(picUrl).content(fileName).typeEnum(AttachmentTypeEnum.IMAGE).build();
        Attachments attachments = attachmentService.buildAttachment(param, corpId);
        if (attachments != null) {
            attachmentList.add(attachments);
        }
        //        Optional.ofNullable(weMediaDto).ifPresent(media -> builder.image(Image.builder().media_id(media.getMedia_id()).pic_url(media.getUrl()).build()));
    }
    /**
     * 构建客户专属活码信息。
     * <p>
     * 该方法根据外部联系人信息和生成二维码所需的信息构建一个临时员工活码设置对象。
     *
     * @param contactDTO                 包含外部联系人信息的数据传输对象
     * @param genCustomerEmployQrcodeDTO 包含生成二维码所需信息的数据传输对象
     * @param state                      联系我state回调参数
     * @param originEmpleCodeId          原始员工活码ID
     * @param corpId                     企业ID
     * @return 构建的客户专属活码设置对象
     */
    private WeCustomerTempEmpleCodeSetting buildCustomerEmployCodeInfo(WeExternalContactDTO contactDTO, GenCustomerEmployQrcodeDTO genCustomerEmployQrcodeDTO, State state, Long originEmpleCodeId, String corpId) {

        return WeCustomerTempEmpleCodeSetting.builder()
                .id(state.unWrapCustomerEmployCodeId())
                .corpId(corpId)
                .originEmpleCodeId(originEmpleCodeId)
                .remarkOpen(genCustomerEmployQrcodeDTO.getRemarkOpen())
                .remarkType(genCustomerEmployQrcodeDTO.getRemarkType())
                .remarkName(genCustomerEmployQrcodeDTO.getRemarkName())
                .tagIds(String.join(",", genCustomerEmployQrcodeDTO.getTagIds()))
                .customerExtendInfo(JSON.toJSONString(genCustomerEmployQrcodeDTO.getExtendPropertyList()))
                .state(state.getState())
                .qrCode(contactDTO.getQr_code())
                .configId(contactDTO.getConfig_id())
                .expireTime(DateUtils.addHours(new Date(), ruoYiConfig.getCustomerEmpleCodeExpireTime()))
                .build();
    }

    /**
     * 生成客户专属活码二维码的状态标识。
     * <p>
     * 该方法根据员工活码ID生成一个状态标识字符串，用于标识特定的客户专属活码二维码。
     *
     * @param id 员工活码ID
     * @return 状态标识字符串
     */
    private String genCustomerEmployQrCodeState(Long id) {
        return EmployCodeConstants.CUSTOMER_EMPLOY_STATE_PREFIX + id;
    }

    private String genCustomerEmployCodedLongUrl(Long id, String corpId) {
        if (id == null || StringUtils.isAnyBlank(corpId)) {
            log.info("[客户专属活码生成长链接] 参数缺失 employCodeId: {}, corpId: {}", id, corpId);
            return StringUtils.EMPTY;
        }
        // 获取中间页域名
        String middlePageDomain = wechatOpenService.getDomain(corpId);
        if (StringUtils.isBlank(middlePageDomain)) {
            log.info("[客户专属活码生成长链接] 未设置中间页域名 employCodeId:{}", id);
            throw new CustomException(ResultTip.TIP_GET_MIDDLE_PAGE_DOMAIN_ERROR);
        }
        return EmployCodeConstants.genCustomerEmployLongUrl(middlePageDomain, id, corpId);
    }

    /**
     * 查询是否为员工活码创建
     *
     * @param source 数据来源
     * @return boolean
     */
    private boolean isEmplyCodeCreate(Integer source) {
        if (source == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return EmployCodeSourceEnum.CODE_CREATE.getSource().equals(source);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode) {
        //校验参数
        verifyParam(weEmpleCode, weEmpleCode.getIsAutoPass(), weEmpleCode.getIsAutoSetRemark());
        weEmpleCode.setCreateTime(new Date());
        weEmpleCode.setCreateBy(LoginTokenService.getUsername());
        // 使用员工活码的id作为state(好友添加的回调会携带该参数)
        weEmpleCode.setState(weEmpleCode.getId().toString());
        addWeEmpleCode(weEmpleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode) {
        //校验请求参数
        verifyParam(weEmpleCode, weEmpleCode.getIsAutoPass(), weEmpleCode.getIsAutoSetRemark());
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            weEmpleCode.buildCodeMsg();
        }
        if (weEmpleCode.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        weEmpleCode.setUpdateTime(new Date());
        weEmpleCode.setUpdateBy(LoginTokenService.getUsername());
        Boolean isNotCreate = true;
        List<WeEmpleCodeUseScop> useScops = weEmpleCode.getWeEmpleCodeUseScops();
        //更新使用人
        if (CollectionUtils.isNotEmpty(useScops) && useScops.get(0).getBusinessIdType() != null) {
            weEmpleCodeUseScopService.remove(new LambdaUpdateWrapper<WeEmpleCodeUseScop>().eq(WeEmpleCodeUseScop::getEmpleCodeId, weEmpleCode.getId()));
            useScops.forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
            weEmpleCodeUseScopService.saveOrUpdateBatch(useScops);
            //调用企微接口
            WeExternalContactDTO.WeContactWay weContactWay = getWeContactWay(weEmpleCode);
            // 从作用范围获取userId列表
            List<String> userIdList = getUserIdByScope(useScops, weEmpleCode.getCorpId());
            // 处理活码统计表数据
            handleEmpleStatisticData(userIdList, weEmpleCode.getCorpId(), weEmpleCode.getId());
            // 调用企微更新联系我接口
            try {
                // 更新活码信息
                weExternalContactClient.updateContactWay(weContactWay, weEmpleCode.getCorpId());
            } catch (Exception e) {
                log.info("[更新活码] 更新活码信息异常，corpId:{}, empleCodeId:{},ex:{}", weEmpleCode.getCorpId(), weEmpleCode.getId(), ExceptionUtils.getStackTrace(e));
            }
            isNotCreate = false;
        }

        //更新标签
        weEmpleCodeTagService.remove(new LambdaUpdateWrapper<WeEmpleCodeTag>().eq(WeEmpleCodeTag::getEmpleCodeId, weEmpleCode.getId()));
        if (CollectionUtils.isNotEmpty(weEmpleCode.getWeEmpleCodeTags())) {
            weEmpleCode.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
            weEmpleCodeTagService.saveOrUpdateBatch(weEmpleCode.getWeEmpleCodeTags());
        }
        if (isEmplyCodeCreate(weEmpleCode.getSource())) {
            weEmpleCodeMaterialService.remove(new LambdaUpdateWrapper<WeEmpleCodeMaterial>().eq(WeEmpleCodeMaterial::getEmpleCodeId, weEmpleCode.getId()));
            buildMaterialSort(weEmpleCode);
        } else {
            weEmpleCodeMaterialService.remove(new LambdaUpdateWrapper<WeEmpleCodeMaterial>().eq(WeEmpleCodeMaterial::getEmpleCodeId, weEmpleCode.getId()));
            if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
                buildMaterialSort(weEmpleCode);
            }
            weEmpleCode.setMaterialSort(new String[]{weEmpleCode.getGroupCodeId().toString()});
            //保存群活码到附件表
            saveGroupCodeMaterial(weEmpleCode.getId(), weEmpleCode.getGroupCodeId());
        }
        // 更新员工活码
        this.baseMapper.updateWeEmpleCode(weEmpleCode);
        //未创建新的活码才更新
        if (isNotCreate) {
            WeExternalContactDTO.WeContactWay weContactWay = getWeContactWay(weEmpleCode);
            weExternalContactClient.updateContactWay(weContactWay, weEmpleCode.getCorpId());
        }
    }

    /**
     * 处理活码统计表数据
     *
     * @param userIdList  员工ID列表
     * @param corpId      企业ID
     * @param empleCodeId 活码ID
     */
    @Override
    public void handleEmpleStatisticData(List<String> userIdList, String corpId, Long empleCodeId) {
        if (StringUtils.isBlank(corpId) || empleCodeId == null || CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        // 今日日期
        String today = DateUtils.dateTime(new Date());
        // 最后要插入的数据
        List<WeEmpleCodeStatistic> insertData = new ArrayList<>();
        // 查询统计表中当天的数据
        List<WeEmpleCodeStatistic> statisticList = weEmpleCodeStatisticMapper.selectList(new LambdaQueryWrapper<WeEmpleCodeStatistic>()
                .eq(WeEmpleCodeStatistic::getCorpId, corpId)
                .eq(WeEmpleCodeStatistic::getEmpleCodeId, empleCodeId)
                .eq(WeEmpleCodeStatistic::getTime, today)
        );
        // 不存在数据
        if (CollectionUtils.isEmpty(statisticList)) {
            // 初始化这个活码的所有员工数据
            List<Long> initList = new ArrayList<>();
            initList.add(empleCodeId);
            insertData = weEmpleCodeAnalyseService.initData(corpId, initList, today);
        } else {
            // 旧的员工ID列表
            List<String> oldUserIdList = statisticList.stream().map(WeEmpleCodeStatistic::getUserId).collect(Collectors.toList());
            // 删除掉旧的
            userIdList.removeAll(oldUserIdList);
            if (CollectionUtils.isEmpty(userIdList)) {
                return;
            }
            // 初始化数据
            for (String userId : userIdList) {
                insertData.add(new WeEmpleCodeStatistic(corpId, empleCodeId, userId, today));
            }
        }
        // 批量插入或更新今天的数据
        weEmpleCodeStatisticMapper.batchInsertOrUpdate(insertData);
    }

    /**
     * 根据使用范围获取userId列表
     *
     * @param useScops 活码使用范围
     * @param corpId   企业ID
     * @return userId列表
     */
    @Override
    public List<String> getUserIdByScope(List<WeEmpleCodeUseScop> useScops, String corpId) {
        if (CollectionUtils.isEmpty(useScops) || StringUtils.isBlank(corpId)) {
            return new ArrayList<>();
        }
        // 获取员工ID列表，businessIdType = 2 表示是userid
        List<String> userIdList = useScops.stream()
                .filter(item -> item.getBusinessIdType().equals(WeConstans.USE_SCOP_BUSINESSID_TYPE_USER))
                .map(WeEmpleCodeUseScop::getBusinessId).collect(Collectors.toList());
        // 获取部门ID列表，businessIdType = 1 表示是departmentId
        List<String> departmentIdList = useScops.stream()
                .filter(item -> item.getBusinessIdType().equals(WeConstans.USE_SCOP_BUSINESSID_TYPE_ORG))
                .map(WeEmpleCodeUseScop::getBusinessId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return userIdList;
        }
        // 获取部门下的userId列表
        userIdList.addAll(weUserService.listOfUserId(corpId, departmentIdList.toArray(new String[0])));
        if (CollectionUtils.isNotEmpty(departmentIdList)) {
            // 获取部门下的userId列表
            userIdList.addAll(weUserService.listOfUserId(corpId, departmentIdList.toArray(new String[0])));
        }
        return userIdList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRemoveWeEmpleCodeIds(String corpId, List<Long> ids) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<String> configIdList = this.baseMapper.batchGetWeEmpleCodeConfigId(corpId, ids);
        if (CollectionUtils.isEmpty(configIdList)) {
            throw new CustomException(ResultTip.TIP_DELETE_QRCODE_NOT_FIND);
        }
        WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
        for (String configId : configIdList) {
            weContactWay.setConfig_id(configId);
            weExternalContactClient.delContactWay(weContactWay, corpId);
        }
        //删除附件表
        weEmpleCodeMaterialService.removeByEmpleCodeId(ids);
        return this.baseMapper.batchRemoveWeEmpleCodeIds(corpId, ids);
    }

    @Override
    public WeEmpleCodeDTO selectWelcomeMsgByScenario(String scenario, String userId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.baseMapper.selectWelcomeMsgByScenario(scenario, userId, corpId);
    }

    @Override
    public EmplyCodeWelcomeMsgInfo selectWelcomeMsgByState(String state, String corpId) {
        if (StringUtils.isBlank(state) || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.baseMapper.selectWelcomeMsgByState(state, corpId);
    }

    @Override
    public EmplyCodeWelcomeMsgInfo selectWelcomeMsgById(String id, String corpId) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.baseMapper.selectWelcomeMsgById(id, corpId);
    }

    @Override
    public WeExternalContactDTO getQrcode(String userIds, String departmentIds, String corpId) {
        String[] userIdArr = Arrays.stream(userIds.split(",")).filter(StringUtils::isNotEmpty).toArray(String[]::new);
        Long[] departmentIdArr = Arrays.stream(departmentIds.split(",")).filter(StringUtils::isNotEmpty).map(Long::new).toArray(Long[]::new);
        WeExternalContactDTO qrcode = getQrcode(userIdArr, departmentIdArr, corpId);
        //设置24小时过期
        log.info("qrcode:>>>>>>>>>>>【{}】", JSON.toJSONString(qrcode));
        if (qrcode != null && qrcode.getConfig_id() != null) {
            redisCache.setCacheObject(WeConstans.getWeEmployCodeKey(corpId, qrcode.getConfig_id()), qrcode.getConfig_id(), 24, TimeUnit.HOURS);
        }
        return qrcode;
    }

    @Override
    public WeExternalContactDTO getQrcode(String[] userIdArr, Long[] departmentIdArr, String corpId) {
        WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
        //当存在部门id或者用户id大于一个的情况为多人二维码
        if (departmentIdArr.length > 0 || userIdArr.length > 1) {
            weContactWay.setType(WeConstans.MANY_EMPLE_CODE_TYPE);
        } else {
            weContactWay.setType(WeConstans.SINGLE_EMPLE_CODE_TYPE);
        }
        weContactWay.setScene(WeConstans.QR_CODE_EMPLE_CODE_SCENE);
        weContactWay.setUser(userIdArr);
        weContactWay.setParty(departmentIdArr);
        return getQrCodeFromClient(weContactWay, corpId);
    }

    /**
     * 获取二维码
     *
     * @param weContactWay 请求参数
     * @param corpId       企业ID
     * @return WeExternalContactDTO
     */
    private WeExternalContactDTO getQrCodeFromClient(WeExternalContactDTO.WeContactWay weContactWay, String corpId) {
        return weExternalContactClient.addContactWay(weContactWay, corpId);
    }

    /**
     * 新增员工活码
     *
     * @param weEmpleCode weEmpleCode
     */
    private void addWeEmpleCode(AddWeEmpleCodeDTO weEmpleCode) {
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            weEmpleCode.buildCodeMsg();
        }
        //调用企微接口
        WeExternalContactDTO.WeContactWay weContactWay = getWeContactWay(weEmpleCode);
        WeExternalContactDTO contactDTO = getQrCodeFromClient(weContactWay, weEmpleCode.getCorpId());
        weEmpleCode.setConfigId(contactDTO.getConfig_id());
        weEmpleCode.setQrCode(contactDTO.getQr_code());
        //保存使用人及部门
        weEmpleCode.getWeEmpleCodeUseScops().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
        weEmpleCodeUseScopService.saveBatch(weEmpleCode.getWeEmpleCodeUseScops());
        // 根据使用人及部门获取userId
        List<String> userIdList = getUserIdByScope(weEmpleCode.getWeEmpleCodeUseScops(), weEmpleCode.getCorpId());
        // 插入初始化数据
        handleEmpleStatisticData(userIdList, weEmpleCode.getCorpId(), weEmpleCode.getId());
        //保存标签信息
        if (CollectionUtils.isNotEmpty(weEmpleCode.getWeEmpleCodeTags())) {
            weEmpleCode.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
            weEmpleCodeTagService.saveBatch(weEmpleCode.getWeEmpleCodeTags());
        }
        if (isEmplyCodeCreate(weEmpleCode.getSource())) {
            buildMaterialSort(weEmpleCode);
        } else {
            weEmpleCode.setMaterialSort(new String[]{weEmpleCode.getGroupCodeId().toString()});
            //保存群活码到附件表
            saveGroupCodeMaterial(weEmpleCode.getId(), weEmpleCode.getGroupCodeId());
            buildMaterialSort(weEmpleCode);
        }
        baseMapper.insertWeEmpleCode(weEmpleCode);
    }

    /**
     * 保存素材
     *
     * @param codeMaterialList
     * @param weEmpleCode
     */
    private void setMaterialSort(List<AddWeMaterialDTO> codeMaterialList, WeEmpleCode weEmpleCode) {
        //判断为新增或者从素材库获取,若为新增则保存tempFlag=1
        saveTempMaterial(codeMaterialList, weEmpleCode.getCorpId());
        //保存素材到附件表
        saveEmpleCodeMaterialList(codeMaterialList, weEmpleCode.getId());
    }

    /**
     * 保存附件顺序
     *
     * @param weEmpleCode
     */
    @Override
    public void buildMaterialSort(AddWeEmpleCodeDTO weEmpleCode) {
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            //插入素材附件
            if (CollectionUtils.isNotEmpty(weEmpleCode.getMaterialList())) {
                final List<AddWeMaterialDTO> materialList = weEmpleCode.getMaterialList();
                setMaterialSort(materialList, weEmpleCode);
                //将素材附件顺序保存到weEmpleCode
                weEmpleCode.setMaterialSort(getMaterialSort(materialList));
            } else {
                // 素材列表为空，将之前的素材排序清空
                weEmpleCode.setMaterialSort(new String[]{});
            }
        } else if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            if (CollectionUtils.isNotEmpty(weEmpleCode.getCodeSuccessMaterialList())) {
                final List<AddWeMaterialDTO> materialList = weEmpleCode.getCodeSuccessMaterialList();
                setMaterialSort(materialList, weEmpleCode);
                weEmpleCode.setCodeSuccessMaterialSort(getMaterialSort(materialList));
            }
            if (CollectionUtils.isNotEmpty(weEmpleCode.getCodeFailMaterialList())) {
                final List<AddWeMaterialDTO> materialList = weEmpleCode.getCodeFailMaterialList();
                setMaterialSort(materialList, weEmpleCode);
                weEmpleCode.setCodeFailMaterialSort(getMaterialSort(materialList));
            }
            if (CollectionUtils.isNotEmpty(weEmpleCode.getCodeRepeatMaterialList())) {
                final List<AddWeMaterialDTO> materialList = weEmpleCode.getCodeRepeatMaterialList();
                setMaterialSort(materialList, weEmpleCode);
                weEmpleCode.setCodeRepeatMaterialSort(getMaterialSort(materialList));
            }
        }
    }

    /**
     * 当素材ID为空时,则为临时素材 需保存到素材库拿取素材ID
     *
     * @param materialDTOList materialDTOList
     * @param corpId          企业ID
     */
    private void saveTempMaterial(List<AddWeMaterialDTO> materialDTOList, String corpId) {
        if (CollectionUtils.isNotEmpty(materialDTOList)) {
            for (AddWeMaterialDTO materialDTO : materialDTOList) {
                if (materialDTO.getId() == null) {
                    materialDTO.setTempFlag(WeTempMaterialEnum.TEMP.getTempFlag());
                    weMaterialService.insertWeMaterial(materialDTO);
                } else {
                    // 更新编辑后的素材信息
                    UpdateWeMaterialDTO weMaterialDTO = new UpdateWeMaterialDTO();
                    BeanUtils.copyProperties(materialDTO, weMaterialDTO);
                    weMaterialDTO.setCorpId(corpId);
                    // 如果判断是从素材库取出的素材，且内容有被修改，则插入一条新的数据，不更新原来的素材库中的内容
                    if (WeTempMaterialEnum.MATERIAL.getTempFlag().equals(weMaterialDTO.getTempFlag())) {
                        WeMaterial originalMaterial = weMaterialService.getById(materialDTO.getId());
                        // 比较本地素材库中的素材和编辑/新增的附件中从素材库取出的素材是否相同
                        if (!compareMaterial(originalMaterial, materialDTO)) {
                            // 素材有做修改，设置类型为临时素材
                            materialDTO.setTempFlag(WeTempMaterialEnum.TEMP.getTempFlag());
                            InsertWeMaterialVO insertWeMaterialVO = weMaterialService.insertWeMaterial(materialDTO);
                            // 获取新的素材ID存入素材排序列表
                            materialDTO.setId(insertWeMaterialVO.getId());
                        }
                    } else {
                        // 自定义的附件，直接更新
                        weMaterialService.updateWeMaterial(weMaterialDTO);
                    }
                }
            }
        }
    }

    /**
     * 比较本地素材库中的素材和编辑/新增的附件中从素材库取出的素材是否相同
     *
     * @param originalMaterial {@link WeMaterial}
     * @param materialDTO      {@link AddWeMaterialDTO}
     * @return True-相同，表示没有修改， False-不同，表示素材有做修改
     */
    private Boolean compareMaterial(WeMaterial originalMaterial, AddWeMaterialDTO materialDTO) {
        String materialType = materialDTO.getMediaType().toString();
        // 图片、文件，只能修改标题
        if (MediaType.IMAGE.getType().equals(materialType) || MediaType.FILE.getType().equals(materialType)) {
            return originalMaterial.getMaterialName().equals(materialDTO.getMaterialName());
        }
        // 视频，可以修改显示的封面图片、标题
        if (MediaType.VIDEO.getType().equals(materialType)) {
            return originalMaterial.getMaterialName().equals(materialDTO.getMaterialName()) && originalMaterial.getCoverUrl().equals(materialDTO.getCoverUrl());
        }
        // 链接，可以修改标题、摘要、地址、封面
        if (GroupMessageType.LINK.getType().equals(materialType)) {
            return originalMaterial.getCoverUrl().equals(materialDTO.getCoverUrl()) && originalMaterial.getMaterialName().equals(materialDTO.getMaterialName()) && originalMaterial.getDigest().equals(materialDTO.getDigest()) && originalMaterial.getMaterialUrl().equals(materialDTO.getMaterialUrl());
        }
        // 小程序，可以修改原始ID，APPID，地址，标题，封面
        if (GroupMessageType.MINIPROGRAM.getType().equals(materialType)) {
            return originalMaterial.getAccountOriginalId().equals(materialDTO.getAccountOriginalId()) && originalMaterial.getAppid().equals(materialDTO.getAppid()) && originalMaterial.getMaterialUrl().equals(materialDTO.getMaterialUrl()) && originalMaterial.getMaterialName().equals(materialDTO.getMaterialName()) && originalMaterial.getCoverUrl().equals(materialDTO.getCoverUrl());
        }
        // 表单、雷达，不能在附件中修改
        return true;
    }

    /**
     * 保存素材到附件表
     *
     * @param materialDTOList 素材列表
     * @param weEmpleCodeId   员工活码ID
     */
    private void saveEmpleCodeMaterialList(List<AddWeMaterialDTO> materialDTOList, Long weEmpleCodeId) {
        List<WeEmpleCodeMaterial> addList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(materialDTOList)) {
            for (AddWeMaterialDTO weEmpleCodeMaterialDTO : materialDTOList) {
                if (weEmpleCodeMaterialDTO.getId() == null || weEmpleCodeMaterialDTO.getMediaType() == null) {
                    throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
                }
                addList.add(new WeEmpleCodeMaterial(weEmpleCodeId, weEmpleCodeMaterialDTO.getId(), weEmpleCodeMaterialDTO.getMediaType()));
            }
            weEmpleCodeMaterialService.batchInsert(addList);
        }
    }

    /**
     * 保存群活码ID到附件表
     *
     * @param weEmpleCodeId 员工活码ID
     * @param groupCodeId   群活码ID
     */
    private void saveGroupCodeMaterial(Long weEmpleCodeId, Long groupCodeId) {
        WeEmpleCodeMaterial weEmpleCodeMaterial = new WeEmpleCodeMaterial(weEmpleCodeId, groupCodeId, WeConstans.DEFAULT_GROUP_CODE_MEDIA_TYPE);
        weEmpleCodeMaterialService.save(weEmpleCodeMaterial);
    }


    /**
     * 组装调用企微接口参数（客户联系「联系我」）
     *
     * @param weEmpleCode 员工活码实体类
     * @return 企微接口参数实体类
     */
    private WeExternalContactDTO.WeContactWay getWeContactWay(WeEmpleCode weEmpleCode) {
        WeExternalContactDTO.WeContactWay weContactWay = new WeExternalContactDTO.WeContactWay();
        List<WeEmpleCodeUseScop> weEmpleCodeUseScops = weEmpleCode.getWeEmpleCodeUseScops();
        //根据类型生成相应的活码
        weContactWay.setConfig_id(weEmpleCode.getConfigId());
        weContactWay.setType(weEmpleCode.getCodeType());
        weContactWay.setScene(WeConstans.QR_CODE_EMPLE_CODE_SCENE);
        weContactWay.setSkip_verify(WeEmployCodeSkipVerifyEnum.isPassByNow(weEmpleCode.getSkipVerify(), weEmpleCode.getEffectTimeOpen(), weEmpleCode.getEffectTimeClose()));
        weContactWay.setState(weEmpleCode.getState());

        List<String> userIdList = new LinkedList<>();
        List<Long> partyIdList = new LinkedList<>();

        if (CollUtil.isNotEmpty(weEmpleCodeUseScops)) {
            weEmpleCodeUseScops.forEach(item -> {
                //员工列表
                if (WeConstans.USE_SCOP_BUSINESSID_TYPE_USER.equals(item.getBusinessIdType())
                        && StringUtils.isNotEmpty(item.getBusinessId())) {
                    userIdList.add(item.getBusinessId());
                }
                //部门列表
                if (!WeConstans.SINGLE_EMPLE_CODE_TYPE.equals(weEmpleCode.getCodeType())
                        && WeConstans.USE_SCOP_BUSINESSID_TYPE_ORG.equals(item.getBusinessIdType())) {
                    //partyIdList.add(Long.valueOf(item.getBusinessId()));
                    //查找部门下员工
                    List<String> userIdsByDepartment = weUserService.listOfUserId(weEmpleCode.getCorpId(), new String[]{item.getBusinessId()});
                    if (CollectionUtils.isNotEmpty(userIdsByDepartment)) {
                        userIdList.addAll(userIdsByDepartment);
                    }
                }
            });
            String[] userIdArr = userIdList.toArray(new String[]{});
            weContactWay.setUser(userIdArr);
            Long[] partyArr = partyIdList.toArray(new Long[]{});
            weContactWay.setParty(partyArr);
        }
        return weContactWay;
    }

    @Override
    public List<WeEmpleCode> getWeEmpleCodeByEffectTime(String HHmm) {
        if (StringUtils.isBlank(HHmm)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.getWeEmpleCodeByTime(HHmm);
    }

    @Override
    public List<WeEmplyCodeDownloadVO> downloadWeEmplyCodeData(String corpId, List<Long> idList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(idList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.downloadWeEmplyCodeData(corpId, idList);
    }

    @Override
    public List<WeEmplyCodeScopeUserVO> getUserByEmplyCode(String corpId, Long id) {
        if (StringUtils.isBlank(corpId) || id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeEmplyCodeScopeUserVO> users = baseMapper.getUserByEmplyCodeId(corpId, id);
        List<WeEmplyCodeScopeUserVO> usersFromDepartment = baseMapper.getUserFromDepartmentByEmplyCodeId(corpId, id);
        return baseMapper.getUserByEmplyCodeId(corpId, id);
    }

    /**
     * 构建普通欢迎语及附件
     *
     * @param welcomeMsgVO
     * @param corpId
     * @param externalUserId
     */
    @Override
    public void buildCommonWelcomeMsg(EmplyCodeWelcomeMsgInfo welcomeMsgVO, String corpId, String externalUserId) {
        if (welcomeMsgVO.getMaterialSort() != null && welcomeMsgVO.getMaterialSort().length != 0) {
            //员工活码，则需要获取素材详情
            if (isEmplyCodeCreate(welcomeMsgVO.getSource())) {
                welcomeMsgVO.setMaterialList(weMaterialService.getListByMaterialSort(welcomeMsgVO.getMaterialSort(), corpId));
            } else {
                //新客进群，从we_group_code获取群url
                String groupCodeId = welcomeMsgVO.getMaterialSort()[0];
                String codeUrl = weGroupCodeService.getCodeUrlByIdAndCorpId(Long.parseLong(groupCodeId), corpId);
                welcomeMsgVO.setGroupCodeUrl(codeUrl);
            }
        }
    }

    /**
     * 构建获客链接普通欢迎语及附件
     *
     * @param messageMap {@link EmplyCodeWelcomeMsgInfo}
     * @param corpId     企业ID
     */
    @Override
    public void buildCustomerAssistantWelcomeMsg(EmplyCodeWelcomeMsgInfo messageMap, String corpId) {
        if (StringUtils.isBlank(corpId) || messageMap == null) {
            return;
        }
        if (messageMap.getMaterialSort() != null && messageMap.getMaterialSort().length != 0) {
            // 获取素材详情
            if (EmployCodeSourceEnum.CUSTOMER_ASSISTANT.getSource().equals(messageMap.getSource())) {
                messageMap.setMaterialList(weMaterialService.getListByMaterialSort(messageMap.getMaterialSort(), corpId));
            }
        }
    }

    /**
     * 构建活动欢迎语及附件
     *
     * @param welcomeMsgVO
     * @param corpId
     * @param externalUserId
     */
    @Override
    public void buildRedeemCodeActivityWelcomeMsg(EmplyCodeWelcomeMsgInfo welcomeMsgVO, String corpId, String externalUserId) {
        if (welcomeMsgVO.getMaterialSort() != null && welcomeMsgVO.getMaterialSort().length != 0) {
            //新客进去需先获取群二维码
            if (!isEmplyCodeCreate(welcomeMsgVO.getSource())) {
                //新客进群，从we_group_code获取群url
                String groupCodeId = welcomeMsgVO.getMaterialSort()[0];
                String codeUrl = weGroupCodeService.getCodeUrlByIdAndCorpId(Long.parseLong(groupCodeId), corpId);
                welcomeMsgVO.setGroupCodeUrl(codeUrl);
            }
        }
        WeRedeemCode weRedeemCodeDTO = WeRedeemCode.builder().activityId(welcomeMsgVO.getCodeActivityId()).effectiveTime(DateUtils.getDate()).build();
        weRedeemCodeDTO.setCorpId(corpId);
        //查找处在有效期且未使用的兑换码
        WeRedeemCode getRedeemCode = weRedeemCodeMapper.selectOneWhenInEffective(weRedeemCodeDTO);
        //库存大于0
        if (!ObjectUtils.isEmpty(getRedeemCode)) {
            //判断该客户是否有参与过活动, 且该活动是否限制再次参与
            WeRedeemCode weRedeemCode = WeRedeemCode.builder().activityId(welcomeMsgVO.getCodeActivityId()).receiveUserId(externalUserId).build();
            final WeRedeemCode selectWeRedeemCode = weRedeemCodeMapper.selectOne(weRedeemCode);
            //如果客户没有参与过活动
            if (ObjectUtils.isEmpty(selectWeRedeemCode)) {
                welcomeMsgVO.setMaterialList(weMaterialService.getRedeemCodeListByMaterialSort(welcomeMsgVO.getCodeSuccessMaterialSort(), corpId));
                welcomeMsgVO.setRedeemCode(getRedeemCode.getCode());
                welcomeMsgVO.setWelcomeMsg(welcomeMsgVO.getCodeSuccessMsg());
            } else {
                //如果有限制重复参与
                final WeRedeemCodeActivityVO redeemCodeActivity = weRedeemCodeActivityService.getRedeemCodeActivity(corpId, Long.valueOf(welcomeMsgVO.getCodeActivityId()));
                if (RedeemCodeConstants.REDEEM_CODE_ACTIVITY_LIMITED.equals(redeemCodeActivity.getEnableLimited())) {
                    welcomeMsgVO.setMaterialList(weMaterialService.getRedeemCodeListByMaterialSort(welcomeMsgVO.getCodeRepeatMaterialSort(), corpId));
                    welcomeMsgVO.setWelcomeMsg(welcomeMsgVO.getCodeRepeatMsg());
                } else {
                    welcomeMsgVO.setMaterialList(weMaterialService.getRedeemCodeListByMaterialSort(welcomeMsgVO.getCodeSuccessMaterialSort(), corpId));
                    welcomeMsgVO.setWelcomeMsg(welcomeMsgVO.getCodeSuccessMsg());
                    welcomeMsgVO.setRedeemCode(getRedeemCode.getCode());
                }
            }
        } else {
            //添加失败附件
            welcomeMsgVO.setMaterialList(weMaterialService.getRedeemCodeListByMaterialSort(welcomeMsgVO.getCodeFailMaterialSort(), corpId));
            welcomeMsgVO.setWelcomeMsg(welcomeMsgVO.getCodeFailMsg());
        }
    }

    @Override
    public String getCodeAppLink(Long id) {
        if(id == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING) ;
        }
        // 获取活码信息
        WeEmpleCode weEmpleCode = getById(id);
        if(weEmpleCode == null) {
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }
        // 如果已经生成过则直接返回
        if(StringUtils.isNotBlank(weEmpleCode.getAppLink())) {
            return weEmpleCode.getAppLink();
        }
        // 没有则 生成一个活码小程序短链
        EmpleCodeShortUrlAppendInfo appendInfo = EmpleCodeShortUrlAppendInfo.builder()
                                                                            .id(id)
                                                                            .corpId(weEmpleCode.getCorpId())
                                                                            .build();
        String shortUrl = empleCodeShortUrlHandler.createShortUrl(weEmpleCode.getCorpId(), weEmpleCode.getQrCode(), LoginTokenService.getUsername(), appendInfo);
        if(StringUtils.isBlank(shortUrl)) {
            throw new CustomException(ResultTip.TIP_ERROR_CREATING_APP_lINK);
        }
        //保存到活码
        weEmpleCode.setAppLink(shortUrl);
        updateById(weEmpleCode);
        return shortUrl;
    }

    /**
     * 活码统计-根据活动场景模糊查询活码信息
     *
     * @param dto {@link FindWeEmpleCodeDTO}
     * @return {@link EmpleCodeByNameVO}
     */
    @Override
    public List<EmpleCodeByNameVO> listByName(FindWeEmpleCodeDTO dto) {
        if (StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return this.baseMapper.listByName(dto);

    }

    /**
     * 获取有效的活码ID
     *
     * @param corpId 企业ID
     * @param date   日期，格式为YYYY-MM-DD
     * @return 活码ID列表
     */
    @Override
    public List<Long> getEffectEmpleCodeId(String corpId, String date) {
        if (StringUtils.isBlank(corpId)) {
            return new ArrayList<>();
        }
        // 获取小于格式为YYYY-MM-DD 23:29:59时间的需要统计数据的活码(未被删除的活码)
        List<WeEmpleCode> weEmpleCodes = this.baseMapper.selectList(new LambdaQueryWrapper<WeEmpleCode>()
                .eq(WeEmpleCode::getCorpId, corpId)
                .eq(WeEmpleCode::getDelFlag, false)
                .lt(WeEmpleCode::getCreateTime, DateUtils.parseEndDay(date))
        );
        if (CollectionUtils.isEmpty(weEmpleCodes)) {
            return new ArrayList<>();
        }
        // 获取活码ID列表
        return weEmpleCodes.stream().map(WeEmpleCode::getId).collect(Collectors.toList());
    }

    @Override
    public boolean updateTagGroupValid(Long empleCodeId, String corpId, Integer tagGroupValid) {
        if (empleCodeId == null || StringUtils.isBlank(corpId) || tagGroupValid == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 检查活码是否存在
        WeEmpleCode existEmpleCode = this.getOne(new LambdaQueryWrapper<WeEmpleCode>()
                .eq(WeEmpleCode::getId, empleCodeId)
                .eq(WeEmpleCode::getCorpId, corpId)
                .eq(WeEmpleCode::getDelFlag, 0));

        if (existEmpleCode == null) {
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }

        // 更新tagGroupValid字段
        boolean result = this.update(new LambdaUpdateWrapper<WeEmpleCode>()
                .eq(WeEmpleCode::getId, empleCodeId)
                .eq(WeEmpleCode::getCorpId, corpId)
                .set(WeEmpleCode::getTagGroupValid, tagGroupValid)
                .set(WeEmpleCode::getUpdateTime, new Date())
                .set(WeEmpleCode::getUpdateBy, LoginTokenService.getUsername()));

        return result;
    }

    /**
     * 设置活码使用者名称和电话
     *
     * @param employCode          返回活码实体
     * @param useScopeList        活码使用人
     * @param departmentScopeList 活码使用部门
     */
    private void setUserData(WeEmpleCodeVO employCode, List<WeEmpleCodeUseScop> useScopeList, List<WeEmpleCodeUseScop> departmentScopeList) {
        List<WeEmpleCodeUseScop> setUseScopeList = new LinkedList<>();
        if (CollUtil.isNotEmpty(useScopeList)) {
            StringBuilder userUserName = new StringBuilder();
            StringBuilder mobile = new StringBuilder();
            useScopeList.forEach(useScope -> {
                if (useScope.getEmpleCodeId().equals(employCode.getId())
                        && WeConstans.USE_SCOP_BUSINESSID_TYPE_USER.equals(useScope.getBusinessIdType())
                        && StringUtils.isNotEmpty(useScope.getBusinessName())) {
                    userUserName.append(useScope.getBusinessName()).append(WeConstans.COMMA);
                    mobile.append(useScope.getMobile()).append(WeConstans.COMMA);
                    setUseScopeList.add(useScope);
                }
            });
            if (StringUtils.isNotEmpty(userUserName)) {
                //删除最后一个","
                userUserName.deleteCharAt(userUserName.length() - 1);
            }
            if (StringUtils.isNotEmpty(mobile)) {
                //删除最后一个","
                mobile.deleteCharAt(mobile.length() - 1);
            }
            employCode.setUseUserName(userUserName.toString());
            employCode.setMobile(mobile.toString());
        }
        if (CollUtil.isNotEmpty(departmentScopeList)) {
            StringBuilder departmentName = new StringBuilder();
            departmentScopeList.forEach(departScope -> {
                if (departScope.getEmpleCodeId().equals(employCode.getId())
                        && WeConstans.USE_SCOP_BUSINESSID_TYPE_ORG.equals(departScope.getBusinessIdType())
                        && StringUtils.isNotEmpty(departScope.getBusinessName())) {
                    departmentName.append(departScope.getBusinessName()).append(WeConstans.COMMA);
                    setUseScopeList.add(departScope);
                }
            });
            if (StringUtils.isNotEmpty(departmentName)) {
                //删除最后一个","
                departmentName.deleteCharAt(departmentName.length() - 1);
            }
            employCode.setDepartmentName(departmentName.toString());
        }
        employCode.setWeEmpleCodeUseScops(setUseScopeList);
    }

    /**
     * 新增和修改时 校验请求参数
     *
     * @param weEmpleCode     weEmpleCode
     * @param isAutoPass      是否自动通过
     * @param isAutoSetRemark 是否自动备注
     */
    private void verifyParam(AddWeEmpleCodeDTO weEmpleCode, Boolean isAutoPass, Boolean isAutoSetRemark) {
        if (weEmpleCode == null
                || StringUtils.isBlank(weEmpleCode.getCorpId())
                || weEmpleCode.getCodeType() == null
                || weEmpleCode.getSkipVerify() == null
                || StringUtils.isBlank(weEmpleCode.getScenario())
                || weEmpleCode.getRemarkType() == null
                || weEmpleCode.getWeEmpleCodeUseScops() == null
                || weEmpleCode.getWeEmpleCodeUseScops().size() == 0
                || CollectionUtils.isEmpty(weEmpleCode.getWeEmpleCodeUseScops())
                || weEmpleCode.getSource() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //判断兑换码活动中欢迎语是否为空
        if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            if (ObjectUtils.isEmpty(weEmpleCode.getCodeActivity())) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_IS_EMPTY);
            } else {
                if (Long.valueOf(0).equals(weEmpleCode.getCodeActivity().getId())) {
                    throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_IS_EMPTY);
                }
            }
            if (StringUtils.isAllBlank(weEmpleCode.getCodeSuccessMsg(), weEmpleCode.getCodeFailMsg(), weEmpleCode.getCodeRepeatMsg())) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_WELCOME_MSG_IS_EMPTY);
            }
        }

        //当为时间段通过时，需填写开始结束时间
        if (WeEmployCodeSkipVerifyEnum.TIME_PASS.getSkipVerify().equals(weEmpleCode.getSkipVerify())) {
            if (StringUtils.isBlank(weEmpleCode.getEffectTimeOpen()) || StringUtils.isBlank(weEmpleCode.getEffectTimeClose())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            //开始时间和结束时间不能一致
            if (weEmpleCode.getEffectTimeOpen().equals(weEmpleCode.getEffectTimeClose())) {
                throw new CustomException(ResultTip.TIP_TIME_RANGE_FORMAT_ERROR);
            }
        }
        if (isAutoPass != null && !isAutoPass) {
            weEmpleCode.setSkipVerify(WeEmployCodeSkipVerifyEnum.NO_PASS.getSkipVerify());
        }
        if (isAutoSetRemark != null && !isAutoSetRemark) {
            weEmpleCode.setRemarkType(WeEmployCodeRemarkTypeEnum.NO.getRemarkType());
        }

        //当需要设置客户备注时,remarkName不能为空
        if (!WeEmployCodeRemarkTypeEnum.NO.getRemarkType().equals(weEmpleCode.getRemarkType()) && StringUtils.isBlank(weEmpleCode.getRemarkName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //为员工活码时，素材不能超过9张
        if (isEmplyCodeCreate(weEmpleCode.getSource())) {
            //数量超出上限抛异常
            if (CollectionUtils.isNotEmpty(weEmpleCode.getMaterialList()) && weEmpleCode.getMaterialList().size() > WeConstans.MAX_ATTACHMENT_NUM) {
                throw new CustomException(ResultTip.TIP_ATTACHMENT_OVER);
            }
        } else {
            //当为新客建群时，则groupCodeId必传
            if (weEmpleCode.getGroupCodeId() == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        }
    }

}
