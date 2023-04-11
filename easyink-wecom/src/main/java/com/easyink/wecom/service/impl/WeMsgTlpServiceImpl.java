package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WelcomeMsgTplTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeWelcomeMsgClient;
import com.easyink.wecom.domain.WeMsgTlp;
import com.easyink.wecom.domain.WeMsgTlpMaterial;
import com.easyink.wecom.domain.WeMsgTlpScope;
import com.easyink.wecom.domain.WeMsgTlpSpecialRule;
import com.easyink.wecom.domain.dto.welcomemsg.*;
import com.easyink.wecom.domain.vo.welcomemsg.WeEmployMaterialVO;
import com.easyink.wecom.domain.vo.welcomemsg.WeMsgTlpListVO;
import com.easyink.wecom.domain.vo.welcomemsg.WelcomeMsgGroupMaterialCountVO;
import com.easyink.wecom.mapper.WeMsgTlpMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.radar.WeRadarService;
import com.easyink.wecom.utils.ExtraMaterialUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 欢迎语模板Service业务层处理
 *
 * @author admin
 * @date 2020-10-04
 */
@Slf4j
@Service
public class WeMsgTlpServiceImpl extends ServiceImpl<WeMsgTlpMapper, WeMsgTlp> implements WeMsgTlpService {

    private WeMsgTlpMapper weMsgTlpMapper;
    private WeWelcomeMsgClient weWelcomeMsgClient;
    private WeMsgTlpMaterialService weMsgTlpMaterialService;
    private WeMsgTlpScopeService weMsgTlpScopeService;
    private WeMsgTlpSpecialRuleService weMsgTlpSpecialRuleService;

    @Lazy
    @Autowired
    public WeMsgTlpServiceImpl(WeMsgTlpMapper weMsgTlpMapper, WeWelcomeMsgClient weWelcomeMsgClient, WeMsgTlpMaterialService weMsgTlpMaterialService, WeMsgTlpScopeService weMsgTlpScopeService, WeMsgTlpSpecialRuleService weMsgTlpSpecialRuleService) {
        this.weMsgTlpMapper = weMsgTlpMapper;
        this.weWelcomeMsgClient = weWelcomeMsgClient;
        this.weMsgTlpMaterialService = weMsgTlpMaterialService;
        this.weMsgTlpScopeService = weMsgTlpScopeService;
        this.weMsgTlpSpecialRuleService = weMsgTlpSpecialRuleService;
    }

