package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeMsgTlpMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 欢迎语素材Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
public interface WeMsgTlpMaterialMapper extends BaseMapper<WeMsgTlpMaterial> {

    /**
     * 根据默认欢迎语id获取素材列表
     *
     * @param defaultMsgId 默认欢迎语id
     * @return
     */
    List<WeMsgTlpMaterial> selectWeMsgTlpMaterialListByDefaultMsgId(@Param("defaultMsgId") Long defaultMsgId);

    /**
     * 根据特殊欢迎语id获取素材列表
     *
     * @param specialMsgId
     * @return
     */
    List<WeMsgTlpMaterial> selectWeMsgTlpMaterialListBySpecialMsgId(@Param("specialMsgId") Long specialMsgId);


}
