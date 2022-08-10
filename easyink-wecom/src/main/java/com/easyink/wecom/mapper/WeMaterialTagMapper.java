package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeMaterialTagEntity;
import com.easyink.wecom.domain.vo.WeMaterialTagVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名： WeMaterialTagMapper
 *
 * @author 佚名
 * @date 2021/10/11 18:41
 */
@Repository
public interface WeMaterialTagMapper extends BaseMapper<WeMaterialTagEntity> {
    /**
     * 根据名称查询标签
     *
     * @param name   名称
     * @param corpId 企业id
     * @return {@link List<WeMaterialTagVO>}
     */
    List<WeMaterialTagVO> listByName(@Param("name") String name, @Param("corpId") String corpId);

    /**
     * 查询素材列表具备的标签
     *
     * @param materialIdList 素材列表
     * @return {@link List<WeMaterialTagVO>}
     */
    List<WeMaterialTagVO> listChecked(@Param("list") List<Long> materialIdList);
}
