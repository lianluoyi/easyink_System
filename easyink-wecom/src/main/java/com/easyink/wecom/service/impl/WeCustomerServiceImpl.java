package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.annotation.Excel;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.sop.CustomerSopConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.sop.CustomerSopPropertyRel;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.core.text.Convert;
import com.easyink.common.enums.CustomerExtendPropertyEnum;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.MethodParamType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.customer.SubjectTypeEnum;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.common.utils.wecom.CorpSecretDecryptUtil;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.client.WeCustomerClient;
import com.easyink.wecom.client.WeUnionIdClient;
import com.easyink.wecom.client.WeUpdateIDClient;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.dto.customer.CustomerTagEdit;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;
import com.easyink.wecom.domain.dto.customer.ExternalUserDetail;
import com.easyink.wecom.domain.dto.customer.GetExternalDetailResp;
import com.easyink.wecom.domain.dto.customer.req.GetByUserReq;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easyink.wecom.domain.dto.customersop.Column;
import com.easyink.wecom.domain.dto.pro.EditCustomerFromPlusDTO;
import com.easyink.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easyink.wecom.domain.dto.unionid.GetUnionIdDTO;
import com.easyink.wecom.domain.entity.WeCustomerExportDTO;
import com.easyink.wecom.domain.entity.WeExternalUseridMapping;
import com.easyink.wecom.domain.vo.QueryCustomerFromPlusVO;
import com.easyink.wecom.domain.vo.WeCustomerExportVO;
import com.easyink.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easyink.wecom.domain.vo.customer.SessionArchiveCustomerVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerSumVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.sop.CustomerSopVO;
import com.easyink.wecom.domain.vo.unionid.GetUnionIdVO;
import com.easyink.wecom.handler.ExtendPropHolder;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeExternalUseridMappingMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import com.easyink.wecom.utils.redis.CustomerRedisCache;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 类名： WeCustomerServiceImpl
 *
 * @author 佚名
 * @date 2021/8/26 20:28
 */
@Slf4j
@Service
public class WeCustomerServiceImpl extends ServiceImpl<WeCustomerMapper, WeCustomer> implements WeCustomerService {
    @Autowired
    private WeCustomerMapper weCustomerMapper;
    @Autowired
    private WeCustomerClient weCustomerClient;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;

    @Autowired
    private WeTagGroupService weTagGroupService;

    @Autowired
    private WeUserService weUserService;

    @Autowired
    private WechatOpenService wechatOpenService;

    @Autowired
    @Lazy
    private WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;

    @Autowired
    private WeUserClient weUserClient;
    @Autowired
    private WeCustomerTrajectoryService weCustomerTrajectoryService;
    @Autowired
    @Lazy
    private PageHomeService pageHomeService;
    @Autowired
    private WeCustomerExtendPropertyRelService weCustomerExtendPropertyRelService;
    @Autowired
    @Lazy
    private WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    @Autowired
    private WeUnionIdClient weUnionIdClient ;
    @Autowired
    private CorpSecretDecryptUtil corpSecretDecryptUtil ;

    @Autowired
    private We3rdAppService we3rdAppService;

    @Autowired
    private WeExternalUseridMappingMapper weExternalUseridMappingMapper;

    @Autowired
    private WeUpdateIDClient convertIDClient;
    @Autowired
    private WeTagService weTagService;
    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;

    /**
     * 导出时查询数据库最大批量数
     */
    private static final int EXPORT_QUERY_BATCH_SIZE = 5000;
    /**
     * 导出一个sheet的最大行数
     */
    private static final int EXPORT_SHEET_ROWS = 1000000;
    @Resource(name = "customerRedisCache")
    private CustomerRedisCache customerRedisCache;

