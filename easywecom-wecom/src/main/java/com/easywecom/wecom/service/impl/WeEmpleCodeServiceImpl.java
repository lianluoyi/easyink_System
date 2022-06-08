package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.*;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.wecom.client.WeExternalContactClient;
import com.easywecom.wecom.domain.*;
import com.easywecom.wecom.domain.dto.AddWeMaterialDTO;
import com.easywecom.wecom.domain.dto.WeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.WeExternalContactDTO;
import com.easywecom.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easywecom.wecom.domain.dto.emplecode.UpdateWeEmplyCodeDTO;
import com.easywecom.wecom.domain.vo.SelectWeEmplyCodeWelcomeMsgVO;
import com.easywecom.wecom.domain.vo.WeEmpleCodeVO;
import com.easywecom.wecom.domain.vo.WeEmplyCodeDownloadVO;
import com.easywecom.wecom.domain.vo.WeEmplyCodeScopeUserVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.WeEmpleCodeMapper;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
public class WeEmpleCodeServiceImpl extends ServiceImpl<WeEmpleCodeMapper, WeEmpleCode> implements WeEmpleCodeService {

    private final WeEmpleCodeTagService weEmpleCodeTagService;
    private final WeEmpleCodeUseScopService weEmpleCodeUseScopService;
    private final WeExternalContactClient weExternalContactClient;
    private final RedisCache redisCache;
    private final WeEmpleCodeMaterialService weEmpleCodeMaterialService;
    private final WeMaterialService weMaterialService;
    private final WeGroupCodeService weGroupCodeService;
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeGroupCodeActualService weGroupCodeActualService;

    @Autowired
    public WeEmpleCodeServiceImpl(WeEmpleCodeTagService weEmpleCodeTagService, WeEmpleCodeUseScopService weEmpleCodeUseScopService, WeExternalContactClient weExternalContactClient, RedisCache redisCache, WeEmpleCodeMaterialService weEmpleCodeMaterialService, WeMaterialService weMaterialService, WeGroupCodeService weGroupCodeService, WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeGroupCodeActualService weGroupCodeActualService) {
        this.weEmpleCodeTagService = weEmpleCodeTagService;
        this.weEmpleCodeUseScopService = weEmpleCodeUseScopService;
        this.weExternalContactClient = weExternalContactClient;
        this.redisCache = redisCache;
        this.weEmpleCodeMaterialService = weEmpleCodeMaterialService;
        this.weMaterialService = weMaterialService;
        this.weGroupCodeService = weGroupCodeService;
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weGroupCodeActualService = weGroupCodeActualService;
    }

