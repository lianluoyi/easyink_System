package com.easywecom.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.enums.WeWordsCategoryTypeEnum;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.domain.WeWordsCategory;
import com.easywecom.wecom.domain.dto.wordscategory.*;
import com.easywecom.wecom.domain.vo.FindExistWordsCategoryNameList;
import com.easywecom.wecom.domain.vo.WeWordsCategoryVO;
import com.easywecom.wecom.mapper.WeWordsCategoryMapper;
import com.easywecom.wecom.service.WeWordsCategoryService;
import com.easywecom.wecom.service.WeWordsGroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名：WeWordsCategoryServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-10-25 10:49
 */
@Service
public class WeWordsCategoryServiceImpl extends ServiceImpl<WeWordsCategoryMapper, WeWordsCategory> implements WeWordsCategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(LoginUser loginUser, AddWeWordsCategoryDTO weWordsCategory) {
        //校验参数是否合法
        validParam(weWordsCategory, loginUser);
        String corpId = weWordsCategory.getCorpId();
        Integer type = weWordsCategory.getType();
        //校验name不能重名
        verifySameName(weWordsCategory.getId(), corpId, type, weWordsCategory.getName(), loginUser);
        //校验上级文件夹是否存在和是否是一级文件夹
        verifyLevel1WordsCategory(corpId, weWordsCategory.getParentId());
        if (weWordsCategory.getParentId() == null) {
            weWordsCategory.setParentId(WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID);
        }
        //设置使用范围
        String useRange = this.getUseRange(weWordsCategory.getType(), loginUser);
        weWordsCategory.setUseRange(useRange);
        //当parentId不为空时，不能设置子文件夹
        if (weWordsCategory.getParentId() != null && !WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID.equals(weWordsCategory.getParentId())
                && !CollectionUtils.isEmpty(weWordsCategory.getChildIdList())) {
            throw new CustomException(ResultTip.TIP_PARENT_NOT_LEVEL1_CATEGORY);
        }
        //设置最大的sort值
        weWordsCategory.setSort(getLastSort(corpId, type, useRange));
        baseMapper.insert(weWordsCategory);