    public WeCustomerServiceImpl(WeFlowerCustomerRelMapper weFlowerCustomerRelMapper) {
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WeCustomer weCustomer) {
        if (weCustomer == null) {
            return false;
        }
        if (StringUtils.isNotBlank(weCustomer.getExternalUserid())
                && StringUtils.isNotBlank(weCustomer.getUserId())
                && StringUtils.isNotBlank(weCustomer.getCorpId())) {
            //添加corpId不为空
            WeCustomer weCustomerBean = selectWeCustomerById(weCustomer.getExternalUserid(), weCustomer.getCorpId());
            if (weCustomerBean != null) {
                WeFlowerCustomerRel weFlowerCustomerRel = new WeFlowerCustomerRel();
                weFlowerCustomerRel.setCorpId(weCustomer.getCorpId());
                weFlowerCustomerRel.setRemark(weCustomer.getRemark());
                weFlowerCustomerRel.setUserId(weCustomer.getUserId());
                weFlowerCustomerRel.setRemarkMobiles(weCustomer.getPhone());
                weFlowerCustomerRel.setDescription(weCustomer.getDesc());

                weFlowerCustomerRelService.update(weFlowerCustomerRel, new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getExternalUserid, weCustomer.getExternalUserid())
                        .eq(WeFlowerCustomerRel::getUserId, weCustomer.getUserId())
                        .eq(WeFlowerCustomerRel::getCorpId, weCustomer.getCorpId()));

                return weCustomerMapper.updateWeCustomer(weCustomer) == 1;
            } else {
                return weCustomerMapper.insertWeCustomer(weCustomer) == 1;
            }
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Convert2Cipher(paramType = MethodParamType.STRUCT)
    public void editCustomer(EditCustomerDTO dto) {
        if (dto == null || StringUtils.isAnyBlank(dto.getExternalUserid(), dto.getUserId(), dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        String corpId = dto.getCorpId();
        String userId = dto.getUserId();
        String externalUserId = dto.getExternalUserid();
        // 1.修改客户生日
        WeCustomer weCustomer = dto.transferToCustomer();
        if (dto.getBirthday() != null) {
            weCustomerMapper.updateBirthday(weCustomer);
        }
        // 2.修改自定义扩展字段属性
        if (CollectionUtils.isNotEmpty(weCustomer.getExtendProperties())) {
            weCustomerExtendPropertyRelService.updateBatch(weCustomer);
        }

        // 3.修改跟进人对客户的备注信息
        WeFlowerCustomerRel rel = dto.transferToCustomerRel();
        WeCustomerDTO.WeCustomerRemark editReq = new WeCustomerDTO().new WeCustomerRemark(rel);
        weCustomerClient.remark(editReq, corpId);
        weFlowerCustomerRelService.update(rel, new LambdaUpdateWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                .eq(WeFlowerCustomerRel::getUserId, userId)
        );
        // 4.修改客户标签
        if (CollUtil.isNotEmpty(dto.getAddTags()) || CollUtil.isNotEmpty(dto.getRemoveTags())) {
            // 批量修改标签
            WeCustomerService weCustomerService = (WeCustomerServiceImpl)AopContext.currentProxy();
            weCustomerService.batchMarkCustomTag(corpId, userId, externalUserId, dto.getAddTags(), dto.getRemoveTags());
        }
        // 5.添加轨迹内容(信息动态)
        weCustomerTrajectoryService.recordEditOperation(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchMarkCustomTagWithTagIds(String corpId, String userId, String externalUserId, List<String> addTagIds, List<String> removeTagIds) {
        List<WeTag> localAddTagList = new ArrayList<>();
        List<WeTag> localDelTagList = new ArrayList<>();
        if (StringUtils.isAnyBlank(userId, externalUserId, corpId) || (CollUtil.isEmpty(addTagIds) && CollUtil.isEmpty(removeTagIds))) {
            log.info("员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", userId, externalUserId, corpId);
            throw new CustomException("增量式打标签错误");
        }
        if (CollUtil.isEmpty(addTagIds) && CollUtil.isEmpty(removeTagIds)) {
            return;
        }
        //查询查出员工和客户关系
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getOne(userId, externalUserId, corpId);
        if (flowerCustomerRel == null) {
            return;
        }
        if (CollUtil.isEmpty(addTagIds) && CollUtil.isEmpty(removeTagIds)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(addTagIds)) {
            // 根据要添加的标签ID获取本地标签列表
            localAddTagList = weTagService.listByIds(addTagIds);
        }
        if (CollectionUtils.isNotEmpty(removeTagIds)){
            // 根据要删除的标签ID获取本地标签列表
            localDelTagList = weTagService.listByIds(removeTagIds);
        }
        if (CollectionUtils.isEmpty(localAddTagList) && CollectionUtils.isEmpty(localDelTagList)) {
            return;
        }
        // 获取有效的添加本地标签ID
        List<String> effectAddTagList = localAddTagList.stream().map(item -> item.getTagId()).collect(Collectors.toList());
        List<WeFlowerCustomerTagRel> addTagRels = new ArrayList<>();
        for (String tagId : effectAddTagList) {
            //本地标签关系
            addTagRels.add(
                    WeFlowerCustomerTagRel.builder()
                            .flowerCustomerRelId(flowerCustomerRel.getId())
                            .externalUserid(flowerCustomerRel.getExternalUserid())
                            .tagId(tagId)
                            .createTime(new Date())
                            .build()
            );
        }
        //官方接口参数
        CustomerTagEdit customerTagEdit = CustomerTagEdit.builder()
                .userid(flowerCustomerRel.getUserId())
                .external_userid(flowerCustomerRel.getExternalUserid())
                .build();
        // 新增标签
        if (CollUtil.isNotEmpty(addTagRels)) {
            weFlowerCustomerTagRelService.batchInsetWeFlowerCustomerTagRel(addTagRels);
            customerTagEdit.setAdd_tag(ArrayUtil.toArray(effectAddTagList, String.class));
            log.info("可打的标签id列表: {}, corpId:{}", effectAddTagList, corpId);
        }
        // 删除标签
        if (CollUtil.isNotEmpty(removeTagIds)) {
            List<String> effectDelTagList = localDelTagList.stream().map(item -> item.getTagId()).collect(Collectors.toList());
            weFlowerCustomerTagRelService.remove(new LambdaQueryWrapper<WeFlowerCustomerTagRel>()
                    .eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, flowerCustomerRel.getId())
                    .eq(WeFlowerCustomerTagRel::getExternalUserid, flowerCustomerRel.getExternalUserid())
                    .in(WeFlowerCustomerTagRel::getTagId, effectDelTagList));
            customerTagEdit.setRemove_tag(ArrayUtil.toArray(effectDelTagList, String.class));
            log.info("可移除的标签id列表: {}, corpId:{}", effectDelTagList, corpId);
        }
        //企微新接口
        weCustomerClient.makeCustomerLabel(customerTagEdit, corpId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchMarkCustomTag(String corpId, String userId, String externalUserId, List<WeTag> addTags, List<WeTag> removeTags) {
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId) || (CollUtil.isEmpty(addTags) && CollUtil.isEmpty(removeTags))) {
            log.info("员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", userId, externalUserId, corpId);
            throw new CustomException("增量式打标签错误");
        }
        List<String> addTagIds = addTags == null ? Collections.emptyList() : addTags.stream()
                                                                                    .map(WeTag::getTagId)
                                                                                    .filter(Objects::nonNull)
                                                                                    .collect(Collectors.toList());
        List<String> removeTagIds = removeTags == null ? Collections.emptyList() : removeTags.stream()
                                                                                             .map(WeTag::getTagId)
                                                                                             .filter(Objects::nonNull)
                                                                                             .collect(Collectors.toList());
        WeCustomerService weCustomerService = (WeCustomerServiceImpl) AopContext.currentProxy();
        weCustomerService.batchMarkCustomTagWithTagIds(corpId, userId, externalUserId, addTagIds, removeTagIds);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeCustomerRemark(WeCustomer weCustomer) {
        if (weCustomer == null
                || StringUtils.isAnyBlank(weCustomer.getCorpId(), weCustomer.getUserId(), weCustomer.getExternalUserid())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 自定义字段修改
        weCustomerExtendPropertyRelService.updateBatch(weCustomer);
        // 企微官方字段修改:如果为“”代表是清除描述的状态，也不能过滤，所以只过滤为空的状态
        if (weCustomer.getRemark() != null || weCustomer.getPhone() != null || weCustomer.getDesc() != null) {
            //如果有修改到备注和描述，调用企微接口修改企微数据
            WeCustomerDTO.WeCustomerRemark weCustomerRemark = new WeCustomerDTO().new WeCustomerRemark(weCustomer);
            //使用企微新接口
            weCustomerClient.remark(weCustomerRemark, weCustomer.getCorpId());
        }
        saveOrUpdate(weCustomer);
    }

    /**
     * 查询企业微信客户
     *
     * @param externalUserId 企业微信客户ID
     * @return 企业微信客户
     */
    @Override
    public WeCustomer selectWeCustomerById(String externalUserId, String corpId) {
        if (StringUtils.isAnyBlank(externalUserId, corpId)) {
            log.error("查询企业数据：externalUserId = {} , corpId = {}", externalUserId, corpId);
            throw new CustomException("查询企业数据失败");
        }

        return weCustomerMapper.selectWeCustomerById(externalUserId, corpId);
    }

    /**
     * 获取用户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return
     */
    @Override
    @DataScope
    @Deprecated
    public List<WeCustomerVO> selectWeCustomerListV2(WeCustomer weCustomer) {


        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        if (CollectionUtils.isNotEmpty(weCustomer.getExtendProperties())) {
            List<WeCustomerRel> extendList = extendProperties(weCustomer);
            if (CollectionUtils.isEmpty(extendList)) {
                return new ArrayList<>();
            }
            weCustomer.setExtendList(extendList);
        }

        return weCustomerMapper.selectWeCustomerListV2(weCustomer);
    }


    /**
     * 判断是否有过滤条件的客户
     *
     * @param weCustomer 客户实体
     * @param corpId     企业ID
     * @return true 存在
     */
    private boolean hasFilterCustomer(WeCustomer weCustomer, String corpId) {
        // 根据自定义字段 筛选满足条件 external_userid和 user_id
        if (CollectionUtils.isNotEmpty(weCustomer.getExtendProperties())) {
            List<WeCustomerRel> extendCustomers = extendProperties(weCustomer);
            if (CollectionUtils.isEmpty(extendCustomers)) {
                return false;
            }
            weCustomer.setExtendList(extendCustomers);

        }
        //  标签筛选满足条件 external_userid和 user_id
        if(StringUtils.isNotBlank(weCustomer.getTagIds())) {
            List<Long> tagFilterCustomers = weTagService.getCustomerByTags(corpId, weCustomer.getTagIds());
            if(CollectionUtils.isEmpty(tagFilterCustomers)) {
                return false;
            }
            weCustomer.setRelIds(tagFilterCustomers);
        }
        return true;
    }

    /**
     * 处理用户自定义字段
     *
     * @param weCustomer
     * @return 筛选后的检索列表
     */
    public List<WeCustomerRel> extendProperties(WeCustomer weCustomer) {
        List<BaseExtendPropertyRel> baseExtendPropertyRelList = weCustomer.getExtendProperties();
        List<BaseExtendPropertyRel> oneLineType = new ArrayList<>();
        List<BaseExtendPropertyRel> multipleType = new ArrayList<>();
        List<BaseExtendPropertyRel> timeType = new ArrayList<>();

        for (BaseExtendPropertyRel baseExtendPropertyRel : baseExtendPropertyRelList) {
            baseExtendPropertyRel.setCorpId(weCustomer.getCorpId());
            // 判断是否为单行文本类型
            if (Objects.equals(baseExtendPropertyRel.getPropertyType(), CustomerExtendPropertyEnum.SINGLE_ROW.getType())) {
                oneLineType.add(baseExtendPropertyRel);
            }
            //判断是否为下拉框类型
            else if (Objects.equals(baseExtendPropertyRel.getPropertyType(), CustomerExtendPropertyEnum.COMBO_BOX.getType())) {
                multipleType.add(baseExtendPropertyRel);
            }
            //判断是否为时间类型
            else if (Objects.equals(baseExtendPropertyRel.getPropertyType(), CustomerExtendPropertyEnum.DATE.getType())) {
                timeType.add(baseExtendPropertyRel);
            }
        }

        List<BaseExtendPropertyRel> oneLineList =  new ArrayList<>();

        if (oneLineType.size()>0){
            oneLineList=weCustomerMapper.selectTypeOneLine(oneLineType);
            if (oneLineList.size()==0){
                return new ArrayList<>();
            }
        }
        if (multipleType.size()>0){
            List<BaseExtendPropertyRel> multipleList=weCustomerMapper.selectTypeMultiple(multipleType);
            if (multipleList.size()==0){
                return new ArrayList<>();
            }
            oneLineList=getIntersectionTypeNotFind(oneLineList,multipleList);
            if (oneLineList.size()==0){
                return new ArrayList<>();
            }
        }
        if (timeType.size()>0){
            List<BaseExtendPropertyRel> timeList=weCustomerMapper.selectTypeTime(timeType);
            if (timeList.size()==0){
                return new ArrayList<>();
            }
            oneLineList=getIntersectionTypeNotFind(oneLineList,timeList);
        }

        List<WeCustomerRel> extendList = new ArrayList<>();
        if (oneLineList.size() > 0) {
            for (BaseExtendPropertyRel list : oneLineList) {
                WeCustomerRel relIds = new WeCustomerRel();
                relIds.setExternalUserid(list.getExternalUserid());
                relIds.setUserId(list.getUserId());
                extendList.add(relIds);
            }
        }
        return extendList;
    }
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 获取查询所需集合。如果其中一个为空，返回另一个
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 查询所需的集合
     */
    public static List<BaseExtendPropertyRel> getIntersectionTypeNotFind(List<BaseExtendPropertyRel> list1, List<BaseExtendPropertyRel> list2) {
        if (list1.size() > 0) {
            if (list2.size() > 0) {
                list1.retainAll(list2);
                return list1;
            } else return list1;
        }
        return list2;
    }

    /**
     * 查询客户sop使用客户
     *
     * @param corpId    企业id
     * @param sopFilter sop过滤条件
     * @return {@link List<WeCustomer>}
     */
    @Override
    public List<WeCustomer> listOfUseCustomer(String corpId, WeOperationsCenterCustomerSopFilterEntity sopFilter) {
        WeCustomerPushMessageDTO weCustomerPushMessageDTO = new WeCustomerPushMessageDTO();
        weCustomerPushMessageDTO.setCorpId(corpId);
        //-1表示筛选条件未选择性别 选择性别的情况下添加过滤条件
        if (!Integer.valueOf(-1).equals(sopFilter.getGender())) {
            weCustomerPushMessageDTO.setGender(String.valueOf(sopFilter.getGender()));
        }
        weCustomerPushMessageDTO.setTagIds(sopFilter.getTagId());
        weCustomerPushMessageDTO.setCustomerStartTime(sopFilter.getStartTime());
        weCustomerPushMessageDTO.setCustomerEndTime(DateUtils.getEndOfDay(sopFilter.getEndTime()));
        weCustomerPushMessageDTO.setUserIds(sopFilter.getUsers());
        weCustomerPushMessageDTO.setDepartmentIds(sopFilter.getDepartments());
        weCustomerPushMessageDTO.setFilterTags(sopFilter.getFilterTagId());
        List<Column> columns = JSONArray.parseArray(sopFilter.getCloumnInfo(), Column.class);
        // 从额外属性条件中获取来源、出生日期条件
        setAddWayAndBirthday(columns, weCustomerPushMessageDTO);
        // 普通属性查询客户
        List<WeCustomer> weCustomers = selectWeCustomerListNoRel(weCustomerPushMessageDTO);
        // 若标签条件不为空，筛选出包含有所有标签条件的客户
        if (StringUtils.isNotBlank(weCustomerPushMessageDTO.getTagIds())) {
            weCustomers = weCustomers.stream().filter(customer -> {
                        if (StringUtils.isBlank(customer.getMarkTagIds()) || StringUtils.isBlank(weCustomerPushMessageDTO.getTagIds())) {
                            return false;
                        }
                        return new HashSet<>(Arrays.asList(customer.getMarkTagIds().split(DictUtils.SEPARATOR)))
                                .containsAll(Arrays.asList(weCustomerPushMessageDTO.getTagIds().split(DictUtils.SEPARATOR)));
                    }
            ).collect(Collectors.toList());
        }
        // 有存在额外属性条件客户
        if (CollectionUtils.isNotEmpty(columns)) {
            // 额外属性条件下的客户
            List<String> customers = selectWeCustomerListExtendRel(columns, weCustomerPushMessageDTO);
            if (CollectionUtils.isNotEmpty(customers)) {
                // 存在额外属性条件则求普通条件、额外属性条件的交集
                weCustomers = weCustomers.stream().filter(customer -> customers.contains(customer.getExternalUserid())).collect(Collectors.toList());
            } else {
                weCustomers = new ArrayList<>();
            }
        }
        return weCustomers;
    }

    /**
     * 额外字段属性客户查询
     *
     * @param columns {@link Column}
     * @param dto {@link WeCustomerPushMessageDTO}
     * @return 客户ID列表
     */
    public List<String> selectWeCustomerListExtendRel(List<Column> columns, WeCustomerPushMessageDTO dto) {
        if (CollectionUtils.isEmpty(columns) || dto == null) {
            return new ArrayList<>();
        }
        // 符合日期条件的external_userid
        List<String> dateExternalUserIds = new ArrayList<>();
        // 日期条件的额外字段个数
        HashSet<Long> dateExtendNum = new HashSet<>();
        // 符合下拉、单选条件的external_userid
        List<String> otherExternalUserIds = new ArrayList<>();
        // 所有符合条件的额外字段关系
        List<CustomerSopPropertyRel> customerSopPropertyRels = weCustomerExtendPropertyRelService.selectBaseExtendValue(columns, dto);
        // 日期查询条件
        HashSet<String> extendPropertyIds = new HashSet<>();
        for (CustomerSopPropertyRel customerSopPropertyRel : customerSopPropertyRels) {
            for (Column column : columns) {
                // extend_property_id相同，判断type类型
                if (customerSopPropertyRel.getExtendPropertyId().equals(column.getExtendPropertyId())) {
                    // type 为 "7" 表示该过滤条件为日期范围选择器
                    if (CustomerSopConstants.CUSTOMER_SOP_DATE_RANGE.equals(column.getType())) {
                        // 将日期额外字段id存入hashset去重
                        dateExtendNum.add(column.getExtendPropertyId());
                        // 判断当前的值是否在日期范围内，若在则存入external_userid
                        if (DateUtils.isDateRange(column.getPropertyValue().split(DictUtils.SEPARATOR)[0], column.getPropertyValue().split(DictUtils.SEPARATOR)[1], customerSopPropertyRel.getPropertyValue())) {
                            dateExternalUserIds.add(customerSopPropertyRel.getExternalUserid());
                        }
                    } else {
                        // 下拉框、单选框属性值
                        extendPropertyIds.add(customerSopPropertyRel.getExtendPropertyId().toString());
                    }
                }
            }
        }
        // 过滤出匹配所有日期字段的客户id
        List<String> allMatchDateExridList = handleMatchDateExtend(dateExternalUserIds, dateExtendNum.size());
        // 获取筛选条件中除日期外的额外字段值
        List<String> allExtendPropertyByFilter = columns.stream().filter(item -> !CustomerSopConstants.CUSTOMER_SOP_DATE_RANGE.equals(item.getType())).map(Column::getPropertyValue).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(extendPropertyIds)) {
            List<CustomerSopPropertyRel> extendPropertyRelList = weCustomerExtendPropertyRelService.selectExtendGroupByCustomer(new ArrayList<>(extendPropertyIds), dto.getUserIds());
            // 是否有其他属性值
            if (CollectionUtils.isNotEmpty(allExtendPropertyByFilter) && CollectionUtils.isNotEmpty(extendPropertyRelList)) {
                for (CustomerSopPropertyRel customerSopPropertyRel : extendPropertyRelList) {
                    // 客户的其他属性值列表
                    List<String> customerPropertyList = Arrays.stream(customerSopPropertyRel.getPropertyValue().split(DictUtils.SEPARATOR)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(customerPropertyList)) {
                        // 客户的其他类型值包含所有的过滤条件，才进行判断
                        if (customerPropertyList.containsAll(allExtendPropertyByFilter)) {
                            // 相同则存入external_userid
                            otherExternalUserIds.add(customerSopPropertyRel.getExternalUserid());
                        }
                    }
                }
            }
        }
        return handleExtendPropertyCustomer(allMatchDateExridList, otherExternalUserIds, columns);
    }

    /**
     * 过滤出匹配所有日期字段的客户id
     *
     * @param dateExternalUserIds 符合日期条件的客户Id列表
     * @param dateExtendNum 额外字段日期条件个数
     */
    private List<String> handleMatchDateExtend(List<String> dateExternalUserIds, int dateExtendNum) {
        if (CollectionUtils.isNotEmpty(dateExternalUserIds) && dateExtendNum > 0) {
            return dateExternalUserIds.stream()
                    .filter(externalUserId -> Collections.frequency(dateExternalUserIds, externalUserId) == dateExtendNum)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 从额外属性条件中取出源、出生日期条件
     *
     * @param columns {@link Column}
     * @param weCustomerPushMessageDTO {@link WeCustomerPushMessageDTO}
     */
    public void setAddWayAndBirthday(List<Column> columns, WeCustomerPushMessageDTO weCustomerPushMessageDTO) {
        if (CollectionUtils.isNotEmpty(columns)) {
            Iterator<Column> iterator = columns.iterator();
            while (iterator.hasNext()) {
                Column column = iterator.next();
                // type 为 "addWay"，表示该过滤条件为来源
                if (CustomerSopConstants.CUSTOMER_SOP_ADD_WAY.equals(column.getType())) {
                    // 获取来源的值
                    String addWay = column.getPropertyValue();
                    weCustomerPushMessageDTO.setAddWay(addWay);
                    // 因是普通属性查询，不需要到额外字段关系表查询，故删除
                    iterator.remove();
                }
                // type 为 "1" 表示该过滤条件为出生日期范围
                if (CustomerSopConstants.CUSTOMER_SOP_BIRTHDAY_RANGE.equals(column.getType())) {
                    weCustomerPushMessageDTO.setBirthdayStr(column.getPropertyValue());
                    // 因是普通属性查询，不需要到额外字段关系表查询，故删除
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 额外属性下客户处理
     *
     * @param dateExternalUserIds  日期条件下的客户
     * @param otherExternalUserIds 下拉、单选条件下的客户
     * @return 客户id列表
     */
    public List<String> handleExtendPropertyCustomer(List<String> dateExternalUserIds, List<String> otherExternalUserIds, List<Column> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return new ArrayList<>();
        }
        // 日期条件
        boolean isDate = false;
        // 下拉、单选条件
        boolean isOther = false;
        // 过滤筛选条件中type = 7 日期的，存在则表示有日期条件
        List<Column> dateType = columns.stream().filter(item -> CustomerSopConstants.CUSTOMER_SOP_DATE_RANGE.equals(item.getType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dateType)) {
            isDate = true;
        }
        // 过滤筛选条件中type != 7 的，存在表示有其他条件
        List<Column> otherType = columns.stream().filter(item -> !CustomerSopConstants.CUSTOMER_SOP_DATE_RANGE.equals(item.getType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(otherType)) {
            isOther = true;
        }
        // 日期 下拉 单选
        if (isDate && isOther) {
            if (CollectionUtils.isNotEmpty(dateExternalUserIds) && CollectionUtils.isNotEmpty(otherExternalUserIds)) {
            // 日期和下拉、单选都不为空，求交集
            return dateExternalUserIds.stream().filter(otherExternalUserIds::contains).collect(Collectors.toList());
            }
        } else if (isDate) {
            // 日期
            if (CollectionUtils.isNotEmpty(dateExternalUserIds)) {
                // 为日期条件下不为空的客户
                return dateExternalUserIds;
            }
        } else if (isOther) {
            // 单选、下拉
            if (CollectionUtils.isNotEmpty(otherExternalUserIds)) {
                // 为符合下拉、单选不为空条件下的客户
                return otherExternalUserIds;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 查询去重客户去重后企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户信息列表
     */
    @DataScope
    @Override
    public List<SessionArchiveCustomerVO> selectWeCustomerListDistinctV2(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId()) || PageInfoUtil.getPageSize() == null || PageInfoUtil.getPageNum() == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 分页大小
        int pageSize = PageInfoUtil.getPageSize();
        // 分页页数
        int startIndex = PageInfoUtil.getStartIndex();
        List<String> filterNameExternalUserIdList = new ArrayList<>();
        // 客户姓名查询条件不为空，根据姓名查询匹配的客户id列表
        if (StringUtils.isNotBlank(weCustomer.getName())) {
            // 获取根据姓名查询出的客户id列表
            filterNameExternalUserIdList = getFilterNameExternalUserId(weCustomer.getName(), weCustomer.getCorpId());
            if (CollectionUtils.isEmpty(filterNameExternalUserIdList)) {
                return Collections.emptyList();
            }
        }
        // 获取数据权限下的客户id列表
        List<SessionArchiveCustomerVO> customerList = weFlowerCustomerRelMapper.selectExternalUseridByDataScope(weCustomer.getCorpId(), weCustomer, filterNameExternalUserIdList, startIndex, pageSize);
        if (CollectionUtils.isEmpty(customerList)) {
            return Collections.emptyList();
        }
        // 根据客户id去重
        List<SessionArchiveCustomerVO> resultList = new ArrayList<>(new HashSet<>(customerList));
        // 补充缺少的数据
        resultList = suppleData(resultList, startIndex, pageSize, weCustomer, filterNameExternalUserIdList);
        // 获取external_userid列表
        List<String> externalUserIdList = resultList.stream().map(SessionArchiveCustomerVO::getExternalUserid).collect(Collectors.toList());
        // 获取客户信息数据列表
        List<SessionArchiveCustomerVO> customerInfoList = weCustomerMapper.selectWeCustomerListDistinctV2(weCustomer.getCorpId(), externalUserIdList);
        if (CollectionUtils.isEmpty(customerInfoList)) {
            return resultList;
        }
        // 补充客户信息数据到结果列表
        for (SessionArchiveCustomerVO resultVO : resultList) {
            for (SessionArchiveCustomerVO customerInfo : customerInfoList) {
                if (resultVO.getExternalUserid().equals(customerInfo.getExternalUserid())) {
                    resultVO.setCustomerInfo(customerInfo.getAvatar(), customerInfo.getName());
                }
            }
        }
        // 按照添加时间倒序排序
        resultList.sort(Comparator.comparing(SessionArchiveCustomerVO::getCreateTime).reversed());
        return resultList;
    }

    /**
     * 获取会话存档-客户检索-客户列表-去重后的客户数
     *
     * @param weCustomer {@link WeCustomer}
     * @return 客户总数
     */
    @Override
    public WeCustomerSumVO customerCount(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<String> filterNameExternalUserIdList = new ArrayList<>();
        // 客户姓名查询条件不为空，根据姓名查询匹配的客户id列表
        if (StringUtils.isNotBlank(weCustomer.getName())) {
            // 获取根据姓名查询出的客户id列表
            filterNameExternalUserIdList = getFilterNameExternalUserId(weCustomer.getName(), weCustomer.getCorpId());
            if (CollectionUtils.isEmpty(filterNameExternalUserIdList)) {
                return WeCustomerSumVO.builder()
                        .ignoreDuplicateCount(filterNameExternalUserIdList.size())
                        .build();
            }
        }
        Integer count = weFlowerCustomerRelMapper.selectExternalUseridByDataScopeCount(weCustomer.getCorpId(), weCustomer, filterNameExternalUserIdList);
        return WeCustomerSumVO.builder()
                .ignoreDuplicateCount(count)
                .build();
    }

    /**
     * 补充缺少的数据
     *
     * @param dataList                     结果列表
     * @param startIndex                   分页起始位置
     * @param pageSize                     分页大小
     * @param weCustomer                   {@link WeCustomer}
     * @param filterNameExternalUserIdList 要过滤的客户id列表
     */
    private List<SessionArchiveCustomerVO> suppleData(List<SessionArchiveCustomerVO> dataList, Integer startIndex, Integer pageSize, WeCustomer weCustomer, List<String> filterNameExternalUserIdList) {
        if (CollectionUtils.isEmpty(dataList) || startIndex == null || pageSize == null || weCustomer == null) {
            return dataList;
        }
        // 判断查询的结果是否满足分页大小
        while (dataList.size() < pageSize) {
            startIndex += pageSize;
            // 缺少多少条，就往后再次查几条。缺少的条数 = 分页大小 - 列表长度
            int endIndex = pageSize - dataList.size();
            // 不满足，继续查询，将新的查询结果添加到列表
            List<SessionArchiveCustomerVO> nextList = weFlowerCustomerRelMapper.selectExternalUseridByDataScope(weCustomer.getCorpId(), weCustomer, filterNameExternalUserIdList, startIndex, endIndex);
            // 没有数据则跳出循环
            if (CollectionUtils.isEmpty(nextList)) {
                break;
            }
            dataList.addAll(nextList);
            // 再次去重
            dataList = new ArrayList<>(new HashSet<>(dataList));
        }
        return dataList;
    }

    /**
     * 获取根据姓名查询出的客户id列表
     *
     * @param name   客户姓名
     * @param corpId 企业ID
     * @return 客户id列表
     */
    private List<String> getFilterNameExternalUserId(String name, String corpId) {
        if (StringUtils.isAnyBlank(name, corpId)) {
            return Collections.emptyList();
        }
        List<WeCustomer> weCustomers = weCustomerMapper.selectList(new LambdaQueryWrapper<WeCustomer>()
                .select(WeCustomer::getExternalUserid)
                .like(WeCustomer::getName, name)
                .eq(WeCustomer::getCorpId, corpId));
        if (CollectionUtils.isEmpty(weCustomers)) {
            return Collections.emptyList();
        }
        return weCustomers.stream().map(WeCustomer::getExternalUserid).collect(Collectors.toList());
    }

    /**
     * 查询去重客户去重后企业微信客户列表
     *
     * @param weCustomer {@link WeCustomer}
     * @return
     */
    @DataScope
    @Override
    public List<WeCustomerVO> selectWeCustomerListDistinct(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return weCustomerMapper.selectWeCustomerListDistinct(weCustomer);
    }

    @Override
    public List<WeCustomerUserListVO> listUserListByCustomerId(String customerId, String corpId) {
        if (StringUtils.isAnyBlank(customerId, corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return weCustomerMapper.selectUserListByCustomerId(customerId, corpId);
    }

    @Override
    @DataScope
    public WeCustomerSumVO weCustomerCount(WeCustomer weCustomer) {
        if (StringUtils.isAnyBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        String corpId = weCustomer.getCorpId();
        if (!hasFilterCustomer(weCustomer, corpId))  {
            return WeCustomerSumVO.empty();
        }
        Integer ignoreDuplicateCnt  = weCustomerMapper.ignoreDuplicateCustomerCnt(weCustomer) ;
        return WeCustomerSumVO.builder()
                .ignoreDuplicateCount(ignoreDuplicateCnt)
                .build();
    }

    @Override
    @Async
    public void syncWeCustomerV2(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        log.info("开始同步客户,corpId:{}", corpId);
        long startTime = System.currentTimeMillis();

        // 1.调用[获取配置了客户联系功能的成员列表] 企微API
        List<String> userIdList = weCustomerClient.getFollowUserList(corpId).getFollowerUserIdList();

        // 2.每个员工依次调用[批量获取客户详情] 企微API, 同步其客户详情
        for (String userId : userIdList) {
            try {
                batchGetCustomerDetailAndSyncLocal(corpId, userId);
            }catch (Exception e) {
                log.error("同步处理单个员工的客户异常,corpId:{}, userId:{}, e:{}", corpId, userId, ExceptionUtils.getStackTrace(e));
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("同步客户结束[新]:corpId:{}耗时：{}", corpId, Double.valueOf((endTime - startTime) / 1000.00D));
        // 4.客户信息同步后,刷新数据概览页客户数据
        pageHomeService.getCustomerData(corpId);
    }


    /**
     * 批量获取客户详情 并同步到本地
     *
     * @param corpId 企业id
     * @param userId 用户id
     */
    public void batchGetCustomerDetailAndSyncLocal(String corpId, String userId) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(userId)) {
            log.info("批量获取客户详情:参数缺失,corpId:{},userId:{}", corpId, userId);
            return;
        }
        GetByUserResp resp = null;
        try {
            // 1. API请求:请求企微[批量获取客户详情]接口
            GetByUserReq req = new GetByUserReq(userId);
            resp = (GetByUserResp) req.executeTillNoNextPage(corpId);
        } catch (Exception e) {
            log.error("[批量获取客户详情异常] corpId:{}, userId:{}", corpId, userId);
        }
        if (resp == null){
            log.info("[批量获取客户详情] 该员工没有添加客户, corpId:{}, userId:{}", corpId, userId);
            return;
        }
        // 2. 数据处理:对返回的数据进行处理
        resp.handleData(corpId, null);
        if (resp.isEmptyResult()) {
            log.info("[批量获取客户详情] 该员工没有添加客户, corpId:{}, userId:{}", corpId, userId);
            return;
        }
        // 3. 数据对齐:本地成员-客户关系数据与服务端对齐,同步远端修改的数据
        List<String> externalUserIdList = resp.getCustomerList().stream().map(WeCustomer::getExternalUserid).collect(Collectors.toList());
        List<WeFlowerCustomerRel> localRelList = weFlowerCustomerRelService.list(
                new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getCorpId, corpId)
                        .eq(WeFlowerCustomerRel::getUserId, userId)
                        .in(WeFlowerCustomerRel::getExternalUserid, externalUserIdList)
        );
        weFlowerCustomerRelService.alignData(resp, userId, corpId , localRelList);
        //**** 客户信息更新、插入 , 客户-成员关系更新 , 客户-标签关系更新
        // 4. 数据同步:插入、更新 客户数据,员工-客户关系
        BatchInsertUtil.doInsert(resp.getCustomerList(), this::batchInsert);
        BatchInsertUtil.doInsert(resp.getRelList(), list -> weFlowerCustomerRelService.batchInsert(list));
        // 5. 数据同步: 客户-标签关系 ,获取每个客户关系对应的标签,并同步更新数据库
        List<WeFlowerCustomerTagRel> tagRelList = resp.getCustomerTagRelList(localRelList);
        List<Long> relIds = localRelList.stream().map(WeFlowerCustomerRel::getId).collect(Collectors.toList());
        weFlowerCustomerTagRelService.syncLocalTagFromRemote(tagRelList, relIds);
    }


    /**
     * 调用企微根据corpId获取离职员工列表
     *
     * @param corpId 企业id
     * @return 离职员工列表
     */
    @Override
    public List<LeaveWeUserListsDTO.LeaveWeUser> getLeaveWeUsers(String corpId) {
        LeaveWeUserListsDTO leaveWeUserListsDTO = new LeaveWeUserListsDTO();
        List<LeaveWeUserListsDTO.LeaveWeUser> result = new ArrayList<>();
        do {
            Map<String, Object> map = new HashMap<>();
            //判断是不是第一次获取，不是的话
            if (StringUtils.isNotBlank(leaveWeUserListsDTO.getNext_cursor())) {
                map.put("cursor", leaveWeUserListsDTO.getNext_cursor());
            }
            //企微获取数据
            leaveWeUserListsDTO = weUserClient.leaveWeUsers(map, corpId);

            if (CollUtil.isEmpty(leaveWeUserListsDTO.getInfo())) {
                continue;
            }
            //判断是不是第一次获取
            result.addAll(leaveWeUserListsDTO.getInfo());
        } while (StringUtils.isNotBlank(leaveWeUserListsDTO.getNext_cursor()));

        return result;
    }

    /**
     * @param leaveWeUsers 离职员工列表
     * @return map（离职员工id+","+"离职时间"，客户集合）
     */
    @Override
    public Map<String, List<String>> replaceCustomerListToMap(List<LeaveWeUserListsDTO.LeaveWeUser> leaveWeUsers) {

        if (CollUtil.isEmpty(leaveWeUsers)) {
            return new HashMap<>();
        }
        Map<String, List<String>> map = new HashMap<>(leaveWeUsers.size());

        for (LeaveWeUserListsDTO.LeaveWeUser leaveWeUser : leaveWeUsers) {
            if (map.containsKey(leaveWeUser.getHandover_userid() + "," + leaveWeUser.getDimission_time())) {
                //如果map已存在数据，就修改集合
                List<String> listExternalUserid = map.get(leaveWeUser.getHandover_userid() + "," + leaveWeUser.getDimission_time());
                listExternalUserid.add(leaveWeUser.getExternal_userid());
                map.put(leaveWeUser.getHandover_userid() + "," + leaveWeUser.getDimission_time(), listExternalUserid);
            } else {
                //客户的离职客户id列表
                List<String> listExternalUserid = new ArrayList<>(leaveWeUsers.size());
                listExternalUserid.add(leaveWeUser.getExternal_userid());
                //userId,时间，离职时间
                map.put(leaveWeUser.getHandover_userid() + "," + leaveWeUser.getDimission_time(), listExternalUserid);
            }
        }
        return map;
    }

    /**
     * 客户批量打标签
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMakeLabel(List<WeMakeCustomerTagVO> list, String updateBy) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (WeMakeCustomerTagVO item : list) {
            IncrementalMarkTag(item);
            // 有操作人才需要记录信息动态 且 修改了标签
            if (StringUtils.isNotBlank(updateBy) && !(CollUtil.isEmpty(item.getAddTag()) && CollUtil.isEmpty(item.getRemoveTags()))) {
                recordBatchTag(item.getCorpId(),item.getUserId(),item.getExternalUserid(),updateBy,item.getAddTag().stream().map(WeTag::getName).collect(Collectors.toList()), CustomerTrajectoryEnums.TagType.BATCH_ADD_TAG.getType());
            }

        }

    }

    /**
     * 记录批量操作标签信息动态
     *
     * @param corpId 公司id
     * @param userId 员工id
     * @param externalUserid 客户id
     * @param updateBy 操作人
     * @param editTag 进行修改标签
     * @param type 操作类型
     */
    public void recordBatchTag(String corpId,String userId,String externalUserid,String updateBy,List<String> editTag,String type){
        if (CollectionUtils.isEmpty(editTag)||StringUtils.isAnyBlank(corpId,updateBy,userId,externalUserid)){
            log.info("记录批量操作标签信息动态时，公司id，员工id，客户id,操作人,标签列表不能为空，corpId：{}，userId：{}，externalUserid：{}，updateBy：{}，addTag：{}", corpId, userId, externalUserid,updateBy,editTag);
            return;
        }
        TagRecordUtil tagRecordUtil=new TagRecordUtil();
        String content=tagRecordUtil.buildEditTagContent(updateBy,type);
        editTag.removeAll(Collections.singleton(null));
        String detail = String.join(",", editTag);
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId,userId,externalUserid,content,detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void singleMarkLabel(String corpId, String userId, String externalUserId, List<String> addTagIds, String oprUserName){
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId) || CollUtil.isEmpty(addTagIds)) {
            log.info("员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", userId, externalUserId, corpId);
            return;
        }
        WeCustomerService weCustomerService = (WeCustomerService)AopContext.currentProxy();
        weCustomerService.batchMarkCustomTagWithTagIds(corpId, userId, externalUserId, addTagIds, null);
    }

    /**
     * 增量式打标签
     *
     * @param weMakeCustomerTag
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void IncrementalMarkTag(WeMakeCustomerTagVO weMakeCustomerTag) {
        if (StringUtils.isAnyBlank(weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId())) {
            log.error("员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId());
            throw new CustomException("增量式打标签错误");
        }
        //增量标签
        List<WeTag> addTags = weMakeCustomerTag.getAddTag();
        // 移除标签
        List<WeTag> removeTags = weMakeCustomerTag.getRemoveTags();
        if (CollUtil.isEmpty(addTags) && CollUtil.isEmpty(removeTags)) {
            return;
        }
        List<String> addTagIds = addTags == null ? Collections.emptyList() : addTags.stream().map(WeTag::getTagId).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> removeTagIds = removeTags == null ? Collections.emptyList() : removeTags.stream().map(WeTag::getTagId).filter(Objects::nonNull).collect(Collectors.toList());
        WeCustomerService weCustomerService = (WeCustomerService)AopContext.currentProxy();
        weCustomerService.batchMarkCustomTagWithTagIds(weMakeCustomerTag.getCorpId(), weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), addTagIds, removeTagIds);
    }

    /**
     * 删除所有标签关系
     *
     * @param flowerCustomerRel 关系实体
     */
    @Transactional
    public void removeAllLabel(WeFlowerCustomerRel flowerCustomerRel) {
        if (StringUtils.isBlank(flowerCustomerRel.getCorpId())) {
            log.error("员工id，客户id，公司id不能为空，corpId：{}", flowerCustomerRel.getCorpId());
            throw new CustomException("删除所有标签关系");
        }
        //构造查询条件
        LambdaQueryWrapper<WeFlowerCustomerTagRel> queryWrapper = new LambdaQueryWrapper<WeFlowerCustomerTagRel>()
                .eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, flowerCustomerRel.getId());
        List<WeFlowerCustomerTagRel> removeTag = weFlowerCustomerTagRelService.list(queryWrapper);

        //官方接口删除
        if (!CollectionUtils.isEmpty(removeTag) && weFlowerCustomerTagRelService.remove(queryWrapper)) {
            //企微新接口
            weCustomerClient.makeCustomerLabel(
                    CustomerTagEdit.builder()
                            .external_userid(flowerCustomerRel.getExternalUserid())
                            .userid(flowerCustomerRel.getUserId())
                            .remove_tag(ArrayUtil.toArray(removeTag.stream().map(WeFlowerCustomerTagRel::getTagId).collect(Collectors.toList()), String.class))
                            .build(), flowerCustomerRel.getCorpId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeLabel(RemoveWeCustomerTagDTO removeWeCustomerTagDTO) {
        List<String> removeTagList = removeWeCustomerTagDTO.getWeTagIdList();
        List<RemoveWeCustomerTagDTO.WeUserCustomer> userCustomerList = removeWeCustomerTagDTO.getCustomerList();
        if (CollUtil.isEmpty(removeTagList) || CollUtil.isEmpty(userCustomerList)) {
            throw new WeComException("请传入需要删除的标签和对应的用户");
        }

        // 调用企微API依次删除客户标签，删除成功后操作数据库(目前企微没有批量编辑标签接口,只能单个删除)
        for (RemoveWeCustomerTagDTO.WeUserCustomer userCustomer : userCustomerList) {
            if (StringUtils.isBlank(userCustomer.getCorpId())) {
                log.error("删除标签失败,公司id不能为空", userCustomer.getCorpId());
                throw new CustomException("删除标签失败");
            }

            CustomerTagEdit customerTagEdit = CustomerTagEdit.builder()
                    .external_userid(userCustomer.getExternalUserid())
                    .userid(userCustomer.getUserId())
                    .remove_tag(ArrayUtil.toArray(removeTagList, String.class))
                    .build();
            //企微新接口
            try {
                WeResultDTO response = weCustomerClient.makeCustomerLabel(customerTagEdit, userCustomer.getCorpId());
                if (response != null && response.isSuccess()) {
                    weFlowerCustomerTagRelService.removeByCustomerIdAndUserId(userCustomer.getExternalUserid(), userCustomer.getUserId(), userCustomer.getCorpId(), removeTagList);
                }
                List<String> removeTagName = weTagService.list(new LambdaQueryWrapper<WeTag>().in(WeTag::getTagId,removeTagList)).stream().map(WeTag::getName).collect(Collectors.toList());
                recordBatchTag(userCustomer.getCorpId(),userCustomer.getUserId(),userCustomer.getExternalUserid(),LoginTokenService.getUsername(),removeTagName,CustomerTrajectoryEnums.TagType.BATCH_REMOVE_TAG.getType());
            } catch (ForestRuntimeException e) {
                log.error("获取客户数据失败 e:{}", ExceptionUtils.getStackTrace(e));
            }
        }

    }


    @Override
    public WeCustomerVO getCustomerByUserId(String externalUserid, String userId, String corpId) {
        List<WeCustomerVO> list = getCustomersByUserIdV2(externalUserid, userId, corpId);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_CUSTOMER_NOT_EXIST);
        }
        return list.get(0);
    }


    @Override
    public List<WeCustomerVO> getCustomersByUserIdV2(String externalUserid, String userId, String corpId) {
        if (StringUtils.isAnyBlank(externalUserid, userId, corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return weCustomerMapper.getCustomersByUserIdV2(externalUserid, userId, corpId);
    }

    @Override
    public void updateExternalContactV2(String corpId, String userId, String externalUserid) {
        if (StringUtils.isAnyBlank(corpId, userId, externalUserid)) {
            log.info("更新客户,参数缺失,corpId:{},userId:{},externalUserid:{}", corpId, userId, externalUserid);
            return;
        }
        // 1. 接口调用: 调用企微API 获取外部联系人详情
        GetExternalDetailResp resp = weCustomerClient.getV2(externalUserid, corpId);
        if (resp.isEmptyResult()) {
            return;
        }
        // 2. 数据处理: 根据返回数据构建与数据库交互的实体
        resp.handleData(corpId, userId);
        if (resp.getRemoteRel() != null) {
            weFlowerCustomerRelService.insert(resp.getRemoteRel());
        }
        WeFlowerCustomerRel localRel = weFlowerCustomerRelService.getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserid)
                .eq(WeFlowerCustomerRel::getUserId, userId)
                .last(GenConstants.LIMIT_1)
        );
        // 根据本地客户跟进人关系和返回标签组构建 客户标签关系实体
        List<WeFlowerCustomerTagRel> tagRelList = resp.getTagRelList(localRel);
        // 3. 数据更新：插入/更新数据库里的客户信息,员工客户关系
        this.insert(resp.getRemoteCustomer());
        // 4. 同步远端标签关系到本地
        weFlowerCustomerTagRelService.syncTagFromRemote(localRel, tagRelList);
    }


    /**
     * 调用企业微信接口发送好友欢迎语
     *
     * @param weWelcomeMsg 消息体
     * @param corpId       企业id
     */
    @Override
    public void sendWelcomeMsg(WeWelcomeMsg weWelcomeMsg, String corpId) {
        weCustomerClient.sendWelcomeMsg(weWelcomeMsg, corpId);
    }


    @Convert2Cipher
    @Override
    public WeCustomerPortrait findCustomerByOperUseridAndCustomerId(String externalUserid, String userid, String corpId) {
        if (StringUtils.isAnyBlank(externalUserid, userid, corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 查询客户详情
        WeCustomerVO customer = getCustomerByUserId(externalUserid, userid, corpId);
        if (customer == null) {
            throw new CustomException(ResultTip.TIP_CUSTOMER_NOT_EXIST);
        }
        // 转换成客户画像实体
        WeCustomerPortrait weCustomerPortrait = new WeCustomerPortrait(customer);
        // 设置年龄
        if (weCustomerPortrait.getBirthday() != null) {
            weCustomerPortrait.setAge(DateUtils.getAge(weCustomerPortrait.getBirthday()));
        }
        //获取当前客户拥有得标签
        weCustomerPortrait.setWeTagGroupList(
                weTagGroupService.findCustomerTagByFlowerCustomerRelId(weCustomerPortrait.getFlowerCustomerRelId())
        );
        //客户社交关系
        weCustomerPortrait.setSocialConn(
                this.baseMapper.countSocialConn(externalUserid, userid, corpId)
        );
        return weCustomerPortrait;
    }

    @Override
    @DataScope
    public List<WeCustomer> selectWeCustomerListNoRel(WeCustomerPushMessageDTO weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            log.error("获取客户列表失败，corpId不能为空");
            throw new CustomException("获取客户列表失败");
        }
        // 获取过滤员工/部门下的员工id列表
        List<String> filterUserIdList = getUserIdList(weCustomer.getFilterUsers(), weCustomer.getFilterDepartments(), weCustomer.getCorpId());
        // 不为空，从所属员工/部门下的员工id列表中移除需要过滤的员工
        if (CollectionUtils.isNotEmpty(filterUserIdList)) {
            // 获取过滤的员工下，所有添加的客户id
            List<WeFlowerCustomerRel> externalList = weFlowerCustomerRelService.list(new LambdaQueryWrapper<WeFlowerCustomerRel>().select(WeFlowerCustomerRel::getExternalUserid)
                    .eq(WeFlowerCustomerRel::getStatus, Constants.NORMAL_CODE)
                    .eq(WeFlowerCustomerRel::getCorpId, weCustomer.getCorpId())
                    .in(WeFlowerCustomerRel::getUserId, filterUserIdList)
                    .groupBy(WeFlowerCustomerRel::getExternalUserid));
            List<String> externalIdList = externalList.stream().map(WeFlowerCustomerRel::getExternalUserid).collect(Collectors.toList());
            weCustomer.setFilterExternalList(externalIdList);
        }
        // 获取所属员工/部门下的员工id列表
        List<String> userIdList = getUserIdList(weCustomer.getUserIds(), weCustomer.getDepartmentIds(), weCustomer.getCorpId());
        // 不为空
        if (CollectionUtils.isNotEmpty(userIdList)) {
            // 设置所属员工id条件
            weCustomer.setUserIds(StringUtils.join(userIdList, WeConstans.COMMA));
        }
        return weCustomerMapper.selectWeCustomerListNoRel(weCustomer);
    }

    /**
     * 根据部门和员工获取员工id列表
     *
     * @param users 员工id，用逗号隔开
     * @param departments 部门id，用逗号隔开
     * @param corpId 企业ID
     * @return 员工id列表
     */
    private List<String> getUserIdList(String users, String departments, String corpId) {
        if ((StringUtils.isBlank(users) && StringUtils.isBlank(departments)) || StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        // 员工id列表
        List<String> filterUserIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(users)) {
            filterUserIdList = new ArrayList<>(Arrays.asList(users.split(WeConstans.COMMA)));
        }
        if (StringUtils.isNotBlank(departments)) {
            filterUserIdList.addAll(weUserService.listOfUserId(corpId, departments.split(WeConstans.COMMA)));
        }
        return filterUserIdList;
    }

    @Override
    public List<CustomerSopVO> listOfCustomerIdAndUserId(String corpId, String userIds, @NotBlank List<String> customerIds) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return weCustomerMapper.listOfCustomerIdAndUserId(corpId, userIds, customerIds);
    }

    @Override
    public Integer customerCount(String corpId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.getBaseMapper().countCustomerNum(corpId);
    }

    @Override
    public QueryCustomerFromPlusVO getDetailByUserIdAndCustomerAvatar(String corpId, String userId, String avatar) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(corpId, userId, avatar)) {
            return null;
        }
        WeCustomer customer = getOne(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getCorpId, corpId)
                .eq(WeCustomer::getAvatar, avatar)
                .last(GenConstants.LIMIT_1)
        );
        if (customer == null) {
            throw new CustomException(ResultTip.TIP_CUSTOMER_NOT_EXIST);
        }
        QueryCustomerFromPlusVO vo = new QueryCustomerFromPlusVO();
        BeanUtils.copyPropertiesASM(customer, vo);
        // 查询员工-客户关联信息
        WeFlowerCustomerRel rel = weFlowerCustomerRelService.getOne(
                new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getExternalUserid, customer.getExternalUserid())
                        .eq(WeFlowerCustomerRel::getCorpId, corpId)
                        .eq(WeFlowerCustomerRel::getUserId, userId)
                        .last(GenConstants.LIMIT_1)
        );
        if (rel != null) {
            QueryCustomerFromPlusVO.FollowUserInfo followUserInfo = new QueryCustomerFromPlusVO().new FollowUserInfo();
            BeanUtils.copyPropertiesASM(rel, followUserInfo);
            vo.setFollowUserInfo(followUserInfo);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editByUserIdAndCustomerAvatar(EditCustomerFromPlusDTO dto) {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(dto.getCorpId(), dto.getAvatar(), dto.getUserId())) {
            return;
        }
        // 查询客户是否存在,并获取外部联系人id
        WeCustomer customer = getOne(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getCorpId, dto.getCorpId())
                .eq(WeCustomer::getAvatar, dto.getAvatar())
                .last(GenConstants.LIMIT_1)
        );
        if (customer == null) {
            throw new CustomException(ResultTip.TIP_CUSTOMER_NOT_EXIST);
        }
        // 参数格式转换
        WeCustomer model = new WeCustomer();
        BeanUtils.copyPropertiesASM(dto, model);
        model.setExternalUserid(customer.getExternalUserid());
        // 调用内部修改客户信息接口
        updateWeCustomerRemark(model);
    }

    @Override
    public void batchInsert(List<WeCustomer> customerList) {
        weCustomerMapper.batchInsert(customerList);
    }

    @Override
    public void insert(WeCustomer weCustomer) {
        List<WeCustomer> list = new ArrayList<>();
        list.add(weCustomer);
        this.batchInsert(list);
    }

    /**
     * 根据传入的字段获取对应的get方法，如name,对应的getName方法
     *
     * @param fieldName 字段名
     * @param person    对象
     * @return
     */
    private static Object getFieldValue(String fieldName, Object person) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = person.getClass().getMethod(getter);
            return method.invoke(person);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将DTO类转换为实体类
     *
     * @param weCustomerSearchDTO
     * @return
     */
    @Override
    public WeCustomer changeWecustomer(WeCustomerSearchDTO weCustomerSearchDTO) {
        //初始化添加列表
        WeFlowerCustomerRel weFlowerCustomerRel = new WeFlowerCustomerRel();
        if (weCustomerSearchDTO.getBeginTime() != null) {
            weFlowerCustomerRel.setBeginTime(weCustomerSearchDTO.getBeginTime().toString());
        }
        if (weCustomerSearchDTO.getEndTime() != null) {
            weFlowerCustomerRel.setEndTime(weCustomerSearchDTO.getEndTime().toString());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getAddWay())) {
            weFlowerCustomerRel.setAddWay(weCustomerSearchDTO.getAddWay());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getEmail())) {
            weFlowerCustomerRel.setEmail(weCustomerSearchDTO.getEmail());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getAddress())) {
            weFlowerCustomerRel.setAddress(weCustomerSearchDTO.getAddress());
        }
        List<WeFlowerCustomerRel> weFlowerCustomerRels = new ArrayList<>();
        weFlowerCustomerRels.add(weFlowerCustomerRel);
        // 转化实体类
        WeCustomer weCustomer = new WeCustomer();
        weCustomer.setCorpId(weCustomerSearchDTO.getCorpId());
        weCustomer.setExternalUserid(weCustomerSearchDTO.getExternalUserid());
        weCustomer.setStatus(weCustomerSearchDTO.getStatus());
        weCustomer.setWeFlowerCustomerRels(weFlowerCustomerRels);

        if (MapUtils.isNotEmpty(weCustomerSearchDTO.getParams())) {
            weCustomer.setParams(weCustomerSearchDTO.getParams());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getName())) {
            weCustomer.setName(weCustomerSearchDTO.getName());
            weCustomer.setRemark(weCustomerSearchDTO.getName());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getDesc())) {
            weCustomer.setDesc(weCustomerSearchDTO.getDesc());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getUserId())) {
            weCustomer.setUserId(weCustomerSearchDTO.getUserId());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getUserIds())) {
            weCustomer.setUserIds(weCustomerSearchDTO.getUserIds());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getTagIds())) {
            weCustomer.setTagIds(weCustomerSearchDTO.getTagIds());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getGender())) {
            weCustomer.setGender(weCustomerSearchDTO.getGender());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getCorpFullName())) {
            weCustomer.setCorpFullName(weCustomerSearchDTO.getCorpFullName());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getBirthday())) {
            weCustomer.setBirthdayStr(weCustomerSearchDTO.getBirthday());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getPhone())) {
            weCustomer.setPhone(weCustomerSearchDTO.getPhone());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getBeginTime())) {
            weCustomer.setBeginTime(weCustomerSearchDTO.getBeginTime());
        }
        if (!StringUtils.isBlank(weCustomerSearchDTO.getEndTime())) {
            weCustomer.setEndTime(weCustomerSearchDTO.getEndTime());
        }
        if (weCustomerSearchDTO.getExtendProperties().size() > 0) {
            weCustomer.setExtendProperties(weCustomerSearchDTO.getExtendProperties());
        }
        return weCustomer;
    }

    /**
     * 查询导出的列表
     *
     * @param weCustomer       客户条件
     * @param selectProperties 需要导出的扩展属性
     * @param extendPropHolder 扩展字段属性容器 {@link ExtendPropHolder}
     * @return 需要导出的客户
     */
    public List<WeCustomerExportVO> selectExportCustomer(WeCustomer weCustomer, List<String> selectProperties, ExtendPropHolder extendPropHolder) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<WeCustomerVO> list = weCustomerMapper.selectWeCustomerV3(weCustomer);
        // 根据返回的结果获取需要的标签详情
        CompletableFuture<Void> tagFuture = CompletableFuture.runAsync(
                () -> weFlowerCustomerTagRelService.setTagForCustomers(weCustomer.getCorpId(), list), threadPoolTaskExecutor
        );
        // 根据返回的结果获取需要的扩展字段
        CompletableFuture<Void> extendPropFuture = CompletableFuture.runAsync(
                () -> weCustomerExtendPropertyService.setExtendPropertyForCustomers(weCustomer.getCorpId(), list), threadPoolTaskExecutor
        );
        CompletableFuture.allOf(tagFuture, extendPropFuture).join();

