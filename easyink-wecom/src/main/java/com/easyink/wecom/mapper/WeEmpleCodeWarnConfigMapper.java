package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeEmpleCodeWarnConfig;
import com.easyink.wecom.domain.vo.WeEmpleCodeWarnConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 获客链接告警设置Mapper接口
 *
 * @author lichaoyu
 * @date 2023/8/23 9:38
 */
@Repository
@Mapper
public interface WeEmpleCodeWarnConfigMapper extends BaseMapper<WeEmpleCodeWarnConfig> {


    /**
     * 更新或插入获客链接告警配置信息
     *
     * @param weEmpleCodeWarnConfig {@link WeEmpleCodeWarnConfig}
     * @return 结果
     */
    Integer saveOrUpdateConfig(WeEmpleCodeWarnConfig weEmpleCodeWarnConfig);

    /**
     * 获取获客链接告警配置信息
     *
     * @param corpId 企业ID
     * @return {@link WeEmpleCodeWarnConfig}
     */
    WeEmpleCodeWarnConfigVO getConfig(@Param("corpId") String corpId);
}
