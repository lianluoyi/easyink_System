package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.wecom.domain.WeWordsCategory;
import com.easywecom.wecom.domain.dto.wordscategory.*;
import com.easywecom.wecom.domain.vo.FindExistWordsCategoryNameList;
import com.easywecom.wecom.domain.vo.WeWordsCategoryVO;

import java.util.List;

/**
 * 类名：WeWordsCategoryService
 *
 * @author Society my sister Li
 * @date 2021-10-25 10:48
 */
public interface WeWordsCategoryService extends IService<WeWordsCategory> {

    /**
     * 新增话术文件夹
     *
     * @param weWordsCategory weWordsCategory
     * @return int
     */
    void insert(LoginUser loginUser, AddWeWordsCategoryDTO weWordsCategory);

    /**
     * 修改话术文件夹
     *
     * @param updateWeWordsCategoryDTO
     * @return int
     */
    void update(LoginUser loginUser, UpdateWeWordsCategoryDTO updateWeWordsCategoryDTO);

    /**
     * 删除文件夹
     *
     * @param deleteWeWordsCategoryDTO deleteWeWordsCategoryDTO
     * @return int
     */
    void delete(DeleteWeWordsCategoryDTO deleteWeWordsCategoryDTO);

    /**
     * 查询文件夹列表
     *
     * @param findWeWordsCategoryDTO findWeWordsCategoryDTO
     * @return List<WeWordsCategoryVO>
     */
    List<WeWordsCategoryVO> list(FindWeWordsCategoryDTO findWeWordsCategoryDTO);

    /**
     * 根据名称查询文件夹ID,没有的则创建
     *
     * @param corpId   企业ID
     * @param type     文件夹类型
     * @param nameList 文件夹名称列表
     * @param useRange 使用范围
     * @return List<FindExistWordsCategoryNameList>
     */
    List<FindExistWordsCategoryNameList> findAndAddWordsCategory(String corpId, Integer type, List<String> nameList, String useRange);

    /**
     * 文件夹上移/下移/置顶
     *
     * @param weWordsCategoryChangeSortDTO weWordsCategoryChangeSortDTO
     * @return int
     */
    int changeSort(WeWordsCategoryChangeSortDTO weWordsCategoryChangeSortDTO);
}