    /**
     * 查询欢迎语模板列表
     *
     * @param weMsgTlp 欢迎语模板
     * @return 欢迎语模板
     */
    @Override
    public List<WeMsgTlpListVO> selectWeMsgTlpList(WeMsgTlp weMsgTlp) {
        if (StringUtils.isEmpty(weMsgTlp.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeMsgTlpListVO> list = weMsgTlpMapper.selectWeMsgTlpList(weMsgTlp);
        list.forEach(item -> {
            buildExtraMaterial(item.getDefaultMaterialList(), weMsgTlp.getCorpId());
            //特殊规则附件
            item.getWeMsgTlpSpecialRules().forEach(specialRule -> buildExtraMaterial(specialRule.getSpecialMaterialList(), weMsgTlp.getCorpId()));
        });

        return list;
    }

    /**
     * 新增欢迎语模板
     *
     * @param welComeMsgAddDTO
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertWeMsgTlpWithEmploy(WelComeMsgAddDTO welComeMsgAddDTO) {
        // 1.内容与参数校验
        checkEmployInsert(welComeMsgAddDTO);

        // 2.插入默认欢迎语模板
        // 如果是员工欢迎语且特殊时段欢迎语不为空，则设置存在子特殊欢迎语标示
        if (CollectionUtils.isNotEmpty(welComeMsgAddDTO.getWeMsgTlpSpecialRules())) {
            welComeMsgAddDTO.getWeMsgTlp().setExistSpecialFlag(Boolean.TRUE);
        }
        weMsgTlpMapper.insert(welComeMsgAddDTO.getWeMsgTlp());
        long defaultMsgId = welComeMsgAddDTO.getWeMsgTlp().getId();

        // 3 插入默认欢迎语模板素材
        weMsgTlpMaterialService.saveDefaultMaterial(defaultMsgId, welComeMsgAddDTO.getWeMsgTlp().getDefaultMaterialList());

        // 4.如果有则插入特殊欢迎语模板
        if (CollectionUtils.isNotEmpty(welComeMsgAddDTO.getWeMsgTlpSpecialRules())) {
            //3.1插入特殊欢迎语
            weMsgTlpSpecialRuleService.saveSpecialMsgBatch(defaultMsgId, welComeMsgAddDTO.getWeMsgTlpSpecialRules());
            // 3.2插入特殊欢迎语模板素材
            weMsgTlpMaterialService.saveSpecialMaterial(defaultMsgId, welComeMsgAddDTO.getWeMsgTlpSpecialRules());
        }

        // 5.插入员工使用范围
        weMsgTlpScopeService.saveScopeBatch(defaultMsgId, welComeMsgAddDTO.getUseUserIds());
    }

    /**
     * 校验新增好友欢迎语数据
     *
     * @param welComeMsgAddDTO
     */
    private void checkEmployInsert(WelComeMsgAddDTO welComeMsgAddDTO) {
        checkInsert(welComeMsgAddDTO);

        // 校验使用人员
        if (CollectionUtils.isEmpty(welComeMsgAddDTO.getUseUserIds())) {
            throw new CustomException(ResultTip.TIP_CHECK_STAFF);
        }

        // 校验特殊时段欢迎语长度
        if (CollectionUtils.isNotEmpty(welComeMsgAddDTO.getWeMsgTlpSpecialRules())) {
            for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : welComeMsgAddDTO.getWeMsgTlpSpecialRules()) {
                // 校验欢迎语和附件不能同时为空
                if (StringUtils.isEmpty(weMsgTlpSpecialRule.getSpecialWelcomeMsg()) && CollectionUtils.isEmpty(weMsgTlpSpecialRule.getSpecialMaterialList())) {
                    throw new CustomException(ResultTip.TIP_PLEASE_INPUT_DEFAULT_MSG);
                }
                // 校验特殊欢迎语附件数量
                checkMaterialNum(weMsgTlpSpecialRule.getSpecialMaterialList(), Boolean.FALSE);
                // 校验长度
                if (StringUtils.isNotEmpty(weMsgTlpSpecialRule.getSpecialWelcomeMsg())) {
                    WelcomeMsgTplTypeEnum.validLength(welComeMsgAddDTO.getWeMsgTlp().getWelcomeMsgTplType(), weMsgTlpSpecialRule.getSpecialWelcomeMsg());
                }
                if (ObjectUtils.isEmpty(weMsgTlpSpecialRule.getWeekendList())) {
                    throw new CustomException(ResultTip.TIP_PLEASE_INPUT_SPECIAL_MSG_TIME);
                }
            }
        }

    }


    /**
     * 新增群欢迎语模板
     *
     * @param welComeMsgAddDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertWeMsgTlpWithGroup(WelComeMsgAddDTO welComeMsgAddDTO) {
        // 1.内容与参数校验
        checkGroupInsert(welComeMsgAddDTO);

        // 2.保存群欢迎语模板
        weMsgTlpMapper.insert(welComeMsgAddDTO.getWeMsgTlp());
        Long defaultMsgId = welComeMsgAddDTO.getWeMsgTlp().getId();

        // 3.保存群欢迎语素材，同步企业微信
        weMsgTlpMaterialService.saveGroupMaterial(defaultMsgId,
                welComeMsgAddDTO.getWeMsgTlp().getDefaultWelcomeMsg(),
                welComeMsgAddDTO.getWeMsgTlp().getCorpId(), welComeMsgAddDTO.getWeMsgTlp().getDefaultMaterialList(),welComeMsgAddDTO.getWeMsgTlp().isNoticeFlag());

    }

    /**
     * 校验新增好友欢迎语数据
     *
     * @param welComeMsgAddDTO
     */
    private void checkGroupInsert(WelComeMsgAddDTO welComeMsgAddDTO) {
        checkInsert(welComeMsgAddDTO);
    }


    /**
     * 详情
     *
     * @param weMsgTlp 默认欢迎语id
     * @return
     */
    @Override
    public WeMsgTlpListVO detail(WeMsgTlp weMsgTlp) {
        if (StringUtils.isEmpty(weMsgTlp.getCorpId()) || weMsgTlp.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        final WeMsgTlpListVO weMsgTlpListVO = weMsgTlpMapper.selectWeMsgTlpList(weMsgTlp).get(0);
        buildExtraMaterial(weMsgTlpListVO.getDefaultMaterialList(), weMsgTlp.getCorpId());
        //特殊规则附件
        weMsgTlpListVO.getWeMsgTlpSpecialRules().forEach(item -> {
            buildExtraMaterial(item.getSpecialMaterialList(), weMsgTlp.getCorpId());
        });
        return weMsgTlpListVO;
    }

    /**
     * 组装雷达数据
     *
     * @param materialList
     * @param corpId
     */
    private void buildExtraMaterial(List<WeMsgTlpMaterial> materialList, String corpId) {
        if (CollectionUtils.isEmpty(materialList)) {
            return;
        }
        materialList.forEach(item -> {
            if (AttachmentTypeEnum.RADAR.getMessageType().equals(item.getType())) {
                item.setRadar(SpringUtils.getBean(WeRadarService.class).getRadar(corpId, item.getExtraId()));
            } else if (AttachmentTypeEnum.FORM.getMessageType().equals(item.getType())) {
                item.setForm(ExtraMaterialUtils.getForm(item.getExtraId()));
            }
        });
    }

    /**
     * 修改好友欢迎语
     *
     * @param welComeMsgUpdateDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateWeMsgTlpWithEmploy(WelComeMsgUpdateEmployDTO welComeMsgUpdateDTO) {
        if (StringUtils.isEmpty(welComeMsgUpdateDTO.getCorpId()) || welComeMsgUpdateDTO.getId() == null) {
            log.error("企业id或者欢迎语id参数错误");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 查询欢迎语类型
        WeMsgTlp weMsgTlp = weMsgTlpMapper.selectOne(new LambdaQueryWrapper<WeMsgTlp>()
                .eq(WeMsgTlp::getId, welComeMsgUpdateDTO.getId())
                .eq(WeMsgTlp::getCorpId, welComeMsgUpdateDTO.getCorpId()));
        // 检查数据是否合法
        checkUpdateWithEmploy(welComeMsgUpdateDTO);
        updateByEmploy(welComeMsgUpdateDTO, weMsgTlp);
    }


    /**
     * 修改好友欢迎语
     *
     * @param welComeMsgUpdateDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateWeMsgTlpWithGroup(WelComeMsgUpdateGroupDTO welComeMsgUpdateDTO) {
        if (StringUtils.isEmpty(welComeMsgUpdateDTO.getCorpId()) || welComeMsgUpdateDTO.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 查询欢迎语类型
        WeMsgTlp weMsgTlp = weMsgTlpMapper.selectOne(new LambdaQueryWrapper<WeMsgTlp>()
                .eq(WeMsgTlp::getId, welComeMsgUpdateDTO.getId())
                .eq(WeMsgTlp::getCorpId, welComeMsgUpdateDTO.getCorpId()));
        // 检查数据是否合法
        checkUpdateWithGroup(welComeMsgUpdateDTO, weMsgTlp.getWelcomeMsgTplType());
        updateByGroup(welComeMsgUpdateDTO, weMsgTlp);
    }

    /**
     * 查询好友欢迎语素材
     *
     * @param userId 员工id
     * @param corpId 企业id
     * @return 不存在返回null
     */
    @Override
    public WeEmployMaterialVO selectMaterialByUserId(String userId, String corpId) {
        WeEmployMaterialVO.WeEmployMaterialVOBuilder builder = WeEmployMaterialVO.builder();
        List<WeMsgTlpMaterial> materialList;
        // 1.查询员工使用范围的最新添加的欢迎语
        WeMsgTlp weMsgTlp = weMsgTlpMapper.selectLatestByUserId(userId, corpId);
        if (weMsgTlp == null) {
            return null;
        }
        // 2.判断是否存在特殊欢迎语，存在则判断特殊欢迎语是否处于可用范围内
        if (Boolean.TRUE.equals(weMsgTlp.getExistSpecialFlag())) {
            HitSpecialWelcomeMsgMaterialVO hitRuleMaterialVO = returnHitRuleMaterial(weMsgTlp.getId());
            if (hitRuleMaterialVO != null) {
                // 存在且命中特殊时段欢迎语，返回对应附件
                builder.defaultMsg(hitRuleMaterialVO.getSpecialMsg());
                return builder.weMsgTlpMaterialList(hitRuleMaterialVO.getSpecialMaterial()).build();
            }
        }
        // 没有存在特殊欢迎语，或则没有命中特殊欢迎语，返回默认欢迎语素材附件
        if (StringUtils.isNotEmpty(weMsgTlp.getDefaultWelcomeMsg())) {
            builder.defaultMsg(weMsgTlp.getDefaultWelcomeMsg());
        }
        materialList = weMsgTlpMaterialService.list(new LambdaQueryWrapper<WeMsgTlpMaterial>()
                .eq(WeMsgTlpMaterial::getDefaultMsgId, weMsgTlp.getId())
                // 默认欢迎语素材的特殊欢迎语id为0
                .eq(WeMsgTlpMaterial::getSpecialMsgId, 0)
                .orderByAsc(WeMsgTlpMaterial::getSortNo));
        return builder.weMsgTlpMaterialList(materialList).build();
    }

    /**
     * 群欢迎语素材统计
     *
     * @param corpId 企业id
     * @return
     */
    @Override
    public WelcomeMsgGroupMaterialCountVO groupCount(String corpId) {
        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        Integer count = weMsgTlpMapper.groupMaterialCount(corpId);
        return new WelcomeMsgGroupMaterialCountVO(count);
    }

    /**
     * 删除好友欢迎语模板信息
     *
     * @param corpId 企业id
     * @param ids    欢迎语ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteEmployWeMsgTlpById(String corpId, List<Long> ids) {
        preDeleteCheck(corpId, ids, WelcomeMsgTplTypeEnum.EMP_WELCOME);
        // 1.删除默认欢迎语
        weMsgTlpMapper.deleteBatchIds(ids);
        // 2.删除使用关系表
        weMsgTlpScopeService.remove(new LambdaQueryWrapper<WeMsgTlpScope>()
                .in(WeMsgTlpScope::getMsgTlpId, ids));
        // 3.删除特殊欢迎语
        weMsgTlpSpecialRuleService.remove(new LambdaQueryWrapper<WeMsgTlpSpecialRule>()
                .in(WeMsgTlpSpecialRule::getMsgTlpId, ids));
        // 4.删除所有素材
        weMsgTlpMaterialService.remove(new LambdaQueryWrapper<WeMsgTlpMaterial>()
                .in(WeMsgTlpMaterial::getDefaultMsgId, ids));
    }

    /**
     * 删除好友欢迎语模板信息
     *
     * @param corpId 企业id
     * @param ids    欢迎语ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGroupWeMsgTlpById(String corpId, List<Long> ids) {
        preDeleteCheck(corpId, ids, WelcomeMsgTplTypeEnum.GROUP_WELCOME);
        // 1. 同步企业微信接口删除所有素材
        weMsgTlpMaterialService.removeGroupMaterial(ids, corpId);
        // 2.删除默认欢迎语
        weMsgTlpMapper.deleteBatchIds(ids);
    }

    /**
     * 删除前参数校验
     */
    private void preDeleteCheck(String corpId, List<Long> ids, WelcomeMsgTplTypeEnum tplTypeEnum) {
        if (StringUtils.isEmpty(corpId) || CollectionUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        Integer count = weMsgTlpMapper.selectCount(new LambdaQueryWrapper<WeMsgTlp>()
                .eq(WeMsgTlp::getCorpId, corpId)
                .eq(WeMsgTlp::getWelcomeMsgTplType, tplTypeEnum.getType())
                .in(WeMsgTlp::getId, ids));
        if (!count.equals(ids.size())) {
            log.error("所传欢迎语ids集合中存在类型与接口不相符");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    /**
     * 查询命中的特殊欢迎语素材
     *
     * @param defaultMsgId 默认欢迎语id
     * @return
     */
    private HitSpecialWelcomeMsgMaterialVO returnHitRuleMaterial(Long defaultMsgId) {
        // 1.查询所有特殊规则欢迎语
        List<WeMsgTlpSpecialRule> specialRuleList = weMsgTlpSpecialRuleService.list(new LambdaQueryWrapper<WeMsgTlpSpecialRule>()
                .eq(WeMsgTlpSpecialRule::getMsgTlpId, defaultMsgId));
        // 2.判断是否命中特殊时段
        WeMsgTlpSpecialRule hitRule = hitRule(specialRuleList);
        if (hitRule != null) {
            return new HitSpecialWelcomeMsgMaterialVO(hitRule.getSpecialWelcomeMsg(),
                    weMsgTlpMaterialService.list(new LambdaQueryWrapper<WeMsgTlpMaterial>()
                            .eq(WeMsgTlpMaterial::getDefaultMsgId, defaultMsgId)
                            .eq(WeMsgTlpMaterial::getSpecialMsgId, hitRule.getId())
                            .orderByAsc(WeMsgTlpMaterial::getSortNo)));
        }
        return null;
    }

    /**
     * 是否命中特殊时段，命中则返回特殊时段欢迎语详情
     *
     * @param specialRuleList 特殊时段列表
     * @return 未命中返回null
     */
    private WeMsgTlpSpecialRule hitRule(List<WeMsgTlpSpecialRule> specialRuleList) {
        for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : specialRuleList) {
            // 判断是否命中
            // 1.查询weekend是否命中
            LocalDateTime localDateTime = LocalDateTime.now();
            Date date = new Date();
            Time nowTime = Time.valueOf(DateUtils.parseDateToStr(DateUtils.HH_MM_SS, date));
            for (String weekNum : weMsgTlpSpecialRule.getWeekends().split(",")) {
                if (localDateTime.getDayOfWeek().getValue() == Integer.parseInt(weekNum)) {
                    if (weMsgTlpSpecialRule.getWeekendBeginTime().compareTo(nowTime) <= 0 && weMsgTlpSpecialRule.getWeekendEndTime().compareTo(nowTime) >= 0) {
                        return weMsgTlpSpecialRule;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检查好友欢迎语更新数据
     *
     * @param welComeMsgUpdateDTO
     */
    private void checkUpdateWithEmploy(WelComeMsgUpdateEmployDTO welComeMsgUpdateDTO) {
        checkUpdate(welComeMsgUpdateDTO);

        checkMaterialNum(welComeMsgUpdateDTO.getDefaultMaterialList(), Boolean.FALSE);

        // 校验员工
        if (CollectionUtils.isEmpty(welComeMsgUpdateDTO.getUseUserIds())) {
            throw new CustomException(ResultTip.TIP_CHECK_STAFF);
        }

        if (CollectionUtils.isNotEmpty(welComeMsgUpdateDTO.getWeMsgTlpSpecialRules())) {
            for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : welComeMsgUpdateDTO.getWeMsgTlpSpecialRules()) {
                checkMaterialNum(weMsgTlpSpecialRule.getSpecialMaterialList(), Boolean.FALSE);
            }
        }
    }

    /**
     * 校验素材数量
     *
     * @param defaultMaterials 新添加的素材
     * @param groupFlag        是否群欢迎语
     */
    private void checkMaterialNum(List<WeMsgTlpMaterial> defaultMaterials, boolean groupFlag) {
        int size = CollectionUtils.isEmpty(defaultMaterials) ? 0 : defaultMaterials.size();
        int maxNum = groupFlag ? WeConstans.GROUP_MAX_ATTACHMENT_NUM : WeConstans.MAX_ATTACHMENT_NUM;
        if (size > maxNum) {
            throw new CustomException(ResultTip.TIP_ATTACHMENT_OVER);
        }
    }

    /**
     * 检查修改群欢迎语数据
     */
    private void checkUpdateWithGroup(WelComeMsgUpdateGroupDTO welComeMsgUpdateDTO, Integer welcomeMsgTplType) {
        checkUpdate(welComeMsgUpdateDTO);

        checkMaterialNum(welComeMsgUpdateDTO.getDefaultMaterialList(), Boolean.TRUE);

        if (!WelcomeMsgTplTypeEnum.GROUP_WELCOME.getType().equals(welcomeMsgTplType)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    /**
     * 更新好友欢迎语
     *
     * @param welComeMsgUpdateDTO
     * @param weMsgTlp            欢迎语模板
     */
    private void updateByEmploy(WelComeMsgUpdateEmployDTO welComeMsgUpdateDTO, WeMsgTlp weMsgTlp) {
        // 1.员工
        weMsgTlpScopeService.updateScope(welComeMsgUpdateDTO.getId(), welComeMsgUpdateDTO.getUseUserIds());
        // 2.默认欢迎语
        weMsgTlp.setDefaultWelcomeMsg(welComeMsgUpdateDTO.getDefaultWelcomeMsg());
        boolean notEmptyFlag = CollectionUtils.isNotEmpty(welComeMsgUpdateDTO.getWeMsgTlpSpecialRules());
        weMsgTlp.setExistSpecialFlag(notEmptyFlag);
        weMsgTlpMapper.updateById(weMsgTlp);
        // 2.1修改默认欢迎语附件
        weMsgTlpMaterialService.updateDefaultEmployMaterial(welComeMsgUpdateDTO.getRemoveMaterialIds(), welComeMsgUpdateDTO.getDefaultMaterialList(), weMsgTlp.getId());

        // 3.特殊欢迎语
        if (notEmptyFlag) {
            // 3.1 修改特殊欢迎语
            weMsgTlpSpecialRuleService.updateSpecialRuleMsg(welComeMsgUpdateDTO.getRemoveSpecialRuleIds(), welComeMsgUpdateDTO.getWeMsgTlpSpecialRules(), weMsgTlp.getId());
            // 3.2修改特殊欢迎语附件
            if (CollectionUtils.isNotEmpty(welComeMsgUpdateDTO.getWeMsgTlpSpecialRules())) {
                List<Long> specialRuleMaterialIds = new ArrayList<>();
                for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : welComeMsgUpdateDTO.getWeMsgTlpSpecialRules()) {
                    if (CollectionUtils.isNotEmpty(weMsgTlpSpecialRule.getRemoveSpecialRuleMaterialIds())) {
                        specialRuleMaterialIds.addAll(weMsgTlpSpecialRule.getRemoveSpecialRuleMaterialIds());
                    }
                }
                weMsgTlpMaterialService.updateSpecialMaterial(specialRuleMaterialIds, welComeMsgUpdateDTO.getWeMsgTlpSpecialRules(), weMsgTlp.getId());
            }
        } else {
            // 删除当前欢迎语的特殊欢迎语时段内容
            weMsgTlpSpecialRuleService.remove(new LambdaQueryWrapper<WeMsgTlpSpecialRule>()
                    .eq(WeMsgTlpSpecialRule::getMsgTlpId, weMsgTlp.getId()));
            // 删除对应素材
            weMsgTlpMaterialService.remove(new LambdaQueryWrapper<WeMsgTlpMaterial>()
                    .eq(WeMsgTlpMaterial::getDefaultMsgId, weMsgTlp.getId())
                    .notIn(WeMsgTlpMaterial::getSpecialMsgId, 0));
        }
    }

    /**
     * 更新群欢迎语
     *
     * @param welComeMsgUpdateDTO
     * @param weMsgTlp
     */
    private void updateByGroup(WelComeMsgUpdateGroupDTO welComeMsgUpdateDTO, WeMsgTlp weMsgTlp) {
        // 1.默认欢迎语
        // 1.1 修改欢迎语
        weMsgTlp.setDefaultWelcomeMsg(welComeMsgUpdateDTO.getDefaultWelcomeMsg());
        weMsgTlp.setNoticeFlag(welComeMsgUpdateDTO.isNoticeFlag());
        weMsgTlpMapper.updateById(weMsgTlp);
        // 1.2 修改默认群欢迎语素材
        weMsgTlpMaterialService.updateDefaultGroupMaterial(welComeMsgUpdateDTO.getDefaultWelcomeMsg(), welComeMsgUpdateDTO.getRemoveMaterialIds(), welComeMsgUpdateDTO.getDefaultMaterialList(), weMsgTlp.getId(), weMsgTlp.getTemplateId(), weMsgTlp.getCorpId());

    }

    /**
     * 检查基本的更新数据
     *
     * @param welComeMsgUpdateDTO
     */
    private void checkUpdate(WelComeMsgUpdateDTO welComeMsgUpdateDTO) {
        if (ObjectUtils.isEmpty(welComeMsgUpdateDTO)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 欢迎语和附件不能同时为空
        if ((StringUtils.isEmpty(welComeMsgUpdateDTO.getDefaultWelcomeMsg())
                && CollectionUtils.isEmpty(welComeMsgUpdateDTO.getDefaultMaterialList()))) {
            throw new CustomException(ResultTip.TIP_PLEASE_INPUT_DEFAULT_MSG);
        }
    }

    /**
     * 检查欢迎语insert参数校验
     *
     * @param welComeMsgAddDTO
     */
    private void checkInsert(WelComeMsgAddDTO welComeMsgAddDTO) {
        if (ObjectUtils.isEmpty(welComeMsgAddDTO) || ObjectUtils.isEmpty(welComeMsgAddDTO.getWeMsgTlp())
                || StringUtils.isEmpty(welComeMsgAddDTO.getWeMsgTlp().getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String defaultWelcomeMsg = welComeMsgAddDTO.getWeMsgTlp().getDefaultWelcomeMsg();

        // 欢迎语和附件不能同时为空
        if (StringUtils.isEmpty(defaultWelcomeMsg) && CollectionUtils.isEmpty(welComeMsgAddDTO.getWeMsgTlp().getDefaultMaterialList())) {
            throw new CustomException(ResultTip.TIP_PLEASE_INPUT_DEFAULT_MSG);
        }
        // 校验欢迎语长度
        if (StringUtils.isNotEmpty(defaultWelcomeMsg)) {
            WelcomeMsgTplTypeEnum.validLength(welComeMsgAddDTO.getWeMsgTlp().getWelcomeMsgTplType(), defaultWelcomeMsg);
        }
        // 校验默认欢迎语附件数量
        boolean groupFlag = welComeMsgAddDTO.getWeMsgTlp().getWelcomeMsgTplType().equals(WelcomeMsgTplTypeEnum.GROUP_WELCOME.getType());
        checkMaterialNum(welComeMsgAddDTO.getWeMsgTlp().getDefaultMaterialList(), groupFlag);

    }

}
