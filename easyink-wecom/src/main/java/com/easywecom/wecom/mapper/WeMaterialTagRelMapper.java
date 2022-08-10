package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMaterialTagRelEntity;
import com.easywecom.wecom.domain.vo.WeMaterialAndTagRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名： 素材标签关联持久层
 *
 * @author 佚名
 * @date 2021/10/11 19:22
 */
@Repository
@Validated
public interface WeMaterialTagRelMapper extends BaseMapper<WeMaterialTagRelEntity> {
    /**
     * 批量插入
     *
     * @param weMaterialTagRelEntities 关系实体列表
     * @return 受影响行
     */
    int batchInsert(@Param("list") List<WeMaterialTagRelEntity> weMaterialTagRelEntities);

    /**
     * 通过素材id列表批量删除
     *
     * @param materialIds 素材id列表(不能为空)
     * @param corpId      企业id
     * @return 受影响行
     */
    int deleteBatchByMaterialId(@NotEmpty @Param("materialIds") List<Long> materialIds, @Param("corpId") String corpId);

    /**
     * 删除标签联系
     *
     * @param weMaterialTagRelEntities 标签关联实体列表
     * @return 受影响行
     */
    int deleteBatchByEntities(@NotEmpty @Param("list") List<WeMaterialTagRelEntity> weMaterialTagRelEntities);

    /**
     * 查询素材和标签关系关联
     *
     * @param materialId materialId
     * @return List<WeMaterialAndTagRel>
     */
    List<WeMaterialAndTagRel> listOfTagRelByMaterialId(@Param("materialId") Long materialId, @Param("tagIds") String tagIds);


}
