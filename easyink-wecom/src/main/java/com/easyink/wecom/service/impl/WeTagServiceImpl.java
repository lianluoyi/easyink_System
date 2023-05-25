package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.exception.BaseException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.client.WeCropTagClient;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagDTO;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagListDTO;
import com.easyink.wecom.domain.dto.tag.WeCropTagDTO;
import com.easyink.wecom.domain.dto.tag.WeFindCropTagParam;
import com.easyink.wecom.domain.enums.statistics.StatisticsEnum;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsChartVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsVO;
import com.easyink.wecom.mapper.WeTagGroupMapper;
import com.easyink.wecom.mapper.WeTagMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeTagService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业微信标签Service业务层处理
 *
 * @author admin
 * @date 2020-09-07
 */
@Slf4j
@Service
public class WeTagServiceImpl extends ServiceImpl<WeTagMapper, WeTag> implements WeTagService {

    @Autowired
    private WeTagMapper weTagMapper;
    @Autowired
    private WeCropTagClient weCropTagClient;

    private final WeUserMapper weUserMapper;

    private final WeTagGroupMapper weTagGroupMapper;

    /**
     * 默认按照客户数量排序
     */
    private final static String DEFAULT_CUSTOMER_CNT_SORT = "customerCntSort";

    /**
     * 导出标签统计-客户标签-报表名称
     */
    protected static final String CUSTOMER_TAG_REPORT_FORMS = "客户标签统计报表";

    /**
     * 图表模式下默认最大的标签信息数
     */
    private static final Integer DEFAULT_TAG_LIST_NUM = 10;

    /**
     * 图表模式下默认的标签信息数列表初始位
     */
    private static final Integer DEFAULT_TAG_LIST_START = 0;

    @Autowired
    public WeTagServiceImpl(WeUserMapper weUserMapper, WeTagGroupMapper weTagGroupMapper) {
        this.weUserMapper = weUserMapper;
        this.weTagGroupMapper = weTagGroupMapper;
    }

    /**
     * 批量修改客户数据
     *
     * @param list
     */
    @Override
    @Transactional
    public void updatWeTagsById(List<WeTag> list) {
        this.updateBatchById(list);
    }

    /**
     * 保存或修改客户标签
     *
     * @param list
     */
    @Override
    @Transactional
    public void saveOrUpadteWeTag(List<WeTag> list) {
        this.saveOrUpdateBatch(list);
    }

    /**
     * 删除企业微信标签信息
     *
     * @param id 企业微信标签ID
     * @return 结果
     */
    @Override
    public int deleteWeTagById(String id, String corpId) {
        return weTagMapper.deleteWeTagById(id, corpId);
    }

    /**
     * 删除企业微信标签信息
     *
     * @param groupIds 企业微信标签ID
     * @return 结果
     */
    @Override
    public int deleteWeTagByGroupId(String[] groupIds) {
        return weTagMapper.deleteWeTagByGroupId(groupIds);
    }

