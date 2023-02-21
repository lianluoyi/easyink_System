package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.form.ChangeFormGroupSortDTO;
import com.easyink.wecom.domain.dto.form.FormGroupAddDTO;
import com.easyink.wecom.domain.dto.form.FormGroupUpdateDTO;
import com.easyink.wecom.domain.entity.form.WeFormGroup;
import com.easyink.wecom.domain.enums.form.FormSourceType;
import com.easyink.wecom.domain.vo.form.FormGroupTreeVO;

import java.util.List;

/**
 * 表单分组表(WeFormGroup)表服务接口
 *
 * @author tigger
 * @since 2023-01-09 11:23:22
 */
public interface WeFormGroupService extends IService<WeFormGroup> {

    /**
     * 查询分组列表树
     *
     * @param sourceType   分组所属类别(1:企业 2: 部门 3:个人) {@link FormSourceType}
     * @param departmentId
     * @param corpId       企业id
     * @return
     */
    List<FormGroupTreeVO> selectTree(Integer sourceType, Integer departmentId, String corpId);

    /**
     * 保存表单分组
     *
     * @param addDTO 分组dto
     * @param corpId 企业id
     */
    void saveGroup(FormGroupAddDTO addDTO, String corpId);

    /**
     * 删除表单分组
     *
     * @param id     分组id
     * @param corpId 企业id
     */
    void deleteGroup(Integer id, String corpId);

    /**
     * 修改分组
     *
     * @param updateDTO updateDTO
     * @param corpId    企业id
     */
    void updateGroup(FormGroupUpdateDTO updateDTO, String corpId);

    /**
     * 修改分组排序
     * 注意：
     * 1.只能修改同层级下的排序（属于同一个父分组）
     * 2.只能修改同类别下的排序
     *
     * @param sortDTO 排序DTO
     * @param corpId  企业id
     */
    void changeSort(ChangeFormGroupSortDTO sortDTO, String corpId);

    /**
     * 查询当前分组的子分组列表
     *
     * @param groupId 分组id
     * @param corpId 企业id
     * @return 子分组列表id
     */
    List<Integer> listChildGroupIdList(Integer groupId, String corpId);

    /**
     * 初始化企业类型表单的默认分组
     * @param corpId 企业id
     * @return
     */
    boolean initCorpFormDefaultGroup(String corpId);

    /**
     * 删除分组包含其子分组
     *
     * @param id 分组id
     * @return 删除的分组id列表
     */
    List<Integer> deleteGroupByIdWithChild(Integer id);
}

