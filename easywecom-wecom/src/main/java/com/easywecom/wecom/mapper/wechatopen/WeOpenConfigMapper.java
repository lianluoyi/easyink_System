package com.easywecom.wecom.mapper.wechatopen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.wechatopen.WeOpenConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 类名: 微信公众平台设置持久层接口
 *
 * @author : silver_chariot
 * @date : 2022/7/25 14:07
 **/
@Repository
@Mapper
public interface WeOpenConfigMapper extends BaseMapper<WeOpenConfig> {
    /**
     * 更新或者修改
     *
     * @param config {@link WeOpenConfig}
     */
    void insertOrUpdate(WeOpenConfig config);
}
