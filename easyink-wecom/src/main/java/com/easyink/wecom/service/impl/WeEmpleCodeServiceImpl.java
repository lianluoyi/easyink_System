package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.redeemcode.RedeemCodeConstants;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.*;
import com.easyink.common.enums.code.WelcomeMsgTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeExternalContactClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import com.easyink.wecom.domain.dto.WeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.WeExternalContactDTO;
import com.easyink.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easyink.wecom.domain.vo.SelectWeEmplyCodeWelcomeMsgVO;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.domain.vo.WeEmplyCodeDownloadVO;
import com.easyink.wecom.domain.vo.WeEmplyCodeScopeUserVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.mapper.redeemcode.WeRedeemCodeMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.radar.MiniAppQrCodeUrlHandler;
import com.easyink.wecom.service.radar.WeRadarService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
    private final WeRedeemCodeMapper weRedeemCodeMapper;
    private final WeRedeemCodeActivityService weRedeemCodeActivityService;
    private final WeUserService weUserService;
    private final MiniAppQrCodeUrlHandler miniAppQrCodeUrlHandler;
    private final WeRadarService weRadarService;

    @Autowired
    public WeEmpleCodeServiceImpl(WeEmpleCodeTagService weEmpleCodeTagService, WeEmpleCodeUseScopService weEmpleCodeUseScopService, WeExternalContactClient weExternalContactClient, RedisCache redisCache, WeEmpleCodeMaterialService weEmpleCodeMaterialService, WeMaterialService weMaterialService, WeGroupCodeService weGroupCodeService, WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeGroupCodeActualService weGroupCodeActualService, WeRedeemCodeMapper weRedeemCodeMapper, WeRedeemCodeActivityService weRedeemCodeActivityService, WeUserService weUserService, MiniAppQrCodeUrlHandler miniAppQrCodeUrlHandler, WeRadarService weRadarService) {
        this.weEmpleCodeTagService = weEmpleCodeTagService;
        this.weEmpleCodeUseScopService = weEmpleCodeUseScopService;
        this.weExternalContactClient = weExternalContactClient;
        this.redisCache = redisCache;
        this.weEmpleCodeMaterialService = weEmpleCodeMaterialService;
        this.weMaterialService = weMaterialService;
        this.weGroupCodeService = weGroupCodeService;
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weGroupCodeActualService = weGroupCodeActualService;
        this.weRedeemCodeMapper = weRedeemCodeMapper;
        this.weRedeemCodeActivityService = weRedeemCodeActivityService;
        this.weUserService = weUserService;
        this.miniAppQrCodeUrlHandler = miniAppQrCodeUrlHandler;
        this.weRadarService = weRadarService;
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
        //查询使用部门(查询使用人时需要用businessId关联we_user表，活码使用部门时不传入businessId)
        List<WeEmpleCodeUseScop> departmentScopeList = weEmpleCodeUseScopService.selectDepartmentWeEmpleCodeUseScopListByIds(employCodeIdList);

        weEmployCodeList.forEach(employCode -> {
            //设置活码使用人/部门对象
            setUserData(employCode, useScopeList, departmentScopeList);
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
            if (WelcomeMsgTypeEnum.REDEEM_CODE_WELCOME_MSG_TYPE.getType().equals(employCode.getWelcomeMsgType())) {
                buildEmployCodeMaterial(employCode, employCode.getCorpId());
            }
        } else {
            //查询素材
            buildEmployCodeMaterial(employCode, employCode.getCorpId());
        }
    }

    private void buildRadarDate(List<AddWeMaterialDTO> materialList, String corpId) {
        materialList.forEach(item -> {
            if (AttachmentTypeEnum.RADAR.getMessageType().equals(item.getMediaType())) {
                item.setRadar(SpringUtils.getBean(WeRadarService.class).getRadar(corpId, item.getRadarId()));
            }
        });
    }

    /**
     * 根据附件排序查找添加素材
     *
     * @param employCode
     * @param corpId
     */
    private void buildEmployCodeMaterial(WeEmpleCodeVO employCode, String corpId) {
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(employCode.getWelcomeMsgType())) {
            if (!ArrayUtils.isEmpty(employCode.getMaterialSort())) {
                List<AddWeMaterialDTO> materialList = weMaterialService.getListByMaterialSort(employCode.getMaterialSort(), corpId);
                buildRadarDate(materialList, corpId);
                employCode.setMaterialList(materialList);
            } else {
                employCode.setMaterialList(Collections.emptyList());
            }
        } else {
            final WeRedeemCodeActivityVO redeemCodeActivity = weRedeemCodeActivityService.getRedeemCodeActivity(corpId, Long.valueOf(employCode.getCodeActivityId()));
            employCode.setCodeActivity(Optional.ofNullable(redeemCodeActivity).orElseGet(WeRedeemCodeActivityVO::new));

            List<AddWeMaterialDTO> successMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeSuccessMaterialSort(), corpId);
            buildRadarDate(successMaterialList, corpId);
            employCode.setCodeSuccessMaterialList(successMaterialList);

            List<AddWeMaterialDTO> failMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeFailMaterialSort(), corpId);
            buildRadarDate(failMaterialList, corpId);
            employCode.setCodeFailMaterialList(failMaterialList);

            List<AddWeMaterialDTO> repeatMaterialList = weMaterialService.getRedeemCodeListByMaterialSort(employCode.getCodeRepeatMaterialSort(), corpId);
            buildRadarDate(repeatMaterialList, corpId);
            employCode.setCodeRepeatMaterialList(repeatMaterialList);
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
            // 生成小程序短链
            WeExternalContactDTO contactDTO = getQrCodeFromClient(weContactWay, weEmpleCode.getCorpId());
            weEmpleCode.setConfigId(contactDTO.getConfig_id());
            weEmpleCode.setQrCode(contactDTO.getQr_code());
            String shortUrl = miniAppQrCodeUrlHandler.createShortCode(contactDTO.getQr_code());
            if (StringUtils.isNotBlank(shortUrl)) {
                weEmpleCode.setAppLink(shortUrl);
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

        List<Long> activityIdList = new ArrayList<>();
        activityIdList.add(weEmpleCode.getId());
        //删除附件表
        weEmpleCodeMaterialService.removeByEmpleCodeId(activityIdList);
        //删除员工活码，物理删除
        this.baseMapper.deleteWeEmpleCode(weEmpleCode.getCorpId(), weEmpleCode.getId());
        this.baseMapper.insertWeEmpleCode(weEmpleCode);
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


    /**
     * 组装活动欢迎语
     *
     * @param corpId
     * @param externalUserId
     * @param welcomeMsgVO
     * @return
     */
    private void buildMaterial(String corpId, String externalUserId, SelectWeEmplyCodeWelcomeMsgVO welcomeMsgVO) {

    }

    @Override
    public SelectWeEmplyCodeWelcomeMsgVO selectWelcomeMsgByState(String state, String corpId, String externalUserId) {
        if (StringUtils.isBlank(state) || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        SelectWeEmplyCodeWelcomeMsgVO welcomeMsgVO = this.baseMapper.selectWelcomeMsgByState(state, corpId);
        if (welcomeMsgVO == null) {
            return null;
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
        // 生成小程序短链活码
        String shortUrl = miniAppQrCodeUrlHandler.createShortCode(contactDTO.getQr_code());
        if (StringUtils.isNotBlank(shortUrl)) {
            weEmpleCode.setAppLink(shortUrl);
        }
        // 生成小程序活码
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
        saveTempMaterial(codeMaterialList);
        //保存素材到附件表
        saveEmpleCodeMaterialList(codeMaterialList, weEmpleCode.getId());
    }

    /**
     * 保存附件顺序
     *
     * @param weEmpleCode
     */
    private void buildMaterialSort(AddWeEmpleCodeDTO weEmpleCode) {
        if (WelcomeMsgTypeEnum.COMMON_WELCOME_MSG_TYPE.getType().equals(weEmpleCode.getWelcomeMsgType())) {
            //插入素材附件
            if (CollectionUtils.isNotEmpty(weEmpleCode.getMaterialList())) {
                final List<AddWeMaterialDTO> materialList = weEmpleCode.getMaterialList();
                setMaterialSort(materialList, weEmpleCode);
                //将素材附件顺序保存到weEmpleCode
                weEmpleCode.setMaterialSort(getMaterialSort(materialList));
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
     */
    private void saveTempMaterial(List<AddWeMaterialDTO> materialDTOList) {
        if (CollectionUtils.isNotEmpty(materialDTOList)) {
            for (AddWeMaterialDTO materialDTO : materialDTOList) {
                if (materialDTO.getId() == null) {
                    materialDTO.setTempFlag(WeTempMaterialEnum.TEMP.getTempFlag());
                    weMaterialService.insertWeMaterial(materialDTO);
                }
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
    public void buildCommonWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO welcomeMsgVO, String corpId, String externalUserId) {
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
     * 构建活动欢迎语及附件
     *
     * @param welcomeMsgVO
     * @param corpId
     * @param externalUserId
     */
    @Override
    public void buildRedeemCodeActivityWelcomeMsg(SelectWeEmplyCodeWelcomeMsgVO welcomeMsgVO, String corpId, String externalUserId) {
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
