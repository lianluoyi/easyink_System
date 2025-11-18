package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeThirdPartyPushLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 第三方推送日志Mapper接口
 *
 * @author easyink
 * @date 2024-01-01
 */
@Mapper
public interface WeThirdPartyPushLogMapper extends BaseMapper<WeThirdPartyPushLog> {

}