        //有子文件夹,批量保存子文件夹数据
        if (!CollectionUtils.isEmpty(weWordsCategory.getChildIdList())) {
            //判断文件夹名是否已存在
            verifyExistWordsCategoryNameList(weWordsCategory.getChildIdList(), corpId, type, useRange, true);
            //批量创建子文件夹
            List<WeWordsCategory> addList = new ArrayList<>();
            for (WeWordsCategoryChildSortDTO childSortDTO : weWordsCategory.getChildIdList()) {
                if (StringUtils.isBlank(childSortDTO.getName()) || childSortDTO.getSort() == null) {
                    throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
                }
                addList.add(new WeWordsCategory(corpId, weWordsCategory.getId(), type, useRange, childSortDTO.getName(), childSortDTO.getSort()));
            }
            baseMapper.batchInsert(addList);
        }
    }

    /**
     * 校验多个子文件夹名称是否存在
     *
     * @param childIdList 子文件夹集合
     * @param corpId      企业ID
     * @param type        文件夹类型
     * @param useRange    使用范围
     * @param isAddFlag   是否是新增
     */
    private void verifyExistWordsCategoryNameList(List<WeWordsCategoryChildSortDTO> childIdList, String corpId, Integer type, String useRange, Boolean isAddFlag) {
        if (CollectionUtils.isEmpty(childIdList) || StringUtils.isBlank(corpId) || type == null || StringUtils.isBlank(useRange) || isAddFlag == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<String> nameList = childIdList.stream().map(WeWordsCategoryChildSortDTO::getName).collect(Collectors.toList());
        //校验当前子文件夹中 是否有重名
        long count = nameList.stream().distinct().count();
        if (nameList.size() != count) {
            throw new CustomException(ResultTip.TIP_WORDS_CATEGORY_DUPLICATION_NAME);
        }
        //查询数据库中是否有重名数据
        List<FindExistWordsCategoryNameList> existWordsCategoryNameLists = baseMapper.existsSameNameWordsCategory(corpId, type, useRange, nameList);

        if (!CollectionUtils.isEmpty(existWordsCategoryNameLists)) {
            //新增子文件夹
            if (isAddFlag) {
                List<String> sameList = existWordsCategoryNameLists.stream().map(FindExistWordsCategoryNameList::getName).collect(Collectors.toList());
                String join = String.join("、", sameList);
                throw new CustomException(getSameNameResultTipMessage(join), ResultTip.TIP_WORDS_CATEGORY_SAME_NAME.getCode());
            } else {
                //旧数据的文件夹ID与此次ID是否一致 (不一致则为重名)
                existWordsCategoryNameLists.forEach(entityClass -> {
                    for (WeWordsCategoryChildSortDTO childSortDTO : childIdList) {
                        if (entityClass.getName().trim().equals(childSortDTO.getName().trim()) && !entityClass.getId().equals(childSortDTO.getChildId())) {
                            throw new CustomException(getSameNameResultTipMessage(entityClass.getName()), ResultTip.TIP_WORDS_CATEGORY_SAME_NAME.getCode());
                        }
                    }
                });
            }
        }
    }


    /**
     * 组装文件夹重名消息
     *
     * @param message message
     * @return String
     */
    private static String getSameNameResultTipMessage(String message) {
        if (StringUtils.isBlank(message)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return String.format("“%s”%s", message, ResultTip.TIP_WORDS_CATEGORY_SAME_NAME.getTipMsg());
    }

    /**
     * 校验上级文件夹
     *
     * @param corpId   企业ID
     * @param parentId 上级文件夹ID
     */
    private void verifyLevel1WordsCategory(String corpId, Long parentId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (parentId == null) {
            return;
        }
        if (WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID.equals(parentId)) {
            return;
        }
        LambdaQueryWrapper<WeWordsCategory> wrapper = new LambdaQueryWrapper<WeWordsCategory>()
                .eq(WeWordsCategory::getCorpId, corpId)
                .eq(WeWordsCategory::getId, parentId);
        WeWordsCategory weWordsCategory = baseMapper.selectOne(wrapper);
        //判断上级文件夹是否存在
        if (weWordsCategory == null) {
            throw new CustomException(ResultTip.TIP_PARENT_WORDS_CATEGORY_NOT_EXIST);
        }
        //判断上级文件夹是否为一级文件夹
        if (!WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID.equals(weWordsCategory.getParentId())) {
            throw new CustomException(ResultTip.TIP_PARENT_NOT_LEVEL1_CATEGORY);
        }
    }

    /**
     * 设置文件夹使用范围
     *
     * @param type      文件夹类型
     * @param loginUser loginUser
     */
    private String getUseRange(Integer type, LoginUser loginUser) {
        String useRange;
        if (WeWordsCategoryTypeEnum.SELF.getType().equals(type)) {
            useRange = loginUser.getWeUser().getUserId();
        } else if (WeWordsCategoryTypeEnum.DEPARTMENT.getType().equals(type)) {
            useRange = loginUser.getWeUser().getMainDepartment().toString();
        } else {
            useRange = WeConstans.WE_ROOT_DEPARMENT_ID.toString();
        }
        return useRange;
    }

    /**
     * 校验文件夹是否重名
     *
     * @param id        文件夹ID
     * @param corpId    企业ID
     * @param type      文件夹类型
     * @param name      文件夹名称
     * @param loginUser 当前登录账号
     */
    private void verifySameName(Long id, String corpId, Integer type, String name, LoginUser loginUser) {
        LambdaQueryWrapper<WeWordsCategory> wrapper = new LambdaQueryWrapper<WeWordsCategory>()
                .eq(WeWordsCategory::getCorpId, corpId)
                .eq(WeWordsCategory::getType, type)
                .eq(WeWordsCategory::getName, name.trim());
        //个人类型下的同一个人的文件夹不能重名;同一部门下的文件夹名称不能重名;同一企业下的文件夹名称不能重名
        if (WeWordsCategoryTypeEnum.SELF.getType().equals(type)) {
            wrapper.eq(WeWordsCategory::getUseRange, loginUser.getWeUser().getUserId());
        } else if (WeWordsCategoryTypeEnum.DEPARTMENT.getType().equals(type)) {
            wrapper.eq(WeWordsCategory::getUseRange, loginUser.getWeUser().getMainDepartment());
        } else {
            wrapper.eq(WeWordsCategory::getUseRange, WeConstans.WE_ROOT_DEPARMENT_ID);
        }
        WeWordsCategory selectCategory = baseMapper.selectOne(wrapper);
        if (selectCategory != null && (id == null || !id.equals(selectCategory.getId()))) {
            throw new CustomException(getSameNameResultTipMessage(name), ResultTip.TIP_WORDS_CATEGORY_SAME_NAME.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LoginUser loginUser, UpdateWeWordsCategoryDTO updateDTO) {
        if (updateDTO == null || updateDTO.getId() == null || StringUtils.isBlank(updateDTO.getName())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String corpId = updateDTO.getCorpId();
        //校验name不能重名
        verifySameName(updateDTO.getId(), corpId, updateDTO.getType(), updateDTO.getName(), loginUser);
        //判断parentId是不是一级文件夹
        if (updateDTO.getParentId() != null) {
            verifyLevel1WordsCategory(corpId, updateDTO.getParentId());
        }
        //删除子文件夹
        if (!CollectionUtils.isEmpty(updateDTO.getDelChildList())) {
            //删除话术内容
            WeWordsGroupService weWordsGroupService = SpringUtils.getBean("weWordsGroupService");
            weWordsGroupService.deleteByCategoryId(updateDTO.getDelChildList(), updateDTO.getCorpId());
            //删除子文件夹
            baseMapper.deleteByCorpIdIdList(updateDTO.getCorpId(), updateDTO.getDelChildList());
        }
        //更新子文件夹信息
        if (!CollectionUtils.isEmpty(updateDTO.getChildIdList())) {
            updateChildWeWordsCategory(updateDTO);
        }
        baseMapper.updateByIdAndCorpId(updateDTO);
    }

    /**
     * 更新子文件夹集合信息
     *
     * @param updateDTO updateDTO
     */
    private void updateChildWeWordsCategory(UpdateWeWordsCategoryDTO updateDTO) {
        //更新子文件夹顺序
        List<WeWordsCategory> addList = new ArrayList<>();
        List<WeWordsCategoryChildSortDTO> updateList = new ArrayList<>();
        WeWordsCategory weWordsCategory = baseMapper.selectById(updateDTO.getId());
        List<String> nameList = new ArrayList<>();
        //校验子文件夹参数
        updateDTO.getChildIdList().forEach(
                childSortDTO -> {
                    if (childSortDTO == null || StringUtils.isBlank(childSortDTO.getName()) && StringUtils.isBlank(childSortDTO.getName().trim()) || childSortDTO.getSort() == null) {
                        throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
                    }
                    nameList.add(childSortDTO.getName());
                }
        );
        //判断此次子文件夹名称是否有重名
        long count = nameList.stream().distinct().count();
        if (nameList.size() != count) {
            throw new CustomException(ResultTip.TIP_WORDS_CATEGORY_DUPLICATION_NAME);
        }
        //判断集合中数据和原有数据的文件夹是否有重名
        verifyExistWordsCategoryNameList(updateDTO.getChildIdList(), updateDTO.getCorpId(), updateDTO.getType(), weWordsCategory.getUseRange(), false);

        for (WeWordsCategoryChildSortDTO childSortDTO : updateDTO.getChildIdList()) {
            //子文件夹中可能存在刚加的子文件夹，所以需要区分
            if (childSortDTO.getChildId() != null) {
                updateList.add(childSortDTO);
            } else {
                addList.add(new WeWordsCategory(updateDTO.getCorpId(), updateDTO.getId(), updateDTO.getType(), weWordsCategory.getUseRange(), childSortDTO.getName(), childSortDTO.getSort()));
            }
        }

        //批量更新子文件夹的sort和name
        if (!CollectionUtils.isEmpty(updateList)) {
            baseMapper.batchUpdateChildSortAndName(updateDTO.getId(), updateDTO.getCorpId(), updateList);
        }
        //新增子文件夹
        if (!CollectionUtils.isEmpty(addList)) {
            baseMapper.batchInsert(addList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeleteWeWordsCategoryDTO deleteWeWordsCategoryDTO) {
        if (deleteWeWordsCategoryDTO == null || StringUtils.isBlank(deleteWeWordsCategoryDTO.getCorpId()) || deleteWeWordsCategoryDTO.getId() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        Long id = deleteWeWordsCategoryDTO.getId();
        String corpId = deleteWeWordsCategoryDTO.getCorpId();
        LambdaQueryWrapper<WeWordsCategory> wrapper = new LambdaQueryWrapper<WeWordsCategory>()
                .eq(WeWordsCategory::getCorpId, deleteWeWordsCategoryDTO.getCorpId())
                .eq(WeWordsCategory::getId, deleteWeWordsCategoryDTO.getId());
        WeWordsCategory wordsCategory = baseMapper.selectOne(wrapper);
        if (wordsCategory == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }

        WeWordsGroupService weWordsGroupService = SpringUtils.getBean("weWordsGroupService");
        //一级文件夹
        if (WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID.equals(wordsCategory.getParentId())) {
            wrapper = new LambdaQueryWrapper<WeWordsCategory>()
                    .eq(WeWordsCategory::getCorpId, corpId)
                    .eq(WeWordsCategory::getParentId, id);
            List<WeWordsCategory> childList = baseMapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(childList)) {
                List<Long> childIds = childList.stream().map(WeWordsCategory::getId).collect(Collectors.toList());
                //删除话术内容
                weWordsGroupService.deleteByCategoryId(childIds, corpId);
                //删除文件夹
                baseMapper.deleteByCorpIdIdList(corpId, childIds);
            }
        }
        //删除关联的话术内容
        List<Long> idList = new ArrayList<>();
        idList.add(deleteWeWordsCategoryDTO.getId());
        weWordsGroupService.deleteByCategoryId(idList, corpId);
        List<Long> delList = new ArrayList<>();
        delList.add(deleteWeWordsCategoryDTO.getId());
        baseMapper.deleteByCorpIdIdList(corpId, delList);
    }

    @Override
    public List<WeWordsCategoryVO> list(FindWeWordsCategoryDTO findWeWordsCategoryDTO) {
        if (findWeWordsCategoryDTO == null || StringUtils.isBlank(findWeWordsCategoryDTO.getCorpId()) || findWeWordsCategoryDTO.getType() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.selectWeWordsCategoryList(findWeWordsCategoryDTO);
    }

    @Override
    public List<FindExistWordsCategoryNameList> findAndAddWordsCategory(String corpId, Integer type, List<String> nameList, String useRange) {
        if (StringUtils.isBlank(corpId) || type == null || CollectionUtils.isEmpty(nameList) || StringUtils.isBlank(useRange)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //先数据去重一遍
        nameList = nameList.stream().distinct().collect(Collectors.toList());
        List<FindExistWordsCategoryNameList> existNameList = baseMapper.existsSameNameWordsCategory(corpId, type, useRange, nameList);

        //数据库中已存在的数据
        List<String> collect = existNameList.stream().map(FindExistWordsCategoryNameList::getName).collect(Collectors.toList());
        //差集
        List<String> diffList = nameList.stream().filter(item -> !collect.contains(item)).collect(Collectors.toList());
        List<WeWordsCategory> addList = new ArrayList<>();

        Long parentId = WeConstans.DEFAULT_WE_WORDS_CATEGORY_PARENT_ID;
        //查询最大的sort
        Integer lastSort = getLastSort(corpId, type, useRange);

        for (String name : diffList) {
            addList.add(new WeWordsCategory(corpId, parentId, type, useRange, name, lastSort++));
        }
        if (!CollectionUtils.isEmpty(addList)) {
            baseMapper.batchInsert(addList);
        }
        if (!CollectionUtils.isEmpty(diffList)) {
            List<FindExistWordsCategoryNameList> newNameList = baseMapper.existsSameNameWordsCategory(corpId, type, useRange, diffList);
            existNameList.addAll(newNameList);
        }
        return existNameList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSort(WeWordsCategoryChangeSortDTO weWordsCategoryChangeSortDTO) {
        if (weWordsCategoryChangeSortDTO == null || StringUtils.isBlank(weWordsCategoryChangeSortDTO.getCorpId()) || CollectionUtils.isEmpty(weWordsCategoryChangeSortDTO.getSortList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.changeSort(weWordsCategoryChangeSortDTO.getCorpId(), weWordsCategoryChangeSortDTO.getSortList());
    }

    /**
     * 查询最大的sort
     *
     * @param corpId   企业ID
     * @param type     文件夹类型
     * @param useRange 使用范围
     * @return Integer
     */
    private Integer getLastSort(String corpId, Integer type, String useRange) {
        LambdaQueryWrapper<WeWordsCategory> wrapper = new LambdaQueryWrapper<WeWordsCategory>()
                .eq(WeWordsCategory::getCorpId, corpId)
                .eq(WeWordsCategory::getType, type)
                .eq(WeWordsCategory::getUseRange, useRange)
                .orderByDesc(WeWordsCategory::getSort)
                .last(GenConstants.LIMIT_1);
        //设置最大的sort值
        WeWordsCategory category = baseMapper.selectOne(wrapper);
        return category == null ? WeConstans.DEFAULT_WE_WORDS_CATEGORY_HIGHEST_SORT : category.getSort() + 1;
    }

    /**
     * 新增/修改校验请求参数
     *
     * @param weWordsCategory weWordsCategory
     */
    private void validParam(WeWordsCategory weWordsCategory, LoginUser loginUser) {
        if (weWordsCategory == null || StringUtils.isBlank(weWordsCategory.getCorpId())
                || weWordsCategory.getType() == null
                || StringUtils.isBlank(weWordsCategory.getName())
                || StringUtils.isBlank(weWordsCategory.getName().trim())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //管理员不能创建部门文件夹
        if (loginUser.getWeUser() == null && (!WeWordsCategoryTypeEnum.CORP.getType().equals(weWordsCategory.getType()))) {
            throw new CustomException(ResultTip.TIP_ADMIN_NOT_CREATE_DEPART_WORDS_CATEGORY);
        }
    }
}