    @Override
    public WeEmpleCodeVO selectWeEmpleCodeById(Long id, String corpId) {
        if (StringUtils.isBlank(corpId) || id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeEmpleCodeVO weEmpleCodeVO = this.baseMapper.selectWeEmpleCodeById(id, corpId);
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
        //查询已打标签
        List<WeEmpleCodeTag> tagList = weEmpleCodeTagService.selectWeEmpleCodeTagListByIds(employCodeIdList);
        //查询使用人
        List<WeEmpleCodeUseScop> useScopeList = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListByIds(employCodeIdList, weEmployCode.getCorpId());
        weEmployCodeList.forEach(employCode -> {
            //活码使用人对象
            List<WeEmpleCodeUseScop> weEmployCodeUseScopeList = useScopeList.stream().filter(useScope -> useScope.getEmpleCodeId().equals(employCode.getId())).collect(Collectors.toList());
            setUserData(employCode, weEmployCodeUseScopeList);
            employCode.setWeEmpleCodeUseScops(weEmployCodeUseScopeList);
            //员工活码标签对象
            employCode.setWeEmpleCodeTags(tagList.stream().filter(tag -> tag.getEmpleCodeId().equals(employCode.getId())).collect(Collectors.toList()));
            //组装数据（员工活码=>素材数据，新客建群=>添加人数、群活码数据、群实际数据）
            bulidWeEmpleCodeVOData(employCode);
        });
        return weEmployCodeList;
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
        } else {
            //查询素材
            if (employCode.getMaterialSort() == null || employCode.getMaterialSort().length == 0) {
                employCode.setMaterialList(new ArrayList<>());
            } else {
                employCode.setMaterialList(weMaterialService.getListByMaterialSort(employCode.getMaterialSort(), employCode.getCorpId()));
            }
        }
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
    public void updateWeEmpleCode(UpdateWeEmplyCodeDTO weEmpleCode) {
        //校验请求参数
        verifyParam(weEmpleCode, weEmpleCode.getIsAutoPass(), weEmpleCode.getIsAutoSetRemark());
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
            WeExternalContactDTO contactDTO = getQrCodeFromClient(weContactWay, weEmpleCode.getCorpId());
            weEmpleCode.setConfigId(contactDTO.getConfig_id());
            weEmpleCode.setQrCode(contactDTO.getQr_code());
            isNotCreate = false;
        }

        //更新标签
        weEmpleCodeTagService.remove(new LambdaUpdateWrapper<WeEmpleCodeTag>().eq(WeEmpleCodeTag::getEmpleCodeId, weEmpleCode.getId()));
        if (CollectionUtils.isNotEmpty(weEmpleCode.getWeEmpleCodeTags())) {
            weEmpleCode.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
            weEmpleCodeTagService.saveOrUpdateBatch(weEmpleCode.getWeEmpleCodeTags());
        }
        if (isEmplyCodeCreate(weEmpleCode.getSource())) {
            List<AddWeMaterialDTO> materialList = weEmpleCode.getMaterialList();
            weEmpleCode.setMaterialSort(new String[]{});
            if (CollectionUtils.isNotEmpty(materialList)) {
                saveTempMaterial(materialList);
                //将素材附件顺序保存到weEmpleCode
                weEmpleCode.setMaterialSort(getMaterialSort(weEmpleCode.getMaterialList()));
                //更新素材附件表
                weEmpleCodeMaterialService.remove(new LambdaUpdateWrapper<WeEmpleCodeMaterial>().eq(WeEmpleCodeMaterial::getEmpleCodeId, weEmpleCode.getId()));
                saveEmpleCodeMaterialList(materialList, weEmpleCode.getId());
            }
        } else {
            weEmpleCode.setMaterialSort(new String[]{weEmpleCode.getGroupCodeId().toString()});
            //更新附件表
            weEmpleCodeMaterialService.updateGroupCodeMediaIdByEmpleCodeId(weEmpleCode.getId(), weEmpleCode.getGroupCodeId());
        }
        this.baseMapper.updateWeEmpleCode(weEmpleCode);
        //未创建新的活码才更新
        if (isNotCreate) {
            WeExternalContactDTO.WeContactWay weContactWay = getWeContactWay(weEmpleCode);
            weExternalContactClient.updateContactWay(weContactWay, weEmpleCode.getCorpId());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRemoveWeEmpleCodeIds(String corpId, List<Long> ids) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
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
    public SelectWeEmplyCodeWelcomeMsgVO selectWelcomeMsgByState(String state, String corpId) {
        if (StringUtils.isBlank(state) || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        SelectWeEmplyCodeWelcomeMsgVO welcomeMsgVO = this.baseMapper.selectWelcomeMsgByState(state, corpId);
        if (welcomeMsgVO == null) {
            return null;
        }
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
        return welcomeMsgVO;
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

        //调用企微接口
        WeExternalContactDTO.WeContactWay weContactWay = getWeContactWay(weEmpleCode);
        WeExternalContactDTO contactDTO = getQrCodeFromClient(weContactWay, weEmpleCode.getCorpId());
        weEmpleCode.setConfigId(contactDTO.getConfig_id());
        weEmpleCode.setQrCode(contactDTO.getQr_code());

        //保存使用人
        weEmpleCode.getWeEmpleCodeUseScops().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
        weEmpleCodeUseScopService.saveBatch(weEmpleCode.getWeEmpleCodeUseScops());
        //保存标签信息
        if (CollectionUtils.isNotEmpty(weEmpleCode.getWeEmpleCodeTags())) {
            weEmpleCode.getWeEmpleCodeTags().forEach(item -> item.setEmpleCodeId(weEmpleCode.getId()));
            weEmpleCodeTagService.saveBatch(weEmpleCode.getWeEmpleCodeTags());
        }
        if (isEmplyCodeCreate(weEmpleCode.getSource())) {
            //插入素材附件
            if (CollectionUtils.isNotEmpty(weEmpleCode.getMaterialList())) {
                //判断为新增或者从素材库获取,若为新增则保存tempFlag=1
                saveTempMaterial(weEmpleCode.getMaterialList());
                //保存素材到附件表
                saveEmpleCodeMaterialList(weEmpleCode.getMaterialList(), weEmpleCode.getId());
                //将素材附件顺序保存到weEmpleCode
                weEmpleCode.setMaterialSort(getMaterialSort(weEmpleCode.getMaterialList()));
            }
        } else {
            weEmpleCode.setMaterialSort(new String[]{weEmpleCode.getGroupCodeId().toString()});
            //保存群活码到附件表
            saveGroupCodeMaterial(weEmpleCode.getId(), weEmpleCode.getGroupCodeId());
        }
        baseMapper.insertWeEmpleCode(weEmpleCode);

    }

    /**
     * 当素材ID为空时,则为临时素材 需保存到素材库拿取素材ID
     *
     * @param materialDTOList materialDTOList
     */
    private void saveTempMaterial(List<AddWeMaterialDTO> materialDTOList) {
        for (AddWeMaterialDTO materialDTO : materialDTOList) {
            if (materialDTO.getId() == null) {
                materialDTO.setTempFlag(WeTempMaterialEnum.TEMP.getTempFlag());
                weMaterialService.insertWeMaterial(materialDTO);
            }
        }
    }

    /**
     * 保存素材到附件表
     *
     * @param materialDTOList 素材列表
     * @param weEmpleCodeId   员工活码ID
     */
    private void saveEmpleCodeMaterialList(List<AddWeMaterialDTO> materialDTOList, Long weEmpleCodeId) {
        List<WeEmpleCodeMaterial> addList = new ArrayList<>();
        for (AddWeMaterialDTO weEmpleCodeMaterialDTO : materialDTOList) {
            if (weEmpleCodeMaterialDTO.getId() == null || weEmpleCodeMaterialDTO.getMediaType() == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            addList.add(new WeEmpleCodeMaterial(weEmpleCodeId, weEmpleCodeMaterialDTO.getId(), weEmpleCodeMaterialDTO.getMediaType()));
        }
        weEmpleCodeMaterialService.batchInsert(addList);
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
        if (CollUtil.isNotEmpty(weEmpleCodeUseScops)) {
            //员工列表
            String[] userIdArr = weEmpleCodeUseScops.stream().filter(itme ->
                    WeConstans.USE_SCOP_BUSINESSID_TYPE_USER.equals(itme.getBusinessIdType())
                            && StringUtils.isNotEmpty(itme.getBusinessId()))
                    .map(WeEmpleCodeUseScop::getBusinessId).toArray(String[]::new);
            weContactWay.setUser(userIdArr);
            //部门列表
            if (!WeConstans.SINGLE_EMPLE_CODE_TYPE.equals(weEmpleCode.getCodeType())) {
                Long[] partyArr = weEmpleCodeUseScops.stream().filter(itme ->
                        WeConstans.USE_SCOP_BUSINESSID_TYPE_ORG.equals(itme.getBusinessIdType())
                                && StringUtils.isNotEmpty(itme.getBusinessId()))
                        .map(item -> Long.valueOf(item.getBusinessId())).collect(Collectors.toList()).toArray(new Long[]{});
                weContactWay.setParty(partyArr);
            }

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
        return baseMapper.getUserByEmplyCodeId(corpId, id);
    }

    /**
     * 设置活码使用者名称和电话
     *
     * @param empleCode              empleCode
     * @param weEmpleCodeUseScopList weEmpleCodeUseScopList
     */
    private void setUserData(WeEmpleCodeVO empleCode, List<WeEmpleCodeUseScop> weEmpleCodeUseScopList) {
        if (CollUtil.isNotEmpty(weEmpleCodeUseScopList)) {
            String useUserName = weEmpleCodeUseScopList.stream().map(WeEmpleCodeUseScop::getBusinessName).filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
            empleCode.setUseUserName(useUserName);
            String mobile = weEmpleCodeUseScopList.stream().map(WeEmpleCodeUseScop::getMobile).filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
            empleCode.setMobile(mobile);
        }
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

    /**
     * 新增和修改时 校验请求参数
     *
     * @param weEmpleCode     weEmpleCode
     * @param isAutoPass      是否自动通过
     * @param isAutoSetRemark 是否自动备注
     */
    private void verifyParam(WeEmpleCode weEmpleCode, Boolean isAutoPass, Boolean isAutoSetRemark) {
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
