package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.WeUserIdMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工userId明密文映射表(WeUserIdMapping)表数据库访问层
 *
 * @author wx
 * @since 2023-03-14 18:02:22
 */
@Mapper
public interface WeUserIdMappingMapper extends BaseMapper<WeUserIdMapping> {
    /**
     * 批量更新/插入 userid明文密文映射关系
     *
     * @param mappingList 映射关系表 {@link WeUserIdMapping}
     * @return affected rows
     */
    Integer batchInsertOrUpdate(@Param("list") List<WeUserIdMapping> mappingList);

    /**
     * 批量更新/插入 第三方服务商的 userid明文密文映射关系
     * @param mappingList 映射关系列表
     */
    void batchInsertOrUpdateThirdService(@Param("list") List<WeUserIdMapping> mappingList);
}

