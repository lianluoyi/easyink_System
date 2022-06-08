package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeWordsCategory;
import com.easywecom.wecom.domain.dto.wordscategory.FindWeWordsCategoryDTO;
import com.easywecom.wecom.domain.dto.wordscategory.UpdateWeWordsCategoryDTO;
import com.easywecom.wecom.domain.dto.wordscategory.WeWordsCategoryChangeSort;
import com.easywecom.wecom.domain.dto.wordscategory.WeWordsCategoryChildSortDTO;
import com.easywecom.wecom.domain.vo.FindExistWordsCategoryNameList;
import com.easywecom.wecom.domain.vo.WeWordsCategoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeWordsCategoryMapper
 *
 * @author 李小琳
 * @date 2020-10-25 10:52
 */
@Repository
public interface WeWordsCategoryMapper extends BaseMapper<WeWordsCategory> {

    /**
     * 根据搜索条件查询话术文件夹列表
     *
     * @param weWordsCategory weWordsCategory
     * @return List<WeWordsCategoryVO>
     */
    List<WeWordsCategoryVO> findByWeWordsCategory(WeWordsCategory weWordsCategory);

    /**
     * 批量插入数据
     *
     * @param list list
     * @return int
     */
    int batchInsert(@Param("list") List<WeWordsCategory> list);

    /**
     * 更新文件夹名称
     *
     * @param updateWeWordsCategoryDTO updateWeWordsCategoryDTO
     * @return int
     */
    int updateByIdAndCorpId(UpdateWeWordsCategoryDTO updateWeWordsCategoryDTO);

    /**
     * 批量更新子文件夹排序和名称
     *
     * @param id          父级文件ID
     * @param corpId      企业ID
     * @param childIdList 子文件夹列表
     * @return int
     */
    int batchUpdateChildSortAndName(@Param("id") Long id, @Param("corpId") String corpId, @Param("childIdList") List<WeWordsCategoryChildSortDTO> childIdList);

    /**
     * 批量删除文件夹
     *
     * @param corpId 企业ID
     * @param idList 文件夹列表
     * @return int
     */
    int deleteByCorpIdIdList(@Param("corpId") String corpId, @Param("idList") List<Long> idList);

    /**
     * 查询列表
     *
     * @param findWeWordsCategoryDTO findWeWordsCategoryDTO
     * @return List<WeWordsCategoryVO>
     */
    List<WeWordsCategoryVO> selectWeWordsCategoryList(FindWeWordsCategoryDTO findWeWordsCategoryDTO);

    /**
     * 查询该类型下是否有重名的文件夹
     *
     * @param corpId   企业ID
     * @param type     文件夹类型
     * @param useRange 使用范围
     * @param nameList 文件夹名称
     * @return List<FindExistWordsCategoryNameList>
     */
    List<FindExistWordsCategoryNameList> existsSameNameWordsCategory(@Param("corpId") String corpId, @Param("type") Integer type, @Param("useRange") String useRange, @Param("nameList") List<String> nameList);

    /**
     * 文件夹修改sort顺序
     *
     * @param corpId 企业ID
     * @param list   id和新的sort
     * @return int
     */
    int changeSort(@Param("corpId") String corpId, @Param("list") List<WeWordsCategoryChangeSort> list);
}
