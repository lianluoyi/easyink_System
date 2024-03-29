package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.client.WeCropTagClient;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.customer.CustomerTagEdit;
import com.easyink.wecom.domain.dto.statistics.WeTagStatisticsDTO;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagDTO;
import com.easyink.wecom.domain.dto.tag.WeCropGroupTagListDTO;
import com.easyink.wecom.domain.dto.tag.WeCropTagDTO;
import com.easyink.wecom.domain.dto.tag.WeFindCropTagParam;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsChartVO;
import com.easyink.wecom.domain.vo.statistics.WeTagCustomerStatisticsVO;
import com.easyink.wecom.domain.vo.statistics.WeTagStatisticsBaseVO;
import com.easyink.wecom.mapper.WeTagGroupMapper;
import com.easyink.wecom.mapper.WeTagMapper;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeFlowerCustomerTagRelService;
import com.easyink.wecom.service.WeTagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private final WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;
    private final WeCustomerClient weCustomerClient;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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
    public WeTagServiceImpl(WeUserMapper weUserMapper, WeTagGroupMapper weTagGroupMapper, WeFlowerCustomerTagRelService weFlowerCustomerTagRelService, WeCustomerClient weCustomerClient) {
        this.weUserMapper = weUserMapper;
        this.weTagGroupMapper = weTagGroupMapper;
        this.weFlowerCustomerTagRelService = weFlowerCustomerTagRelService;
        this.weCustomerClient = weCustomerClient;
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
        // 导出不分页
        dto.setPageNum(null);
        dto.setPageSize(null);
        List<WeTagCustomerStatisticsVO> list = getCustomerTagTableView(dto);
        // 补充要返回的分页数据下对应的标签组信息
        suppleTagGroupInfo(list, dto);
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
        // 补充要返回的分页数据下对应的标签组信息
        suppleTagGroupInfo(resultList, dto);
        return PageInfoUtil.getDataTable(resultList, dto.getTotal());
    }

    /**
     * 补充要返回的分页数据下对应的标签组信息
     *
     * @param resultList 要返回的数据
     * @param dto {@link WeTagStatisticsDTO}
     */
    public void suppleTagGroupInfo(List<WeTagCustomerStatisticsVO> resultList, WeTagStatisticsDTO dto) {
        if (CollectionUtils.isEmpty(resultList) || dto == null) {
            return;
        }
        // 数据范围下的标签ID列表
        List<String> tagIdList = resultList.stream().map(WeTagStatisticsBaseVO::getTagId).collect(Collectors.toList());
        // 获取数据范围下的标签组和标签信息
        List<WeTagCustomerStatisticsVO> currentGroupTagList = weTagMapper.selectTagStatistics(dto, tagIdList);
        // 设置数据范围下的标签组名称和标签组ID
        resultList.forEach(statisticsVO -> {
            currentGroupTagList.stream()
                    .filter(groupInfo -> statisticsVO.getTagId().equals(groupInfo.getTagId()))
                    .findFirst()
                    .ifPresent(groupInfo -> {
                        statisticsVO.setTagGroupId(groupInfo.getTagGroupId());
                        statisticsVO.setGroupTagName(groupInfo.getGroupTagName());
                    });
        });
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
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            // 分页
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        // 根据数据权限，获取选中员工对应的标签id，并按照标签创建时间倒序排序，分页
        List<WeTag> weTagInfoList = weTagMapper.selectTagInfoByDataScope(dto);
        // 获取总数
        PageInfo<WeTag> pageInfo = new PageInfo<>(weTagInfoList);
        dto.setTotal(pageInfo.getTotal());
        if (CollectionUtils.isEmpty(weTagInfoList)) {
            return new ArrayList<>();
        }
        // 执行任务列表
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        // 结果列表
        CopyOnWriteArrayList<WeTagCustomerStatisticsVO> resultList = new CopyOnWriteArrayList<>();
        for (WeTag weTag : weTagInfoList) {
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                try {
                    // 获取有效的标签ID列表下所有的客户信息
                    HashSet<WeTagStatistic> weTagList = weTagMapper.getWeTagListByTagId(weTag.getTagId(), dto);
                    if (CollectionUtils.isEmpty(weTagList)) {
                        return;
                    }
                    // 为标签设置对应的客户数量
                    resultList.add(filterAndSetCustomerCnt(new ArrayList<>(weTagList), weTag));
                } catch (Exception e) {
                    log.error("[标签统计-表格视图] 处理标签数据异常，异常原因：{}", ExceptionUtils.getStackTrace(e));
                }
            }, threadPoolTaskExecutor);
            completableFutures.add(voidCompletableFuture);
        }
        // 等待所有的CompletableFuture执行完成
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        // 按照标签创建时间倒序排序
        resultList.sort(Comparator.comparing(WeTagCustomerStatisticsVO::getCreateTime).reversed());
        return resultList;
    }

    /**
     * 获取标签统计-客户标签-图表视图-分页数据
     *
     * @param dto {@link WeTagStatisticsDTO}
     * @return 结果集 {@link WeTagCustomerStatisticsChartVO}
     */
    @Override
    @DataScope
    public TableDataInfo<WeTagCustomerStatisticsChartVO> getCustomerTagTableChartView(WeTagStatisticsDTO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getCorpId()) || !checkDepartmentAndUpdateUserIds(dto)) {
            return PageInfoUtil.emptyData();
        }
        // 根据数据权限，获取选中员工对应的所有标签id，不排序
        List<WeTag> weTagInfoList = weTagMapper.selectTagInfoByDataScope(dto);
        if (CollectionUtils.isEmpty(weTagInfoList)) {
            return PageInfoUtil.emptyData();
        }
        List<String> tagIdList = weTagInfoList.stream().map(WeTag::getTagId).collect(Collectors.toList());
        // 根据有效的标签ID，设置对应的标签组总数
        HashSet<String> tagGroupIdList = weTagInfoList.stream().map(WeTag::getGroupId).collect(Collectors.toCollection(HashSet::new));
        long tagGroupCnt = tagGroupIdList.size();
        // 设置标签ID列表
        dto.setTagIdList(tagIdList);
        // 启动分页
        if (dto.getPageSize() != null && dto.getPageNum() != null) {
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        }
        // 根据标签组ID列表查出按照时间倒序，分页后的标签组信息列表
        List<WeTagGroup> weTagGroupList = weTagGroupMapper.selectWeTagGroupListByStatistic(new ArrayList<>(tagGroupIdList), dto.getCorpId());
        if (CollectionUtils.isEmpty(weTagGroupList)) {
            return PageInfoUtil.emptyData();
        }
        // 获取标签组对应的标签列表
        List<WeTag> tagListByTagGroup = weTagInfoList.stream()
                .filter(tagInfo -> weTagGroupList.stream().anyMatch(weTagGroup -> tagInfo.getGroupId().equals(weTagGroup.getGroupId())))
                .collect(Collectors.toList());
        // 将标签组对应的标签列表进行分组
        Map<String, List<WeTag>> groupTagRelList = tagListByTagGroup.stream().collect(Collectors.groupingBy(WeTag::getGroupId));
        // 执行的任务列表
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        // 结果列表
        CopyOnWriteArrayList<WeTagCustomerStatisticsChartVO> resultList = new CopyOnWriteArrayList<>();
        for (WeTagGroup weTagGroup : weTagGroupList) {
            List<WeTag> weTags = groupTagRelList.get(weTagGroup.getGroupId());
            // 根据分组后的标签组对应的标签信息进行查询
            if (CollectionUtils.isNotEmpty(weTags)) {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    try {
                        // 获取标签列表下客户信息
                        HashSet<WeTagStatistic> weTagList = weTagMapper.getWeTagList(weTags, dto);
                        if (CollectionUtils.isEmpty(weTagList)) {
                            return;
                        }
                        // 为标签设置对应的客户数量
                        List<WeTagCustomerStatisticsVO> allWeTagList = filterAndSetCustomerCnt(new ArrayList<>(weTagList), weTags);
                        if (CollectionUtils.isEmpty(allWeTagList)) {
                            return;
                        }
                        // 补充要返回的分页数据下对应的标签组信息
                        suppleTagGroupInfo(allWeTagList, dto);
                        resultList.add(countTagGroupStatistic(weTagGroup, allWeTagList, new ArrayList<>(weTagList)));
                    } catch (Exception e) {
                        log.error("[标签统计-图表图表视图] 处理标签组统计数据异常，异常原因ex:{}", ExceptionUtils.getStackTrace(e));
                    }
                }, threadPoolTaskExecutor);
                completableFutures.add(voidCompletableFuture);
            }
        }
        // 等待所有的CompletableFuture执行完成
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        // 默认按标签组时间正序排序
        resultList.sort(Comparator.comparing(WeTagCustomerStatisticsChartVO::getGroupTagCreateTime));
        // 标签组下的标签列表，超过十条标签数据，只返回前按照客户数倒序排序的十条数据，其他数据由前端计算，显示为其它
        resultList.forEach(item -> {
            if (item.getGroupTagList().size() > DEFAULT_TAG_LIST_NUM) {
                // 根据标签下客户数倒序排序
                item.getGroupTagList().sort(Comparator.comparing(WeTagCustomerStatisticsVO::getCustomerCnt).reversed());
                item.setGroupTagList(item.getGroupTagList().subList(DEFAULT_TAG_LIST_START, DEFAULT_TAG_LIST_NUM));
            }
        });
        return PageInfoUtil.getDataTable(resultList, tagGroupCnt);
    }

    /**
     * 标签组下的每一个标签对应的客户数统计
     *
     * @param weTagGroup      标签组信息
     * @param tagCustomerRels 标签和对应客户数量的列表
     * @param allCustomerRels 标签组下所有标签列表对应客户信息
     * @return 结果
     */
    public WeTagCustomerStatisticsChartVO countTagGroupStatistic(WeTagGroup weTagGroup, List<WeTagCustomerStatisticsVO> tagCustomerRels, List<WeTagStatistic> allCustomerRels) {
        if (weTagGroup == null || CollectionUtils.isEmpty(tagCustomerRels) || CollectionUtils.isEmpty(allCustomerRels)) {
            return null;
        }
        // 将标签组对应的标签合并到一起
        return new WeTagCustomerStatisticsChartVO(weTagGroup, tagCustomerRels, allCustomerRels);
    }

    /**
     * 根据单个标签信息，设置对应的客户数量
     *
     * @param weTagList 有效的标签ID列表下客户信息
     * @param weTag     标签信息
     * @return 结果
     */
    public WeTagCustomerStatisticsVO filterAndSetCustomerCnt(List<WeTagStatistic> weTagList, WeTag weTag) {
        if (CollectionUtils.isEmpty(weTagList) || weTag == null) {
            return null;
        }
        // 转换查询出来的标签信息
        WeTagCustomerStatisticsVO weTagCustomerStatisticsVO = new WeTagCustomerStatisticsVO();
        weTagCustomerStatisticsVO.setTagId(weTag.getTagId());
        weTagCustomerStatisticsVO.setTagName(weTag.getName());
        weTagCustomerStatisticsVO.setCreateTime(DateUtils.getDateTime(weTag.getCreateTime()));
        weTagCustomerStatisticsVO.setCustomerCnt(weTagList.size());
        return weTagCustomerStatisticsVO;
    }

    /**
     * 根据多个标签信息列表，为标签设置对应的客户数量
     *
     * @param weTagList 有效的标签ID列表下客户信息
     * @param filterWeTags 关系表中企业下有打的标签（去重）
     * @return 结果
     */
    public List<WeTagCustomerStatisticsVO> filterAndSetCustomerCnt(List<WeTagStatistic> weTagList, List<WeTag> filterWeTags) {
        if (CollectionUtils.isEmpty(weTagList) || CollectionUtils.isEmpty(filterWeTags)) {
            return new ArrayList<>();
        }
        // 过滤tagId和external_userid相同的数据
        CopyOnWriteArrayList<WeTag> filterWeTagList = new CopyOnWriteArrayList<>(weTagList);
        if (CollectionUtils.isEmpty(filterWeTagList)) {
            return new ArrayList<>();
        }
        // 根据标签id和标签id对应的数量进行分组，Map<标签ID，标签ID对应的客户数量>
        Map<String, List<WeTag>> tagMap = filterWeTagList.stream().collect(Collectors.groupingBy(WeTag::getTagId));
        // 转换查询出来的标签信息
        List<WeTagCustomerStatisticsVO> resultList = filterWeTags.stream().map(weTag -> {
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
            if (tagMap.get(weTagData.getTagId()) != null) {
                weTagData.setCustomerCnt(tagMap.get(weTagData.getTagId()).size());
            }
        }
        return resultList;
    }

    @Override
    public List<Long> getCustomerByTags(String corpId, String tagIds) {
        if(StringUtils.isBlank(tagIds) || StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
         return weTagMapper.getCustomerByTags(corpId, tagIds);
    }

    @Override
    public void addTag(String corpId, WeFlowerCustomerRel rel, List<String> tagIds) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId) || rel == null || CollectionUtils.isEmpty(tagIds)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 新增标签关系
        List<WeFlowerCustomerTagRel> tagRelList = tagIds.stream().map(tagId -> new WeFlowerCustomerTagRel(tagId, rel))
                                                        .collect(Collectors.toList());
        weFlowerCustomerTagRelService.batchInsetWeFlowerCustomerTagRel(tagRelList);
        // 调用打标签接口
        CustomerTagEdit customerTagEdit = CustomerTagEdit.builder()
                                                         .userid(rel.getUserId())
                                                         .external_userid(rel.getExternalUserid())
                                                         .add_tag(ArrayUtil.toArray(tagIds, String.class))
                                                         .build();
         weCustomerClient.makeCustomerLabel(customerTagEdit, corpId);
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

    /**
     * 根据标签ID列表获取标签名
     *
     * @param tagIds 标签ID列表
     * @return 标签名列表
     */
    @Override
    public List<String> getTagNameByIds(List<String> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        // 根据标签ID，获取状态为正常的标签信息
        List<WeTag> weTagList = weTagMapper.selectList(new LambdaQueryWrapper<WeTag>().in(WeTag::getTagId, tagIds).eq(WeTag::getStatus, Constants.NORMAL_CODE));
        if (CollectionUtils.isEmpty(weTagList)) {
            return new ArrayList<>();
        }
        return weTagList.stream().map(WeTag::getName).collect(Collectors.toList());
    }
}
