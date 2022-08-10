package com.easyink.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.common.shorturl.SysShortUrlMapping;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 类名: 短链映射持久层接口
 *
 * @author : silver_chariot
 * @date : 2022/7/18 16:49
 **/
@Mapper
@Repository
public interface SysShortUrlMappingMapper extends BaseMapper<SysShortUrlMapping> {

}