//        List<WeCustomerVO> list = weCustomerMapper.selectExportCustomer(weCustomer);
//        List<WeCustomerVO> list  = weCustomerMapper.selectWeCustomerListV2(weCustomer) ;
        List<WeCustomerExportVO> exportList = list.stream().map(WeCustomerExportVO::new).collect(Collectors.toList());
        weCustomerExtendPropertyService.setKeyValueMapper(weCustomer.getCorpId(), exportList, selectProperties, extendPropHolder);
        return exportList;
    }

    @DataScope
    @Override
    public List<WeCustomerVO> selectWeCustomerListV3(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        String corpId = weCustomer.getCorpId();
        if (!hasFilterCustomer(weCustomer, corpId)) {
            return Collections.emptyList();
        }
        // 查询客户
        PageInfoUtil.startPage();
        List<WeCustomerVO> list = weCustomerMapper.selectWeCustomerV3(weCustomer);
        // 根据返回的结果获取需要的标签详情
        CompletableFuture<Void> tagFuture = CompletableFuture.runAsync(
                () -> weFlowerCustomerTagRelService.setTagForCustomers(corpId, list), threadPoolTaskExecutor
        );
        // 根据返回的结果获取需要的扩展字段
        CompletableFuture<Void> extendPropFuture = CompletableFuture.runAsync(
                () -> weCustomerExtendPropertyService.setExtendPropertyForCustomers(corpId, list), threadPoolTaskExecutor
        );
        CompletableFuture.allOf(tagFuture, extendPropFuture).join();
        return list;
    }

    @Override
    @DataScope
    @Deprecated
    public <T> AjaxResult<T> export(WeCustomerExportDTO dto) {
        List<String> selectProperties = dto.getSelectedProperties();
        WeCustomerSearchDTO weCustomerSearchDTO = new WeCustomerSearchDTO();
        BeanUtils.copyProperties(dto, weCustomerSearchDTO);
        WeCustomer weCustomer = changeWecustomer(weCustomerSearchDTO);
        List<WeCustomerVO> list = this.selectWeCustomerListV3(weCustomer);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_NO_DATA_TO_EXPORT);
        }
        List<WeCustomerExportVO> exportList = list.stream().map(WeCustomerExportVO::new).collect(Collectors.toList());
        ExtendPropHolder extendPropHolder = new ExtendPropHolder(dto.getCorpId(), dto.getSelectedProperties());
        weCustomerExtendPropertyService.setKeyValueMapper(weCustomer.getCorpId(), exportList, selectProperties, extendPropHolder);
        ExcelUtil<WeCustomerExportVO> util = new ExcelUtil<>(WeCustomerExportVO.class);
        return util.exportExcelV2(exportList, "customer", selectProperties);
    }

    @Override
    @DataScope
    public void genExportData(WeCustomerExportDTO dto, String oprId, String fileName) {
        WeCustomerSearchDTO weCustomerSearchDTO = new WeCustomerSearchDTO();
        List<String> selectProperties = dto.getSelectedProperties();
        BeanUtils.copyProperties(dto, weCustomerSearchDTO);
        // 获取导出客户的条件
        WeCustomer weCustomer = changeWecustomer(weCustomerSearchDTO);
        // 分批导出客户
        OutputStream outputStream = null;
        try {
            outputStream = Files.newOutputStream(Paths.get(getAbsoluteFile(fileName)));
            Stopwatch stopwatch = Stopwatch.createStarted();
            ExcelUtil<WeCustomerExportVO> util = new ExcelUtil<>(WeCustomerExportVO.class, selectProperties);
            List<List<String>> fields = util.getFields();
            Integer totalCount = weCustomerMapper.selectWeCustomerCount(weCustomer);
            //每一个Sheet存放数据
            Integer sheetDataRows = EXPORT_SHEET_ROWS;
            //每次写入的数据量
            int writeDataRows = EXPORT_QUERY_BATCH_SIZE;
            //计算需要的Sheet数量
            int sheetNum = totalCount % sheetDataRows == 0 ? (totalCount / sheetDataRows) : (totalCount / sheetDataRows + 1);
            //计算一般情况下每一个Sheet需要写入的次数(一般情况不包含后一个sheet,因为后一个sheet不确定会写入多少条数据)
            int oneSheetWriteCount = sheetDataRows / writeDataRows;
            //计算后一个sheet需要写入的次数
            int lastSheetWriteCount = totalCount % sheetDataRows == 0
                    ? oneSheetWriteCount
                    : ((totalCount - (totalCount / sheetDataRows) * sheetDataRows) % writeDataRows == 0 ? (totalCount - (totalCount / sheetDataRows) * sheetDataRows) / writeDataRows : ((totalCount - (totalCount / sheetDataRows) * sheetDataRows) / writeDataRows + 1));            //开始分批查询分次写入
            //注意这次的循环就需要进行嵌套循环了,外层循环是Sheet数目,内层循环是写入次数
            List<List<String>> dataList = new ArrayList<>();
            //todo 待封装 样式
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = getHorizontalCellStyleStrategy();
            ExcelWriter excelWriter = EasyExcel.write(outputStream)
                                               .excludeColumnFiledNames(Arrays.asList("extendPropMapper", "extendProperties", "weFlowerCustomerRels", "extendList", "relIds", "params"))
                                               .registerWriteHandler(horizontalCellStyleStrategy)
                                               .registerWriteHandler(new SimpleColumnWidthStyleStrategy(25))
                                               .build();
            ExtendPropHolder extendPropHolder = new ExtendPropHolder(dto.getCorpId(), selectProperties);
            for (int i = 0; i < sheetNum; i++) {
                //创建Sheet
                //循环写入次数: j的自增条件是当不是后一个Sheet的时候写入次数为正常的每个Sheet写入的次数,如果是后一个就需要使用计算的次数lastSheetWriteCount
                for (int j = 0; j < (i != sheetNum - 1 ? oneSheetWriteCount : lastSheetWriteCount); j++) {
                    dataList.clear();
                    //分页查询
                    PageHelper.startPage(j + 1 + oneSheetWriteCount * i, writeDataRows, false);
                    List<WeCustomerExportVO> resultList = selectExportCustomer(weCustomer, selectProperties, extendPropHolder);
                    WriteSheet writeSheet = EasyExcel.writerSheet(i, "客户" + (i + 1)).head(fields).build();
                    excelWriter.write(getExportDataBySelectedProp(resultList, selectProperties, extendPropHolder), writeSheet);
                }
            }
            // 下载EXCEL
            excelWriter.finish();
            outputStream.flush();
            //导出时间结束
            stopwatch.stop();
            // 获取经过的时间并以毫秒为单位打印出来
            long elapsedTimeMillis = stopwatch.elapsed(TimeUnit.SECONDS);
            log.info("[导出客户] 耗时：{} 秒 ,总量:{} , oprId:{} ", elapsedTimeMillis,totalCount,oprId);
            // 设置导出完成
            customerRedisCache.setExportFinished(oprId);
        } catch (Exception e) {
            log.error("导出客户异常.e:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("[导出客户]关闭流异常,orpId:{}", oprId);
                }
            }
        }
    }

    /**
     * 获取样式
     * @return
     */
    private HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteFont.setBold(true);
        headWriteFont.setColor(IndexedColors.WHITE.getIndex());
        headWriteFont.setFontName("Arial");
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
//            contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

        // 背景绿色