    @Override
    public void creatTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("创建标签失败，企业id不能为空");
            throw new BaseException("创建标签失败");
        }
        Long num = 1000L;
        if (StringUtils.isNotEmpty(tagId)) {
            //使用企微新接口
            WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(WeFindCropTagParam.builder().tag_id(tagId.split(",")).build(), corpId);
            List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
            if (CollUtil.isNotEmpty(tagGroups)) {
                tagGroups.stream().forEach(k -> {
                    List<WeCropTagDTO> tag = k.getTag();
                    if (CollUtil.isNotEmpty(tag)) {
                        List<WeTag> weTags = new ArrayList<>();
                        tag.stream().forEach(v -> {
                            WeTag tagInfo = this.getOne(new LambdaQueryWrapper<WeTag>()
                                    .eq(WeTag::getGroupId, k.getGroup_id())
                                    .eq(WeTag::getTagId, v.getId()).last(GenConstants.LIMIT_1));
                            if (tagInfo == null) {
                                WeTag weTag = new WeTag();
                                weTag.setTagId(v.getId());
                                weTag.setGroupId(k.getGroup_id());
                                weTag.setName(v.getName());
                                weTag.setCorpId(corpId);
                                weTag.setCreateTime(new Date(v.getCreate_time() * num));
                                weTags.add(weTag);
                            }
                        });
                        if (CollUtil.isNotEmpty(weTags)) {
                            this.saveOrUpdateBatch(weTags);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void deleteTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(tagId, corpId)) {
            log.error("删除标签失败，tagId：{}，corpId：{}", tagId, corpId);
        }
        this.deleteWeTagById(tagId, corpId);
    }

    @Override
    public void updateTag(String tagId, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            log.error("创建标签失败，企业id不能为空");
            throw new BaseException("创建标签失败");
        }
        if (StringUtils.isNotEmpty(tagId)) {
            //使用企微新接口
            WeCropGroupTagListDTO weCropGroupTagListDTO = weCropTagClient.getCorpTagListByTagIds(WeFindCropTagParam.builder().tag_id(tagId.split(",")).build(), corpId);
            List<WeCropGroupTagDTO> tagGroups = weCropGroupTagListDTO.getTag_group();
            if (CollUtil.isNotEmpty(tagGroups)) {
                tagGroups.stream().forEach(k -> {
                    List<WeCropTagDTO> tag = k.getTag();
                    if (CollUtil.isNotEmpty(tag)) {
                        List<WeTag> weTags = new ArrayList<>();
                        tag.stream().forEach(v -> {
                            WeTag weTag = new WeTag();
                            weTag.setTagId(v.getId());
                            weTag.setGroupId(k.getGroup_id());
                            weTag.setName(v.getName());
                            weTag.setCorpId(corpId);
                            weTag.setCreateTime(new Date(v.getCreate_time() * 1000L));
                            weTags.add(weTag);
                        });
                        if (CollUtil.isNotEmpty(weTags)) {
                            this.saveOrUpdateBatch(weTags);
                        }
                    }
                });
            }
        }
    }

    @Override
    public List<TagInfoVO> selectTagByIds(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<WeTag> weTags = weTagMapper.selectList(new LambdaQueryWrapper<WeTag>()
                .select(WeTag::getTagId, WeTag::getName)
                .in(WeTag::getTagId, idList)
        );

        return weTags.stream().map(it -> new TagInfoVO(it.getTagId(), it.getName())).collect(Collectors.toList());
    }

    /**
     * 导出标签统计-客户标签
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果
     */
    @Override
    @DataScope
    public AjaxResult exportCustomerTagsView(WeTagStatisticsDTO dto) {
        List<WeTagCustomerStatisticsVO> list = getCustomerTagTableView(dto);
        // 处理返回的数据
        handleVOData(list, dto);
        // 导出
        ExcelUtil<WeTagCustomerStatisticsVO> util = new ExcelUtil<>(WeTagCustomerStatisticsVO.class);
        return util.exportExcel(list, CUSTOMER_TAG_REPORT_FORMS);
    }




    /**
     * 获取标签统计-客户标签-表格视图-分页数据
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果
     */
    @Override
    @DataScope
    public TableDataInfo<WeTagCustomerStatisticsVO> selectTagStatistics(WeTagStatisticsDTO dto) {
        // 要返回的数据
        List<WeTagCustomerStatisticsVO> resultList = getCustomerTagTableView(dto);
        // 分页数据
        PageInfo<WeTagCustomerStatisticsVO> pageInfo = PageInfoUtil.list2PageInfo(resultList, dto.getPageNum(), dto.getPageSize());
        // 当前页数据
        List<WeTagCustomerStatisticsVO> currentList = pageInfo.getList();
        // 处理返回的数据
        handleVOData(currentList, dto);
        return PageInfoUtil.getDataTable(resultList, dto.getPageNum(), dto.getPageSize());
    }

    /**
     * 处理返回的数据
     *
     * @param resultList 要返回的数据
     * @param dto {@link WeTagStatisticsDTO}
     */
    public void handleVOData(List<WeTagCustomerStatisticsVO> resultList, WeTagStatisticsDTO dto) {
        if (CollectionUtils.isEmpty(resultList) || dto == null) {
            return;
        }
        // 数据范围下的标签ID列表
        List<String> tagIdList = resultList.stream().map(item -> item.getTagId()).collect(Collectors.toList());
        // 获取数据范围下的标签组和标签信息
        List<WeTagCustomerStatisticsVO> currentGroupTagList = weTagMapper.selectTagStatistics(dto, tagIdList);
        // 设置数据范围下的标签组名称和标签组ID
        for (WeTagCustomerStatisticsVO weTagCustomerStatisticsVO : resultList) {
            for (WeTagCustomerStatisticsVO tagCustomerStatisticsVO : currentGroupTagList) {
                if (weTagCustomerStatisticsVO.getTagId().equals(tagCustomerStatisticsVO.getTagId())) {
                    weTagCustomerStatisticsVO.setTagGroupId(tagCustomerStatisticsVO.getTagGroupId());
                    weTagCustomerStatisticsVO.setGroupTagName(tagCustomerStatisticsVO.getGroupTagName());
                }
            }
        }
    }

    /**
     * 获取标签统计-客户标签-表格视图
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsVO}
     */
    @Override
    @DataScope
    public List<WeTagCustomerStatisticsVO> getCustomerTagTableView(WeTagStatisticsDTO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getCorpId()) || !checkDepartmentAndUpdateUserIds(dto)) {
            return new ArrayList<>();
        }
        // 获取选中员工所属的客户-员工关系id列表
        setFlowerCustomerRelIdList(dto);
        if (CollectionUtils.isEmpty(dto.getFlowerCustomerRelIdList())) {
            return new ArrayList<>();
        }
        // 获取有效的标签下客户信息，去重
        List<WeTag> weTagList = weTagMapper.getWeTagList(dto);
        if (CollectionUtils.isEmpty(weTagList)) {
            return new ArrayList<>();
        }
        // 根据查询出来的标签信息，去重
        List<WeTagCustomerStatisticsVO> resultList = weTagList.stream().distinct().map(weTag -> {
            WeTagCustomerStatisticsVO weTagCustomerStatisticsVO = new WeTagCustomerStatisticsVO();
            weTagCustomerStatisticsVO.setTagId(weTag.getTagId());
            weTagCustomerStatisticsVO.setTagName(weTag.getName());
            weTagCustomerStatisticsVO.setCreateTime(DateUtils.getDateTime(weTag.getCreateTime()));
            return weTagCustomerStatisticsVO;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        // 统计每个标签对应的客户数量，为对应的标签设置客户数量
        for (WeTagCustomerStatisticsVO weTagData : resultList) {
            for (WeTag item : weTagList) {
                if (item.getTagId().equals(weTagData.getTagId())) {
                    weTagData.addCustomerCnt();
                }
            }
        }
        // 去除标签下客户数为0的标签
        Iterator<WeTagCustomerStatisticsVO> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            WeTagCustomerStatisticsVO weTagData = iterator.next();
            if (weTagData != null && weTagData.getCustomerCnt() == 0) {
                iterator.remove();
            }
        }
        // 排序
        StatisticsEnum.CustomerTagSortEnum.sort(dto, resultList);
        return resultList;
    }

    /**
     * 设置员工对应的客户关系ID列表
     *
     * @param dto {@link WeTagStatisticsDTO}
     */
    public void setFlowerCustomerRelIdList(WeTagStatisticsDTO dto) {
        if (dto == null || CollectionUtils.isNotEmpty(dto.getFlowerCustomerRelIdList())) {
            return;
        }
        List<String> flowerCustomerRelIdList = weUserMapper.selectFlowerCustomerRelIdList(dto);
        if (CollectionUtils.isNotEmpty(flowerCustomerRelIdList)) {
            dto.setFlowerCustomerRelIdList(flowerCustomerRelIdList);
        }
    }

    /**
     * 获取标签统计-客户标签-图表视图-分页数据
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果
     */
    @Override
    @DataScope
    public TableDataInfo<WeTagCustomerStatisticsChartVO> selectTagStatisticsByChart(WeTagStatisticsDTO dto) {
        List<WeTagCustomerStatisticsChartVO> resultList = getCustomerTagTableChartView(dto);
        if (dto.getPageNum() == null || dto.getPageSize() == null) {
            return PageInfoUtil.getDataTable(resultList);
        }
        return PageInfoUtil.getDataTable(resultList, dto.getPageNum(), dto.getPageSize());
    }

    /**
     * 获取标签统计-客户标签-图表视图
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsChartVO}
     */
    @Override
    @DataScope
    public List<WeTagCustomerStatisticsChartVO> getCustomerTagTableChartView(WeTagStatisticsDTO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getCorpId()) || !checkDepartmentAndUpdateUserIds(dto)) {
            return new ArrayList<>();
        }
        // 查出所有的标签组列表
        WeTagGroup weTagGroup = new WeTagGroup();
        weTagGroup.setCorpId(dto.getCorpId());
        List<WeTagGroup> weTagGroupList = weTagGroupMapper.selectWeTagGroupList(weTagGroup);
        // 标签组信息结果集
        List<WeTagCustomerStatisticsChartVO> resultList = new ArrayList<>();
        // 所有的标签组和标签信息
        List<WeTagCustomerStatisticsVO> weTagList = getCustomerTagTableView(dto);
        if (CollectionUtils.isEmpty(weTagList)) {
            return new ArrayList<>();
        }
        // 处理返回的数据
        handleVOData(weTagList, dto);
        // 获取有效的标签下客户信息，去重
        List<WeTagCustomerStatisticsVO> customerTagList = weTagMapper.getCustomerTagCnt(dto);
        if (CollectionUtils.isEmpty(customerTagList)) {
            return new ArrayList<>();
        }
        // 单个标签组下的所有标签和客户信息
        List<WeTagCustomerStatisticsVO> singleGroupTagList;
        // 将标签组对应的标签合并到一起
        for (WeTagGroup tagGroup : weTagGroupList) {
            // 过滤不存在的标签组
            singleGroupTagList = weTagList.stream().filter(WeTagStatisticsVO -> WeTagStatisticsVO.getTagGroupId().equals(tagGroup.getGroupId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(singleGroupTagList)) {
                // 组装标签组信息和标签组内的标签信息
                resultList.add(new WeTagCustomerStatisticsChartVO(tagGroup, singleGroupTagList, customerTagList));
            }
        }
        // 默认按标签组时间正序排序
        resultList.sort(Comparator.comparing(WeTagCustomerStatisticsChartVO::getGroupTagCreateTime));
        // 超过十条标签数据，只返回前十条数据
        resultList.forEach(item -> {
            if (item.getGroupTagList().size() > DEFAULT_TAG_LIST_NUM) {
                item.setGroupTagList(item.getGroupTagList().subList(DEFAULT_TAG_LIST_START, DEFAULT_TAG_LIST_NUM));
            }
        });
        return resultList;
    }

    /**
     * 是否是部门查询
     * 如果只选定部门不选择员工 但是部门下没有员工则直接返回空列表
     *
     * @param dto   {@link WeTagStatisticsDTO}
     * @return true 继续查询 false:停止查询返回空list
     */
    public boolean checkDepartmentAndUpdateUserIds(WeTagStatisticsDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getDepartmentIds())) {
            return true;
        }
        // 部门idList查询
        List<String> userIdsByDepartmentIds = weUserMapper.listOfUserId(dto.getCorpId(), dto.getDepartmentIds().toArray(new String[]{}));
        if (CollectionUtils.isEmpty(userIdsByDepartmentIds) && CollectionUtils.isEmpty(dto.getUserIds())) {
            return false;
        }
        if (dto.getUserIds() == null) {
            dto.setUserIds(userIdsByDepartmentIds);
        }else {
            dto.getUserIds().addAll(userIdsByDepartmentIds);
        }
        return true;
    }
}
