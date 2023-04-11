package com.easyink.wecom.service.impl.form;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.form.FormConstants;
import com.easyink.common.domain.ChangeSortModel;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.TreeUtil;
import com.easyink.wecom.domain.dto.form.ChangeFormGroupSortDTO;
import com.easyink.wecom.domain.dto.form.FormGroupAddDTO;
import com.easyink.wecom.domain.dto.form.FormGroupUpdateDTO;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import com.easyink.wecom.domain.enums.form.FormSourceType;
import com.easyink.wecom.domain.vo.form.FormGroupTreeVO;
import com.easyink.wecom.domain.vo.form.FormGroupTrees;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.form.WeFormGroupMapper;
import com.easyink.wecom.service.WeDepartmentService;
import com.easyink.wecom.service.form.WeFormGroupService;
import com.easyink.wecom.service.form.WeFormService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.easyink.common.enums.ResultTip.TIP_PARAM_MISSING;

/**
 * 表单分组表(WeFormGroup)表服务实现类
 *
 * @author tigger
 * @since 2023-01-09 11:23:22
 */
@Slf4j
@Service("weFormGroupService")
public class WeFormGroupServiceImpl extends ServiceImpl<WeFormGroupMapper, WeFormGroup> implements WeFormGroupService {

    private final WeFormService weFormService;
    private final WeDepartmentService weDepartmentService;

    @Lazy
    public WeFormGroupServiceImpl(WeFormService weFormService, WeDepartmentService weDepartmentService) {
        this.weFormService = weFormService;
        this.weDepartmentService = weDepartmentService;
    }

