package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeSituation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 获客链接-主页获客情况Mapper
 *
 * @author lichaoyu
 * @date 2023/8/24 14:28
 */
@Mapper
@Repository
public interface WeEmpleCodeSituationMapper extends BaseMapper<WeEmpleCodeSituation> {

}
