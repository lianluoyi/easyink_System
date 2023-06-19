package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsChartVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsVO;

import java.util.List;

/**
 * 企业微信标签Service接口
 *
 * @author admin
 * @date 2020-09-07
 */
public interface WeTagService extends IService<WeTag> {

    /**
     * 批量修改客户数据
     *
     * @param list
     */
    void updatWeTagsById(List<WeTag> list);

    /**
     * 保存或修改企业微信标签
     *
     * @param list 标签列表
     */
    void saveOrUpadteWeTag(List<WeTag> list);

    /**
     * 删除企业微信标签信息
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    int deleteWeTagById(String id, String corpId);

    /**
     * 根据标签id删除标签
     *
     * @param groupIds 标签id
     * @return
     */
    int deleteWeTagByGroupId(String[] groupIds);

    /**
     * 创建标签
     *
     * @param tagId  标签ID
     * @param corpId 企业ID
     */
    void creatTag(String tagId, String corpId);

    /**
     * 感觉标签id删除标签
     *
     * @param tagId 标签id
     */
    void deleteTag(String tagId, String corpId);

    /**
     * 根据标签id修改标签
     *
     * @param tagId 标签id
     */
    void updateTag(String tagId, String corpId);

    /**
     * 根据标签ids查询标签详情
     *
     * @param collect 标签id列表
     * @return 标签基础详情列表
     */
    List<TagInfoVO> selectTagByIds(List<String> collect);


    /**
     * 导出标签统计-客户标签
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果
     */
    AjaxResult exportCustomerTagsView(WeTagStatisticsDTO dto);

    /**
     * 获取标签统计-客户标签-表格视图-分页数据
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果
     */
    TableDataInfo<WeTagCustomerStatisticsVO> selectTagStatistics(WeTagStatisticsDTO dto);

    /**
     * 获取标签统计-客户标签-表格视图
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsVO}
     */
    List<WeTagCustomerStatisticsVO> getCustomerTagTableView(WeTagStatisticsDTO dto);

    /**
     * 获取标签统计-客户标签-图表视图-分页数据
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsChartVO}
     */
    TableDataInfo<WeTagCustomerStatisticsChartVO> getCustomerTagTableChartView(WeTagStatisticsDTO dto);

    /**
     * 根据标签获取打上的客户
     *
     * @param corpId 企业id
     * @param tagIds tagids 用,隔开
     * @return {@link Long}
     */
    List<Long> getCustomerByTags(String corpId, String tagIds);
    /**
     * 打标签
     *
     * @param corpId 企业ID
     * @param rel    客户关系实体 {@link WeFlowerCustomerRel}
     * @param tagIds 需要打上的标签
     */
    void addTag(String corpId, WeFlowerCustomerRel rel, List<String> tagIds);

    /**
     * 根据标签ID列表获取标签名
     *
     * @param corpId 企业ID
     * @param tagIds 标签ID列表
     * @return 标签名列表
     */
    List<String> getTagNameByIds(String corpId, List<String> tagIds);
}