    @Override
    public List<FormGroupTreeVO> selectTree(Integer sourceType, Integer departmentId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        Optional<FormSourceType> sourceTypeOp = FormSourceType.getByCode(sourceType);
        if (!sourceTypeOp.isPresent()) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_SOURCE_TYPE_ERROR);
        }

        String createBy = sourceTypeOp.get() == FormSourceType.PERSONAL ? LoginTokenService.getLoginUser().getUserId() : null;
        // 查询过条件后的表单分组
        List<WeFormGroup> formGroupList = baseMapper.selectByCorpIdAndSourceType(corpId, sourceType, departmentId, createBy);

        List<FormGroupTreeVO> formGroupVOList = new ArrayList<>();
        formGroupList.forEach(it -> {
            FormGroupTreeVO formGroupTreeVO = new FormGroupTreeVO();
            formGroupTreeVO.setId(Long.valueOf(it.getId()));
            formGroupTreeVO.setName(it.getName());
            formGroupTreeVO.setParentId(Long.valueOf(it.getPId()));
            formGroupTreeVO.setSort(it.getSort());
            formGroupVOList.add(formGroupTreeVO);
        });
        return (List<FormGroupTreeVO>) TreeUtil.build(formGroupVOList);
    }

    @Override
    public FormGroupTrees selectTrees(Integer departmentId, String corpId) {
        FormGroupTrees formGroupTrees = new FormGroupTrees();
        formGroupTrees.setCorpFormGroup(selectTree(FormSourceType.CORP.getCode(), null, corpId));
        formGroupTrees.setDepartmentFormGroup(selectTree(FormSourceType.DEPARTMENT.getCode(), departmentId, corpId));
        formGroupTrees.setSelfFormGroup(selectTree(FormSourceType.PERSONAL.getCode(), null, corpId));
        return formGroupTrees;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveGroup(FormGroupAddDTO addDTO, String corpId) {
        if (addDTO == null) {
            throw new CustomException(TIP_PARAM_MISSING);
        }
        // 校验基本参数
        addDTO.valid(corpId);
        // 校验名称企业唯一
        validUniqueName(null, addDTO.getName(), corpId);

        // 保存分组
        WeFormGroup entity = addDTO.toEntity(corpId);
        // 查询最大的排序值
        entity.setSort(getLastSort(corpId, addDTO.getSourceType(), addDTO.getParentId() == null ? WeConstans.DEFAULT_WE_WORDS_CATEGORY_HIGHEST_SORT : addDTO.getParentId()));
        this.baseMapper.insert(entity);
    }

    /**
     * 获取最大sort值
     *
     * @param corpId     企业id
     * @param sourceType 分组所属类型
     * @param parentId   父id
     * @return max sort
     */
    private Integer getLastSort(String corpId, Integer sourceType, int parentId) {
        LambdaQueryWrapper<WeFormGroup> wrapper = new LambdaQueryWrapper<WeFormGroup>()
                .eq(WeFormGroup::getCorpId, corpId)
                .eq(WeFormGroup::getSourceType, sourceType)
                .eq(WeFormGroup::getPId, parentId)
                .orderByDesc(WeFormGroup::getSort)
                .last(GenConstants.LIMIT_1);
        WeFormGroup formGroup = baseMapper.selectOne(wrapper);
        return formGroup == null ? WeConstans.DEFAULT_WE_WORDS_CATEGORY_HIGHEST_SORT : formGroup.getSort() + 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGroup(Integer id, String corpId) {
        // 校验参数
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (id == null) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_ID_IS_NOT_NULL);
        }

        // 2.查询是否存在
        WeFormGroup weFormGroup = this.baseMapper.selectById(id);
        if (weFormGroup == null) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_NOT_EXIST);
        }

        // 3.删除分组
        List<Integer> deleteGroupIdList = this.deleteGroupByIdWithChild(id);

        // 4.逻辑删除表单
        weFormService.deleteFormByGroupId(deleteGroupIdList, corpId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateGroup(FormGroupUpdateDTO updateDTO, String corpId) {
        updateDTO.valid(corpId);
        // 校验名称企业唯一
        validUniqueName(updateDTO.getId(), updateDTO.getName(), corpId);

        // 更新分组
        this.updateById(updateDTO.toEntity());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeSort(ChangeFormGroupSortDTO sortDTO, String corpId) {
        // 校验
        if (sortDTO == null || StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(sortDTO.getSortList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_ERROR);
        }

        // 校验是否同级别同类型的分组，不然不允许修改
        validSameGroupLevel(sortDTO.getSortList(), corpId);

        // 修改排序
        this.baseMapper.changeSort(corpId, sortDTO.getSortList());
    }

    @Override
    public List<Integer> listChildGroupIdList(Integer groupId, String corpId) {
        if (groupId == null) {
            return new ArrayList<>();
        }
        return this.baseMapper.selectChildGroupIdList(groupId, corpId);
    }

    @Override
    public boolean initCorpFormDefaultGroup(String corpId) {
        if (com.easyink.common.utils.StringUtils.isBlank(corpId)) {
            return false;
        }
        WeFormGroup formGroup = new WeFormGroup();
        formGroup.setCorpId(corpId);
        formGroup.setName(FormConstants.DEFAULT_CORP_FORM_GROUP_NAME);
        formGroup.setSourceType(FormSourceType.CORP.getCode());
        try {
            this.save(formGroup);
            return true;
        } catch (Exception e) {
            log.warn("初始化企业默认表单分组异常: corpId:{}, e: {}", corpId, ExceptionUtils.getMessage(e));
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Integer> deleteGroupByIdWithChild(Integer id) {
        // 1.查询分组以及子分组的Id列表
        List<Integer> groupIdList = this.baseMapper.selectGroupIdListContainChildById(id);

        // 2. 删除分组数据
        this.baseMapper.deleteBatchByIdList(groupIdList);

        return groupIdList;
    }

    /**
     * 校验是否同level的分组
     *
     * @param sortList 排序列表
     * @param corpId   企业id
     */
    private void validSameGroupLevel(List<ChangeSortModel> sortList, String corpId) {
        List<Long> idList = sortList.stream().map(ChangeSortModel::getId).collect(Collectors.toList());
        List<WeFormGroup> weFormGroupList = this.baseMapper.selectList(new LambdaQueryWrapper<WeFormGroup>()
                .in(WeFormGroup::getId, idList)
                .eq(WeFormGroup::getCorpId, corpId)
        );
        Integer pId = null;
        Integer sourceType = null;
        for (WeFormGroup group : weFormGroupList) {
            if (pId == null) {
                pId = group.getPId();
            }
            if (sourceType == null) {
                sourceType = group.getSourceType();
            }
            boolean pidUnSameFlag = !pId.equals(group.getPId());
            boolean sourceTypeUnSameFlag = !sourceType.equals(group.getSourceType());

            if (pidUnSameFlag || sourceTypeUnSameFlag) {
                throw new CustomException(ResultTip.TIP_GROUP_FORM_CHANGE_SORT_ERROR);
            }
        }
    }

    /**
     * 校验分组名称,不关心类型, 企业下唯一即可
     *
     * @param id
     * @param name   分组名称
     * @param corpId 企业id
     */
    private void validUniqueName(Integer id, String name, String corpId) {
        if (StringUtils.isAnyBlank(corpId)) {
            throw new CustomException(TIP_PARAM_MISSING);
        }
        if (StringUtils.isBlank(name)) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_NAME_IS_NOT_BLANK);
        }

        Integer countNum = this.baseMapper.countSameNameNum(name, corpId);

        // id不等于null,表示更新,需要判断原name和更新的name是否一致,如果一致,则减少count值(不包含当前分组名称的计数)
        if (id != null) {
            WeFormGroup dbGroup = this.baseMapper.selectById(id);
            // 要更新的名称与原数据相同,减掉自己 countNum--
            if (dbGroup.getName().equals(name)) {
                countNum--;
            }
        }

        int uniqueNum = 0;
        if (countNum > uniqueNum) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_NAME_DUPLICATE);
        }
    }
}

