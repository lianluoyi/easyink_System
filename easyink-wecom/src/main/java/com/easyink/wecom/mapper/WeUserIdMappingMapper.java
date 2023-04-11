package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.WeUserIdMapping;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工userId明密文映射表(WeUserIdMapping)表数据库访问层
 *
 * @author wx
 * @since 2023-03-14 18:02:22
 */
@Mapper
public interface WeUserIdMappingMapper extends BaseMapper<WeUserIdMapping> {
}

