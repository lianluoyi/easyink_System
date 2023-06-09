package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.vo.sop.SopAttachmentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名： WeWordsDetailMapper
 *
 * @author 佚名
 * @date 2021/10/27 16:02
 */
@Repository
public interface WeWordsDetailMapper extends BaseMapper<WeWordsDetailEntity> {
    /**
     * 批量插入或更新话术库附件
     *
     * @param list 附件
     * @return 受影响行
     */
    int batchInsertOrUpdate(List<WeWordsDetailEntity> list);

    /**
     * 删除附件根据主表id列表
     *
     * @param groupIds 主表id
     * @param corpId   企业id
     * @return 受影响行
     */
    int deleteByGroupIds(@Param("groupIds") List<Long> groupIds, @Param("corpId") String corpId);

    /**
     * 查询附件
     *
     * @param corpId  企业id
     * @param groupId 话术主体id
     * @return {@link List<WeWordsDetailEntity>}
     */
    List<WeWordsDetailEntity> listOfGroupId(@Param("corpId") String corpId, @Param("groupId") Integer groupId, @Param("seq") String seq);

    /**
     * 通过id查询sop附件
     *
     * @param id id
     * @return {@link List<SopAttachmentVO>}
     */
    List<SopAttachmentVO> listOfRuleId(@Param("id") Long id);
}
