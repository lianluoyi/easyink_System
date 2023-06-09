package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupTagRel;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名：WeGroupTagRelMapper
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:58
 */
@Repository
public interface WeGroupTagRelMapper extends BaseMapper<WeGroupTagRel> {

    /**
     * 批量打标签
     *
     * @param list list
     * @return int
     */
    int batchAddTag(@Param("list") List<WeGroupTagRel> list);

    /**
     * 批量移除标签
     *
     * @param corpId     企业ID
     * @param chatIdList 客户群列表
     * @param tagIdList  标签列表
     * @return int
     */
    int batchDelTag(@Param("corpId") String corpId, @Param("chatIdList") List<String> chatIdList, @Param("tagIdList") List<Long> tagIdList);

    /**
     * 根据tagIdList批量删除关联关系
     *
     * @param corpId    企业ID
     * @param tagIdList 标签列表
     * @return int
     */
    int delByTagIdList(@Param("corpId") String corpId, @Param("tagIdList") List<Long> tagIdList);

    /**
     * 查询chatIdList下的关联标签
     *
     * @param corpId     企业ID
     * @param chatIdList 客户群列表
     * @return List<WeGroupTagRelVO>
     */
    List<WeGroupTagRelVO> getByChatIdList(@Param("corpId") String corpId, @Param("chatIdList") List<String> chatIdList);

    /**
     * 查询符合条件的标签id列表
     *
     * @param corpId   企业ID
     * @param weGroupList 客户群列表
     * @return List<WeGroupTagRelDetail>
     */
    List<WeGroupTagRelDetail> getTagIdList(@Param("corpId") String corpId, @Param("weGroupList") List<WeGroup> weGroupList);

}
