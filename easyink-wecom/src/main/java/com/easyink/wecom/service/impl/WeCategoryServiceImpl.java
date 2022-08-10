package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.Tree;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeCategoryMediaTypeEnum;
import com.easyink.common.enums.WeTempMaterialEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.TreeUtil;
import com.easyink.wecom.domain.WeCategory;
import com.easyink.wecom.domain.WeMaterial;
import com.easyink.wecom.domain.dto.WeCategorySidebarSwitchDTO;
import com.easyink.wecom.domain.vo.WeCategoryBaseInfoVO;
import com.easyink.wecom.domain.vo.WeCategoryVO;
import com.easyink.wecom.mapper.WeCategoryMapper;
import com.easyink.wecom.service.WeCategoryService;
import com.easyink.wecom.service.WeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 素材类别服务
 */
@Slf4j
@Service
public class WeCategoryServiceImpl extends ServiceImpl<WeCategoryMapper, WeCategory> implements WeCategoryService {

    private final WeMaterialService weMaterialService;
    private final RuoYiConfig ruoYiConfig;
    @Autowired
    public WeCategoryServiceImpl(WeMaterialService weMaterialService, RuoYiConfig ruoYiConfig) {
        this.weMaterialService = weMaterialService;
        this.ruoYiConfig = ruoYiConfig;
    }

    @Override
    public void insertWeCategory(WeCategory category) {
        //判断是否存在相同的名称
        WeCategory weCategory = this.getOne(new LambdaQueryWrapper<WeCategory>()
                .eq(WeCategory::getCorpId, category.getCorpId())
                .eq(WeCategory::getMediaType, category.getMediaType())
                .eq(WeCategory::getName, category.getName())
                .eq(WeCategory::getDelFlag, Constants.NORMAL_CODE).last(GenConstants.LIMIT_1));
        if (null != weCategory) {
            throw new WeComException("名称已存在！");
        }
        category.setId(SnowFlakeUtil.nextId());
        this.save(category);
    }

    @Override
    public void updateWeCategory(WeCategory category) {
        //判断是否存在相同的名称
        WeCategory weCategory = this.getOne(new LambdaQueryWrapper<WeCategory>()
                .eq(WeCategory::getCorpId, category.getCorpId())
                .eq(WeCategory::getMediaType, category.getMediaType())
                .eq(WeCategory::getName, category.getName())
                .eq(WeCategory::getDelFlag, Constants.NORMAL_CODE).last(GenConstants.LIMIT_1));

        if (null != weCategory) {
            throw new WeComException("名称已存在！");
        }
        this.updateById(category);
    }


