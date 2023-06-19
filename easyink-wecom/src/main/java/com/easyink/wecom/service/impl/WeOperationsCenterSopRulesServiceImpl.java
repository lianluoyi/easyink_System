package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.GroupMessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeOperationsCenterSop;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeOperationsCenterSopMaterialEntity;
import com.easyink.wecom.domain.WeOperationsCenterSopRulesEntity;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.dto.groupsop.AddWeOperationsCenterSopRuleDTO;
import com.easyink.wecom.domain.vo.sop.SopRuleVO;
import com.easyink.wecom.mapper.WeOperationsCenterSopRulesMapper;
import com.easyink.wecom.service.WeOperationsCenterSopMaterialService;
import com.easyink.wecom.service.WeOperationsCenterSopRulesService;
import com.easyink.wecom.service.WeWordsDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Validated
public class WeOperationsCenterSopRulesServiceImpl extends ServiceImpl<WeOperationsCenterSopRulesMapper, WeOperationsCenterSopRulesEntity> implements WeOperationsCenterSopRulesService {

    private final WeWordsDetailService weWordsDetailService;
    private final WeOperationsCenterSopMaterialService sopMaterialService;

    @Autowired
    public WeOperationsCenterSopRulesServiceImpl(WeWordsDetailService weWordsDetailService, WeOperationsCenterSopMaterialService sopMaterialService) {
        this.weWordsDetailService = weWordsDetailService;
        this.sopMaterialService = sopMaterialService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveRuleAndMaterialList(Long sopId, String corpId, List<AddWeOperationsCenterSopRuleDTO> ruleList) {
        if (sopId == null || StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(ruleList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeOperationsCenterSopRulesEntity sopRulesEntity;
        for (AddWeOperationsCenterSopRuleDTO sopRuleDTO : ruleList) {
            checkParam(sopRuleDTO);
            //保存规则
            sopRulesEntity = new WeOperationsCenterSopRulesEntity();
            BeanUtils.copyProperties(sopRuleDTO, sopRulesEntity);
            sopRulesEntity.setCorpId(corpId);
            sopRulesEntity.setSopId(sopId);
            this.save(sopRulesEntity);
            //保存话术素材
            batchSaveMaterial(corpId, sopId, sopRulesEntity.getId(), sopRuleDTO.getMaterialList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSopByCorpIdAndSopIdList(String corpId, List<Long> sopIdList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sopIdList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //删除规则下的话术素材
        sopMaterialService.delSopByCorpIdAndSopIdList(corpId, sopIdList);

        LambdaQueryWrapper<WeOperationsCenterSopRulesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopRulesEntity::getCorpId, corpId)
                .in(WeOperationsCenterSopRulesEntity::getSopId, sopIdList);
        baseMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSopRules(String corpId, Long sopId, List<AddWeOperationsCenterSopRuleDTO> ruleList, List<Long> delList) {
        if (StringUtils.isBlank(corpId) || sopId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //删除不要的规则
        if (CollectionUtils.isNotEmpty(delList)) {
            delSopByCorpIdAndIdList(corpId, delList);
        }
        List<Long> delMaterialList = new ArrayList<>();
        WeOperationsCenterSopRulesEntity sopRulesEntity;
        List<WeWordsDetailEntity> updateMaterialList = new ArrayList<>();
        List<WeOperationsCenterSopRulesEntity> updateList = new ArrayList<>();
        for (AddWeOperationsCenterSopRuleDTO sopRuleDTO : ruleList) {
            checkParam(sopRuleDTO);

            //存在规则下需要删除的素材，放入delMaterialList统一处理
            if (CollectionUtils.isNotEmpty(sopRuleDTO.getDelMaterialList())) {
                delMaterialList.addAll(sopRuleDTO.getDelMaterialList());
            }

            sopRulesEntity = new WeOperationsCenterSopRulesEntity();
            BeanUtils.copyProperties(sopRuleDTO, sopRulesEntity);
            sopRulesEntity.setCorpId(corpId);
            sopRulesEntity.setSopId(sopId);

            //新增加的规则
            if (sopRuleDTO.getId() == null) {
                baseMapper.insert(sopRulesEntity);
                //保存规则下的素材
                batchSaveMaterial(corpId, sopId, sopRulesEntity.getId(), sopRuleDTO.getMaterialList());
            } else {
                updateList.add(sopRulesEntity);
                if (CollectionUtils.isNotEmpty(sopRuleDTO.getMaterialList())) {
                    sopRuleDTO.getMaterialList().forEach(sopMaterial -> sopMaterial.setRuleId(sopRuleDTO.getId()));
                    for (WeWordsDetailEntity weWordsDetailEntity : sopRuleDTO.getMaterialList()) {
                        // mediaType = 6 表示为小程序附件，将appid存入content中，用于侧边栏发送时取用
                        saveAppidToContent(weWordsDetailEntity);
                    }
                    updateMaterialList.addAll(sopRuleDTO.getMaterialList());
                }
            }
        }

        //删除不要的素材数据
        if (CollectionUtils.isNotEmpty(delMaterialList)) {
            weWordsDetailService.delByCorpIdAndIdList(corpId, delMaterialList);
        }
        //更新修改的规则
        if (CollectionUtils.isNotEmpty(updateList)) {
            baseMapper.batchUpdate(corpId, updateList);
        }
        //保存或更新素材数据
        List<WeOperationsCenterSopMaterialEntity> sopMaterialEntityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updateMaterialList)) {
            updateMaterialList.forEach(material ->{
                material.setCorpId(corpId);
                material.setGroupId(WeConstans.DEFAULT_SOP_WORDS_DETAIL_GROUP_ID);
                if (material.getId() == null) {
                    material.setId(SnowFlakeUtil.nextId());
                    //构造素材关联关系
                    WeOperationsCenterSopMaterialEntity sopMaterialEntity = new WeOperationsCenterSopMaterialEntity();
                    buildSopMaterialEntity(sopMaterialEntity, sopMaterialEntityList, corpId, sopId, material.getRuleId(), material.getId());
                }
            });
            weWordsDetailService.saveOrUpdate(updateMaterialList);
            //保存或更新到sop素材表
            sopMaterialService.saveOrUpdateBatch(sopMaterialEntityList);
        }

    }

    @Override
    public SopRuleVO getSopRule(@NotEmpty String corpId, @NotNull Long sopId, @NotNull Long id) {
        return baseMapper.getSopRule(corpId, sopId, id);
    }

    /**
     * 根据corpId和idList删除规则
     *
     * @param corpId  企业ID
     * @param delList 要删除的规则
     */
    private void delSopByCorpIdAndIdList(String corpId, List<Long> delList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(delList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeOperationsCenterSopRulesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeOperationsCenterSopRulesEntity::getCorpId, corpId)
                .in(WeOperationsCenterSopRulesEntity::getId, delList);
        baseMapper.delete(wrapper);
    }


    /**
     * 保存素材数据
     *
     * @param corpId       企业ID
     * @param sopId        sopId
     * @param ruleId       规则ID
     * @param materialList 素材列表
     */
    private void batchSaveMaterial(String corpId, Long sopId, Long ruleId, List<WeWordsDetailEntity> materialList) {
        if (StringUtils.isBlank(corpId) || sopId == null || ruleId == null || CollectionUtils.isEmpty(materialList)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeWordsDetailEntity> addList = new ArrayList<>();
        List<WeOperationsCenterSopMaterialEntity> sopMaterialEntityList = new ArrayList<>();
        WeOperationsCenterSopMaterialEntity sopMaterialEntity;
        for (WeWordsDetailEntity detailEntity : materialList) {
            if (detailEntity.getId() == null) {
                detailEntity.setId(SnowFlakeUtil.nextId());
                detailEntity.setCorpId(corpId);
                detailEntity.setGroupId(WeConstans.DEFAULT_SOP_WORDS_DETAIL_GROUP_ID);
                // mediaType = 6 表示为小程序附件，将appid存入content中，用于侧边栏发送时取用
                saveAppidToContent(detailEntity);
                addList.add(detailEntity);
            }
            sopMaterialEntity = new WeOperationsCenterSopMaterialEntity();
            buildSopMaterialEntity(sopMaterialEntity, sopMaterialEntityList, corpId, sopId, ruleId, detailEntity.getId());
        }
        //保存临时素材
        if (CollectionUtils.isNotEmpty(addList)) {
            weWordsDetailService.saveOrUpdate(addList);
        }
        //保存到sop素材表
        sopMaterialService.saveBatch(sopMaterialEntityList);
    }

    /**
     * 将小程序appid存入content，用于侧边栏发送时使用
     *
     * @param weWordsDetailEntity {@link WeWordsDetailEntity}
     */
    private void saveAppidToContent(WeWordsDetailEntity weWordsDetailEntity) {
        // mediaType = 6 表示为小程序附件，将appid存入content中，用于侧边栏发送时取用
        if (Objects.equals(weWordsDetailEntity.getMediaType(), Integer.parseInt(GroupMessageType.MINIPROGRAM.getType()))) {
            weWordsDetailEntity.setContent(weWordsDetailEntity.getAppid());
        }
    }

    /**
     * 构造sop素材关联
     */
    private void buildSopMaterialEntity(WeOperationsCenterSopMaterialEntity sopMaterialEntity, List<WeOperationsCenterSopMaterialEntity> sopMaterialEntityList, String corpId, Long sopId, Long ruleId, Long materialId) {
        sopMaterialEntity.setCorpId(corpId);
        sopMaterialEntity.setMaterialId(materialId);
        sopMaterialEntity.setRuleId(ruleId);
        sopMaterialEntity.setSopId(sopId);
        sopMaterialEntityList.add(sopMaterialEntity);
    }

    /**
     * 校验请求参数
     *
     * @param sopRuleDTO sopRuleDTO
     */
    private void checkParam(AddWeOperationsCenterSopRuleDTO sopRuleDTO) {
        if (sopRuleDTO == null || StringUtils.isBlank(sopRuleDTO.getName())
                || sopRuleDTO.getAlertType() == null || CollectionUtils.isEmpty(sopRuleDTO.getMaterialList())
                || StringUtils.isBlank(sopRuleDTO.getAlertData2())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //当alertType=5时(活动SOP)，给alertData1默认值
        if (WeOperationsCenterSop.AlertTypeEnum.TYPE_5.getAlertType().equals(sopRuleDTO.getAlertType())) {
            sopRuleDTO.setAlertData1(WeConstans.DEFAULT_SOP_ALTER_DATA1);
        }
        //根据alertType校验alertData1、alertData2
        WeOperationsCenterSop.AlertTypeEnum.checkParam(sopRuleDTO.getAlertType(), sopRuleDTO.getAlertData1(), sopRuleDTO.getAlertData2());
    }

}