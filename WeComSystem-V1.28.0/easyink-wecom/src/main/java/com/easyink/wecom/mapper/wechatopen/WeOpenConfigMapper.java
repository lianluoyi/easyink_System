package com.easyink.wecom.mapper.wechatopen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 获取微信公众号配置
     *
     * @param corpId    企业id
     * @return
     */
    List<WeOpenConfigVO> getConfigList(@Param("corpId") String corpId);
}