    @Override
    public List<? extends Tree<?>> findWeCategoryByMediaType(String corpId, Integer mediaType) {
        if(StringUtils.isBlank(corpId)){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeCategoryVO> weCategoryVos = new ArrayList<>();
        List<Integer> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(mediaType);
        List<WeCategory> weCategories = baseMapper.selectByCorpIdAndMediaType(corpId,mediaTypeList);

        weCategories.forEach(c -> {
            WeCategoryVO weCategoryVo = new WeCategoryVO();
            weCategoryVo.setId(c.getId());
            weCategoryVo.setName(c.getName());
            weCategoryVos.add(weCategoryVo);
        });
        return TreeUtil.build(weCategoryVos);
    }

    @Override
    public void deleteWeCategoryById(String corpId, Long[] ids) {
        this.baseMapper.deleteWeCategoryById(corpId, ids);
    }


    /**
     * 初始化素材库分组
     *
     * @param corpId 授权企业ID
     * @return Boolean
     */
    @Override
    public Boolean initCategory(String corpId) {
        return initCategory(corpId, Constants.SUPER_ADMIN);
    }


    /**
     * 初始化素材库分组
     *
     * @param corpId   授权企业ID
     * @param createBy 创建人
     * @return Boolean
     */
    @Override
    public Boolean initCategory(String corpId, String createBy) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (StringUtils.isBlank(createBy)) {
            createBy = Constants.SUPER_ADMIN;
        }
        List<WeCategory> addList = new ArrayList<>();

        List<WeCategory> weCategoryList = baseMapper.selectByCorpIdAndMediaType(corpId, WeCategoryMediaTypeEnum.getMediaTypeList());
        List<WeMaterial> initMaterialList = new ArrayList<>();
        //未初始化过，则将所有初始化数据放入
        WeCategory dataWeCategory;
        if (CollectionUtils.isEmpty(weCategoryList)) {
            for (WeCategoryMediaTypeEnum typeEnum : WeCategoryMediaTypeEnum.values()) {
                dataWeCategory = new WeCategory(corpId, typeEnum, createBy);
                addList.add(dataWeCategory);
                List<WeMaterial> weMaterials = initMaterialList(dataWeCategory.getId(), typeEnum, createBy);
                if(CollectionUtils.isNotEmpty(weMaterials)){
                    initMaterialList.addAll(weMaterials);
                }
            }
        } else {
            boolean exist;
            //存在数据，可能存在初始化过程失败，不存在数据库的数据重新插入
            for (WeCategoryMediaTypeEnum typeEnum : WeCategoryMediaTypeEnum.values()) {
                exist = false;
                for (WeCategory weCategory : weCategoryList) {
                    if (typeEnum.getMediaType().equals(weCategory.getMediaType())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    dataWeCategory = new WeCategory(corpId, typeEnum, createBy);
                    addList.add(dataWeCategory);
                    List<WeMaterial> weMaterials = initMaterialList(dataWeCategory.getId(), typeEnum, createBy);
                    if(CollectionUtils.isNotEmpty(weMaterials)){
                        initMaterialList.addAll(weMaterials);
                    }
                }
            }
        }
        try {
            if (CollectionUtils.isNotEmpty(addList)) {
                baseMapper.batchInsert(addList);
            }
            if (CollectionUtils.isNotEmpty(initMaterialList)) {
                weMaterialService.batchInsertWeMaterial(initMaterialList);
            }
            return true;
        } catch (Exception e) {
            log.error("initCategory error.{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 初始化素材数据
     *
     * @param categoryId 分组ID
     * @param typeEnum   素材分组数据枚举
     * @return List<WeMaterial>
     */
    private List<WeMaterial> initMaterialList(Long categoryId, WeCategoryMediaTypeEnum typeEnum, String createBy) {
        List<WeMaterial> initList = new ArrayList<>();
        //多租户需要初始化
        if(ruoYiConfig.isThirdServer()){
            //海报素材
            if (WeCategoryMediaTypeEnum.IMAGE.equals(typeEnum)) {
                initList.add(initWeMaterial(categoryId, "联络易宣传海报.jpg", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/联络易宣传海报.jpg", "582656", createBy));
                initList.add(initWeMaterial(categoryId, "easyWeCom宣传海报.jpg", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom宣传海报.jpg", "369664", createBy));
            }
            //视频素材
            if (WeCategoryMediaTypeEnum.VIDEO.equals(typeEnum)) {
                initList.add(initWeMaterial(categoryId, "中秋福利.mp4", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/中秋福利.mp4", "8713216", createBy));
                initList.add(initWeMaterial(categoryId, "被研发抓住做测试小白是什么体验.mp4", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/被研发抓住做测试小白是什么体验.mp4", "9373696", createBy));
            }
            //文件素材
            if (WeCategoryMediaTypeEnum.FILE.equals(typeEnum)) {
                initList.add(initWeMaterial(categoryId, "easyWeCom注册使用服务表.doc", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom注册使用服务表.doc", "58368", createBy));
                initList.add(initWeMaterial(categoryId, "easyWeCom功能列表.xlsx", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom功能列表.xlsx", "14336", createBy));
                initList.add(initWeMaterial(categoryId, "easywecom产品介绍.pdf", "https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easywecom产品介绍.pdf", "2799616", createBy));
            }
        }
        return initList;
    }

    /**
     * 初始化素材
     *
     * @param categoryId   分组ID
     * @param materialName 素材标题
     * @param materialUrl  素材链接
     * @param content      素材内容
     */
    private WeMaterial initWeMaterial(Long categoryId, String materialName, String materialUrl, String content, String createBy) {
        WeMaterial weMaterial = new WeMaterial();
        weMaterial.setId(SnowFlakeUtil.nextId());
        weMaterial.setCategoryId(categoryId);
        weMaterial.setMaterialName(materialName);
        weMaterial.setMaterialUrl(materialUrl);
        weMaterial.setContent(StringUtils.isBlank(content) ? WeConstans.DEFAULT_EMPTY_STRING : content);
        weMaterial.setDigest(WeConstans.DEFAULT_EMPTY_STRING);
        weMaterial.setCoverUrl(WeConstans.DEFAULT_EMPTY_STRING);
        weMaterial.setAudioTime(WeConstans.DEFAULT_EMPTY_STRING);
        weMaterial.setExpireTime(WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE);
        weMaterial.setShowMaterial(WeConstans.WE_MATERIAL_NOT_USING);
        weMaterial.setCreateBy(createBy);
        weMaterial.setUpdateBy(WeConstans.DEFAULT_EMPTY_STRING);
        weMaterial.setTempFlag(WeTempMaterialEnum.MATERIAL.getTempFlag());
        return weMaterial;
    }

    @Override
    public void sidebarSwitch(WeCategorySidebarSwitchDTO sidebarSwitchDTO) {
        if (sidebarSwitchDTO == null || StringUtils.isBlank(sidebarSwitchDTO.getCorpId()) || sidebarSwitchDTO.getId() == null || sidebarSwitchDTO.getUsing() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.updateSidebarSwitch(sidebarSwitchDTO);
    }

    @Override
    public List<WeCategoryBaseInfoVO> findShowWeCategory(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.selectByCorpIdAndUsing(corpId, WeConstans.WE_CATEGORY_USING);
    }

    @Override
    public List<WeCategoryBaseInfoVO> findListByCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.selectByCorpIdAndUsing(corpId, null);
    }
}
