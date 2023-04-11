package com.easyink.wecom.mapper;

import com.easyink.wecom.domain.WeTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 流失提醒Mapper接口
 *
 * @author lichaoyu
 * @date 2023/3/24 11:50
 */
@Repository
public interface WeLossTagMapper {

    /**
     * 添加流失标签
     * @param corpId 企业ID
     * @param lossTagIdList 流失标签ID列表
     * @return 结果
     */
    Integer insertWeLossTag(@Param("corpId") String corpId, @Param("lossTagIdList") List<String> lossTagIdList);

    /**
     * 删除原有的流失标签
     * @param corpId 企业ID
     * @return 结果
     */
    Integer deleteWeLossTag(@Param("corpId") String corpId);

    /**
     * 查询流失标签
     * @param corpId 企业ID
     * @return 结果
     */
    List<WeTag> selectLossWeTag(@Param("corpId") String corpId);
}