//            contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("Arial");
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 10);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }


    /**
     * 根据自定义字段和数据获取需要导出的最终数据
     *
     * @param resultList       结果数据
     * @param selectProperties 需要导出的表头
     * @param extendPropHolder
     * @return
     */
    private List getExportDataBySelectedProp(List<WeCustomerExportVO> resultList, List<String> selectProperties, ExtendPropHolder extendPropHolder) {
        List list = new ArrayList();
        for (WeCustomerExportVO element : resultList) {
            List<Object> rowData = new ArrayList<>();
            for (String propName : selectProperties) {
                Class<?> currClazz = element.getClass();
                List<Field> fields = new ArrayList<>();
                while (currClazz != null) {
                    Field[] declaredFields = currClazz.getDeclaredFields();
                    fields.addAll(Arrays.asList(declaredFields));
                    currClazz = currClazz.getSuperclass();
                }
                for (Field field : fields) {
                    Excel excel = field.getDeclaredAnnotation(Excel.class);
                    if (excel != null) {
                        // 判断基础字段
                        if (propName.equals(excel.name())) {
                            // 在这里，你可以获取字段的值并将其添加到 rowData 中
                            try {
                                if (!field.isAccessible()) {
                                    field.setAccessible(true);
                                }
                                Object fieldValue = formatValue(element, field, excel);
                                rowData.add(fieldValue);
                                continue;
                            } catch (Exception e) {
                                log.error("设置字段值异常 e;{}", ExceptionUtils.getStackTrace(e));
                            }
                        }
                    }
                }
                if (extendPropHolder.isHasExtendProp()) {
                    for (Map.Entry<String, String> entry : element.getExtendPropMapper().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (key.equals(propName)) {
                            rowData.add(value);
                        }
                    }
                }
            }
            list.add(rowData);
        }
        return list;
    }

    /**
     * 根据注解格式化设置字段的值
     *
     * @param element
     * @param field
     * @param excel
     * @return
     * @throws IllegalAccessException
     */
    private Object formatValue(WeCustomerExportVO element, Field field, Excel excel) throws IllegalAccessException {
        Object fieldValue = field.get(element);
        String dateFormat = excel.dateFormat();
        String readConverterExp = excel.readConverterExp();
        String separator = excel.separator();
        String dictType = excel.dictType();
        if (com.easyink.common.utils.StringUtils.isNotEmpty(dateFormat) && com.easyink.common.utils.StringUtils.isNotNull(fieldValue)) {
            fieldValue = DateUtils.parseDateToStr(dateFormat, (Date) fieldValue);
        } else if (com.easyink.common.utils.StringUtils.isNotEmpty(readConverterExp) && com.easyink.common.utils.StringUtils.isNotNull(fieldValue)) {
            fieldValue = ExcelUtil.convertByExp(Convert.toStr(fieldValue), readConverterExp, separator);
        } else if (com.easyink.common.utils.StringUtils.isNotEmpty(dictType) && com.easyink.common.utils.StringUtils.isNotNull(fieldValue)) {
            fieldValue = ExcelUtil.convertDictByExp(Convert.toStr(fieldValue), dictType, separator);
        } else if (fieldValue instanceof BigDecimal && -1 != excel.scale()) {
            fieldValue = (((BigDecimal) fieldValue).setScale(excel.scale(), excel.roundingMode())).toString();
        }
        return fieldValue;
    }

    @Override
    public Boolean getExportResult(String oprId) {
        return customerRedisCache.hasExportFinished(oprId);
    }

    @Override
    @DataScope
    public  WeCustomerExportDTO transferData(WeCustomerExportDTO dto) {
        return dto;
    }

    public String getAbsoluteFile(String filename) {
        String downloadPath = RuoYiConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }


    /**
     * 模糊查询客户 (无需登录可用)
     *
     * @param corpId
     * @param customerName
     * @return
     */
    @Override
    public List<WeCustomerVO> getCustomer(String corpId, String customerName) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        return this.baseMapper.listCustomers(customerName, corpId);
    }

    @Override
    public GetUnionIdVO getDetailByExternalUserId(GetUnionIdDTO getUnionIdDTO) {
        if(getUnionIdDTO == null || StringUtils.isAnyBlank(getUnionIdDTO.getCorpId(),getUnionIdDTO.getExternalUserId(), getUnionIdDTO.getCorpSecret())) {
            throw new CustomException(ResultTip.TIP_PARAM_NAME_MISSING);
        }
        String corpSecret = corpSecretDecryptUtil.decryptUnionId(getUnionIdDTO.getCorpSecret()) ;
        ExternalUserDetail userDetail = weUnionIdClient.getByExternalUserId(getUnionIdDTO.getExternalUserId(), getUnionIdDTO.getCorpId(), corpSecret);
        if(userDetail == null || userDetail.getExternal_contact() == null ) {
            throw new CustomException(ResultTip.TIP_GET_UNION_ID_FAIL);
        }
        return new GetUnionIdVO(userDetail.getExternal_contact());
    }

    /**
     * 根据openId获取客户详情
     *
     * @param openId 公众号openid
     * @param corpId
     * @return 客户详情 {@link WeCustomer}
     */
    @Override
    public WeCustomer getCustomerInfoByOpenId(String openId, String corpId) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        // 1. 根据openid获取unionId
        String unionId = wechatOpenService.getUnionIdByOpenId(openId);
        //  2. 先根据union_id去客户表查询是否有数据
        WeCustomer customer = getCustomerByUnionId(unionId, openId, corpId);
        if (customer == null) {
            log.info("[根据openId获取客户详情] 获取客户详情,根据union_id在数据库中未匹配到客户,openId:{},unionId:{}", openId, unionId);
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_USER_BY_UNION_ID);
        }
        return customer;
    }

    @Override
    public WeCustomer getCustomerByUnionId(String unionId, String openId, String corpId) {
        if (StringUtils.isAnyBlank(unionId, openId, corpId)) {
            return null;
        }
        WeCustomer customer = this.getOne(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getUnionid, unionId)
                .eq(WeCustomer::getCorpId, corpId)
                .last(GenConstants.LIMIT_1));
        // 如果在数据库中没查到客户信息, 且当前环境为待开发 则调用接口获取externalUserId
        if (customer == null && ServerTypeEnum.THIRD.getType().equals(we3rdAppService.getServerType().getServerType())) {
            ExternalUserDetail externalUserDetail = weCustomerClient.getExternalUserIdByUnionIdAndOpenId(unionId, openId, SubjectTypeEnum.ENTERPRISE.getCode(), corpId);
            if (externalUserDetail == null || StringUtils.isBlank(externalUserDetail.getExternal_userid())) {
                return null;
            }
            // 若正常返回exteranlUserId再查找客户信息，并将unionId写入到到数据库中
            customer = this.getOne(new LambdaQueryWrapper<WeCustomer>()
                    .eq(WeCustomer::getCorpId, corpId)
                    .eq(WeCustomer::getExternalUserid, externalUserDetail.getExternal_userid())
                    .last(GenConstants.LIMIT_1));
            if (customer == null) {
                return null;
            }
            customer.setUnionid(unionId);
            this.update(new LambdaUpdateWrapper<WeCustomer>()
                    .eq(WeCustomer::getExternalUserid, externalUserDetail.getExternal_userid())
                    .eq(WeCustomer::getCorpId, corpId)
                    .set(WeCustomer::getUnionid, unionId));
        }
        return customer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getOpenExUserId(String corpId, String externalUserId) {
        if (StringUtils.isAnyBlank(corpId, externalUserId)) {
            return StringUtils.EMPTY;
        }
        WeExternalUseridMapping weExternalUseridMapping = weExternalUseridMappingMapper.selectOne(new LambdaUpdateWrapper<WeExternalUseridMapping>()
                .eq(WeExternalUseridMapping::getCorpId, corpId)
                .eq(WeExternalUseridMapping::getExternalUserid, externalUserId)
                .last(GenConstants.LIMIT_1));
        if (weExternalUseridMapping == null) {
            String openExUserId = getOpenExUserIdByClient(corpId, externalUserId);
            weExternalUseridMapping = new WeExternalUseridMapping(corpId, externalUserId, openExUserId);
            weExternalUseridMappingMapper.insertOrUpdate(weExternalUseridMapping);
        }
        return weExternalUseridMapping.getOpenExternalUserid();
    }


    /**
     * 通过接口将外部联系人exUserId转化为密文
     *
     * @param corpId            企业id
     * @param externalUserId    外部联系人exUserId
     * @return
     */
    protected String getOpenExUserIdByClient(String corpId, String externalUserId) {
        List<String> externalUserIds = new ArrayList<>();
        externalUserIds.add(externalUserId);
        final CorpIdToOpenCorpIdResp newExternalUser = convertIDClient.getNewExternalUserid(corpId, externalUserIds);
        Map<String,String> openExternalUserIdMap = newExternalUser.getItems().stream().collect(Collectors.toMap(CorpIdToOpenCorpIdResp.ExternalUserMapping::getExternal_userid, CorpIdToOpenCorpIdResp.ExternalUserMapping::getNew_external_userid));
        return openExternalUserIdMap.getOrDefault(externalUserId, com.easyink.common.utils.StringUtils.EMPTY);
    }

}
