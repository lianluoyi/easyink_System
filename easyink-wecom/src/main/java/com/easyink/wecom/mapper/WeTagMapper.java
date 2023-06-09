package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.WeTagStatistic;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业微信标签Mapper接口
 *
 * @author admin
 * @date 2020-09-07
 */
@Repository
public interface WeTagMapper extends BaseMapper<WeTag> {
    /**
     * 查询企业微信标签
     *
     * @param id 企业微信标签ID
     * @return 企业微信标签
     */
    WeTag selectWeTagById(Long id);

    /**
     * 修改企业微信标签
     *
     * @param weTag 企业微信标签
     * @return 结果
     */
    int updateWeTag(WeTag weTag);

    /**
     * 删除企业微信标签
     *
     * @param groupIds 企业微信标签组Id
     * @return 结果
     */
    int deleteWeTagByGroupId(String[] groupIds);

    /**
     * 删除企业微信标签
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    int deleteWeTagById(@Param("id") String id, @Param("corpId") String corpId);

    /**
     * 批量插入/更新标签
     *
     * @param weTags 标签集合
     */
    Integer batchInsert(@Param("list") List<WeTag> weTags);


    /**
     * 获取所有的标签组和标签组下的标签信息
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsVO}
     */
    List<WeTagCustomerStatisticsVO> selectTagStatistics(@Param("dto") WeTagStatisticsDTO dto, @Param("tagIdList") List<String> tagIdList);

    /**
     * 获取企业下有打的标签（去重）
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 标签信息
     */
    List<WeTag> selectTagIds(WeTagStatisticsDTO dto);

    /**
     * 获取所有标签和客户信息
     *
     * @param dto    {@link WeTagStatisticsDTO}
     * @param tagIds 标签ID列表
     * @return 标签和客户ID
     */
    List<WeTagStatistic> getWeTagList(@Param("dto") WeTagStatisticsDTO dto, @Param("tagIds") List<WeTag> tagIds);

    /**
     * 查询企业下有效的标签总数（去重）
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 总数
     */
    Integer selectCount(WeTagStatisticsDTO dto);

    /**
     * 获取所有标签下的客户数量(去重)
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 标签和客户ID
     */
    List<WeTagCustomerStatisticsVO> getCustomerTagCnt(WeTagStatisticsDTO dto);

    /**
     * 统计当前企业下所有未删除的企业标签的数量
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 标签数量
     */
    Integer totalTagCnt(WeTagStatisticsDTO dto);
}
