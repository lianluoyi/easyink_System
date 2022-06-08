package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeGroupTag;
import com.easywecom.wecom.domain.WeGroupTagCategory;
import com.easywecom.wecom.domain.dto.wegrouptag.*;
import com.easywecom.wecom.domain.vo.wegrouptag.PageWeGroupTagCategoryVO;
import com.easywecom.wecom.domain.vo.wegrouptag.PageWeGroupTagVO;
import com.easywecom.wecom.domain.vo.wegrouptag.WeGroupTagCategoryVO;
import com.easywecom.wecom.mapper.WeGroupTagCategoryMapper;
import com.easywecom.wecom.service.WeGroupTagCategoryService;
import com.easywecom.wecom.service.WeGroupTagService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * 类名：WeGroupTagCategoryServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:57
 */
@Service
public class WeGroupTagCategoryServiceImpl extends ServiceImpl<WeGroupTagCategoryMapper, WeGroupTagCategory> implements WeGroupTagCategoryService {

    private final WeGroupTagService weGroupTagService;

    @Autowired
    public WeGroupTagCategoryServiceImpl(WeGroupTagService weGroupTagService) {
        this.weGroupTagService = weGroupTagService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddWeGroupTagCategoryDTO weGroupTagCategory) {
        //校验请求参数
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId()) || StringUtils.isBlank(weGroupTagCategory.getName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //默认标签组最多可设置3000个
        Integer count = baseMapper.selectCount(new LambdaQueryWrapper<WeGroupTagCategory>().eq(WeGroupTagCategory::getCorpId, weGroupTagCategory.getCorpId()));
        if (count > WeConstans.DEFAULT_WE_GROUP_TAG_CATEGORY_SIZE) {
            throw new CustomException(ResultTip.TIP_GROUP_TAG_CATEGORY_OVER_MAX_SIZE);
        }
        //校验标签组是否重名
        boolean sameFlag = baseMapper.checkSameName(weGroupTagCategory.getCorpId(), weGroupTagCategory.getName());
        if (sameFlag) {
            String message = String.format(ResultTip.TIP_GROUP_TAG_CATEGORY_SAME_NAME.getTipMsg(), weGroupTagCategory.getName());
            throw new CustomException(ResultTip.TIP_GROUP_TAG_CATEGORY_SAME_NAME, message);
        }
        baseMapper.insert(weGroupTagCategory);
        //保存群标签列表
        if (CollectionUtils.isNotEmpty(weGroupTagCategory.getTagList())) {
            weGroupTagService.batchInsert(weGroupTagCategory.getCorpId(), weGroupTagCategory.getId(), weGroupTagCategory.getTagList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateWeGroupTagCategoryDTO weGroupTagCategory) {
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId()) || weGroupTagCategory.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //删除标签
        if (CollectionUtils.isNotEmpty(weGroupTagCategory.getDelList())) {
            weGroupTagService.delTag(weGroupTagCategory.getCorpId(), weGroupTagCategory.getDelList());
        }
        //保存新增的标签列表
        if (CollectionUtils.isNotEmpty(weGroupTagCategory.getAddList())) {
            weGroupTagService.batchInsert(weGroupTagCategory.getCorpId(), weGroupTagCategory.getId(), weGroupTagCategory.getAddList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DelWeGroupTagCategoryDTO weGroupTagCategory) {
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId()) || CollectionUtils.isEmpty(weGroupTagCategory.getDelList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //批量删除标签列表
        weGroupTagService.delByGroupId(weGroupTagCategory.getCorpId(), weGroupTagCategory.getDelList());
        //批量删除标签组
        baseMapper.delByIdList(weGroupTagCategory.getCorpId(), weGroupTagCategory.getDelList());
    }

    @Override
    public List<WeGroupTagCategoryVO> list(FindWeGroupTagCategoryDTO weGroupTagCategory) {
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeGroupTagCategoryVO> list = baseMapper.list(weGroupTagCategory);
        for (WeGroupTagCategoryVO weGroupTagCategoryVO : list) {
            List<WeGroupTag> tagList = weGroupTagCategoryVO.getTagList();
            tagList.sort(Comparator.comparing(WeGroupTag::getId));
            weGroupTagCategoryVO.setTagList(tagList);
        }
        return list;
    }

    @Override
    public List<PageWeGroupTagCategoryVO> page(PageWeGroupTagCategoryDTO weGroupTagCategory) {
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<PageWeGroupTagCategoryVO> page = baseMapper.page(weGroupTagCategory);
        for (PageWeGroupTagCategoryVO pageWeGroupTagCategoryVO : page) {
            List<PageWeGroupTagVO> weTags = pageWeGroupTagCategoryVO.getWeTags();
            weTags.sort(Comparator.comparing(PageWeGroupTagVO::getTagId));
            pageWeGroupTagCategoryVO.setWeTags(weTags);
        }
        return page;
    }

    @Override
    public WeGroupTagCategoryVO findInfo(FindWeGroupTagCategoryDTO weGroupTagCategory) {
        if (weGroupTagCategory == null || StringUtils.isBlank(weGroupTagCategory.getCorpId()) || weGroupTagCategory.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.findById(weGroupTagCategory.getCorpId(), weGroupTagCategory.getId());
    }

}
