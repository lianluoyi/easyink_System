package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeWordsGroupEntity;
import com.easyink.wecom.domain.dto.WeWordsChangeSortDTO;
import com.easyink.wecom.domain.dto.WeWordsQueryDTO;
import com.easyink.wecom.domain.vo.WeWordsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： WeWordsGroupMapper
 *
 * @author 佚名
 * @date 2021/10/25 17:45
 */
@Repository
@Validated
public interface WeWordsGroupMapper extends BaseMapper<WeWordsGroupEntity> {
    /**
     * 插入
     *
     * @param weWordsGroupEntity 实体
     * @return 受影响行
     */
    @Override
    int insert(WeWordsGroupEntity weWordsGroupEntity);

    /**
     * 查询id 根据目录id列表
     *
     * @param categoryIds 目录id
     * @param corpId      企业id
     * @return 话术id列表
     */
    List<Long> listOfCategoryId(@NotEmpty @Param("categoryIds") List<Long> categoryIds, @NotBlank @Param("corpId") String corpId);

    /**
     * 查询话术库
     *
     * @param weWordsQueryDTO 话术dto
     * @return {@link List<WeWordsVO>}
     */
    List<WeWordsVO> listOfWords(WeWordsQueryDTO weWordsQueryDTO);

    /**
     * 根据id查询话术库（按最近使用话术排序）
     *
     * @param corpId 企业id
     * @param ids    话术id
     * @return {@link List<WeWordsVO>}
     */
    List<WeWordsVO> listOfWordsById(@Param("corpId") String corpId, @NotEmpty @Param("list") List<Long> ids);

    /**
     * 批量修改话术文件夹
     *
     * @param categoryId 文件夹id
     * @param ids        话术id
     * @param corpId     企业id
     * @return 受影响行
     */
    int updateCategory(@NotNull @Param("categoryId") Long categoryId, @NotEmpty @Param("ids") List<Long> ids, @Param("corpId") String corpId);

    /**
     * 修改
     *
     * @param weWordsGroupEntity 话术主体
     * @return 修改行
     */
    int update(WeWordsGroupEntity weWordsGroupEntity);

    /**
     * 批量插入或更新话术库附件
     *
     * @param list 附件
     * @return 受影响行
     */
    int batchUpdateSeq(@Param("list") List<WeWordsGroupEntity> list, @Param("corpId") String corpId);
    /**
     * 批量插入或更新话术库附件
     *
     * @param list 附件
     * @return 受影响行
     */
    int batchInsert(List<WeWordsGroupEntity> list);
    /**
     * 批量删除
     *
     * @param ids    id
     * @param corpId 企业id
     */
    void deleteBatchIds(@Param("ids") List<Long> ids, @Param("corpId") String corpId);

    /**
     * 文件夹上移/下移/置顶
     *
     * @param corpId                 企业id
     * @param wordsChangeSortDTOList id及排序号
     * @return int 受影响行
     */
    int changeSort(@Param("corpId") String corpId, @Param("wordsChangeSortDTOList") List<WeWordsChangeSortDTO> wordsChangeSortDTOList);
}
