package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easywecom.common.annotation.DataScope;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.exception.wecom.WeComException;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.common.utils.poi.ExcelUtil;
import com.easywecom.common.utils.sql.BatchInsertUtil;
import com.easywecom.wecom.client.WeCustomerClient;
import com.easywecom.wecom.client.WeUserClient;
import com.easywecom.wecom.domain.*;
import com.easywecom.wecom.domain.dto.*;
import com.easywecom.wecom.domain.dto.customer.CustomerTagEdit;
import com.easywecom.wecom.domain.dto.customer.EditCustomerDTO;
import com.easywecom.wecom.domain.dto.customer.GetExternalDetailResp;
import com.easywecom.wecom.domain.dto.customer.req.GetByUserReq;
import com.easywecom.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easywecom.wecom.domain.dto.customersop.Column;
import com.easywecom.wecom.domain.dto.pro.EditCustomerFromPlusDTO;
import com.easywecom.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easywecom.wecom.domain.entity.WeCustomerExportDTO;
import com.easywecom.wecom.domain.vo.QueryCustomerFromPlusVO;
import com.easywecom.wecom.domain.vo.WeCustomerExportVO;
import com.easywecom.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerSumVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerVO;
import com.easywecom.wecom.domain.vo.sop.CustomerSopVO;
import com.easywecom.wecom.mapper.WeCustomerMapper;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.*;
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
        // 4.对客户打标签
        if (dto.getEditTag() != null) {
            // 去除原有标签
            weFlowerCustomerTagRelService.removeByCustomerIdAndUserId(externalUserId, userId, corpId);
            // 批量打上新的标签
            if (!dto.getEditTag().isEmpty()) {
                this.batchMarkCustomTag(
                        WeMakeCustomerTagVO.builder()
                                .corpId(corpId)
                                .externalUserid(externalUserId)
                                .userId(userId)
                                .addTag(dto.getEditTag())
                                .build()
                );
            }
        }
        // 5.添加轨迹内容(信息动态)
        weCustomerTrajectoryService.recordEditOperation(dto);
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


    @Override
    @DataScope
    public List<WeCustomerVO> selectWeCustomerListV2(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return weCustomerMapper.selectWeCustomerListV2(weCustomer);
    }

    @Override
    public List<WeCustomer> listOfUseCustomer(String corpId, WeOperationsCenterCustomerSopFilterEntity sopFilter) {
        WeCustomerPushMessageDTO weCustomerPushMessageDTO = new WeCustomerPushMessageDTO();
        weCustomerPushMessageDTO.setCorpId(corpId);
        //-1表示筛选条件未选择性别 选择性别的情况下添加过滤条件
        if (!Integer.valueOf(-1).equals(sopFilter.getGender())) {
            weCustomerPushMessageDTO.setGender(sopFilter.getGender());
        }
        weCustomerPushMessageDTO.setTagIds(sopFilter.getTagId());
        weCustomerPushMessageDTO.setCustomerStartTime(sopFilter.getStartTime());
        weCustomerPushMessageDTO.setCustomerEndTime(sopFilter.getEndTime());
        weCustomerPushMessageDTO.setUserIds(sopFilter.getUsers());
        weCustomerPushMessageDTO.setFilterTags(sopFilter.getFilterTagId());
        //普通属性查询客户
        List<WeCustomer> weCustomers = selectWeCustomerListNoRel(weCustomerPushMessageDTO);
        if (StringUtils.isNotEmpty(sopFilter.getCloumnInfo())) {
            List<Column> columns = JSONArray.parseArray(sopFilter.getCloumnInfo(), Column.class);
            if (CollectionUtils.isNotEmpty(columns)) {
                //自定义属性查询客户
                List<String> customers = weCustomerExtendPropertyRelService.listOfPropertyIdAndValue(columns);
                //求两个集合交集
                weCustomers = weCustomers.stream().filter(customer -> customers.contains(customer.getExternalUserid())).collect(Collectors.toList());
            }
        }
        return weCustomers;
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
        List<WeCustomerVO> list = this.selectWeCustomerListV2(weCustomer);
        // 根据externalUserId去重
        Set<String> set = list.stream().map(WeCustomerVO::getExternalUserid).collect(Collectors.toSet());
        return WeCustomerSumVO.builder()
                .totalCount(list.size())
                .ignoreDuplicateCount(set.size())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            batchGetCustomerDetailAndSyncLocal(corpId, userId);
        }
        long endTime = System.currentTimeMillis();
        log.info("同步客户结束[新]:corpId:{}耗时：{}", corpId, Double.valueOf((endTime - startTime) / 1000.00D));
        // 3.客户信息同步后,刷新数据概览页客户数据
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
        // 1. API请求:请求企微[批量获取客户详情]接口
        GetByUserReq req = new GetByUserReq(userId);
        GetByUserResp resp = (GetByUserResp) req.executeTillNoNextPage(corpId);

        // 2. 数据处理:对返回的数据进行处理
        resp.handleData(corpId);
        if (resp.isEmptyResult()) {
            return;
        }
        // 3. 数据对齐:本地成员-客户关系数据与服务端对齐,同步远端修改的数据
        weFlowerCustomerRelService.alignData(resp, userId, corpId);
        List<String> externalUserIdList = resp.getCustomerList().stream().map(WeCustomer::getExternalUserid).collect(Collectors.toList());
        List<WeFlowerCustomerRel> localRelList = weFlowerCustomerRelService.list(
                new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getCorpId, corpId)
                        .eq(WeFlowerCustomerRel::getUserId, userId)
                        .in(WeFlowerCustomerRel::getExternalUserid, externalUserIdList)
        );
        // 对以前删除但是重新加回的客户重新把状态设置为正常
        resp.activateDelCustomer(localRelList);
        //**** 客户信息更新、插入 , 客户-成员关系更新 , 客户-标签关系更新
        // 4. 数据同步:插入、更新 客户数据,员工-客户关系
        BatchInsertUtil.doInsert(resp.getCustomerList(), this::batchInsert);
        BatchInsertUtil.doInsert(resp.getRelList(), list -> weFlowerCustomerRelService.batchInsert(list));

        // 5. 数据同步: 客户-标签关系 ,获取每个客户关系对应的标签,并同步更新数据库
        List<WeFlowerCustomerTagRel> tagRelList = resp.getCustomerTagRelList(localRelList);
        BatchInsertUtil.doInsert(tagRelList, list -> weFlowerCustomerTagRelService.batchInsert(list));
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
     * 客户打标签
     *
     * @param weMakeCustomerTag 客户打标签实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeLabel(WeMakeCustomerTagVO weMakeCustomerTag) {
        if (StringUtils.isBlank(weMakeCustomerTag.getCorpId())) {
            log.error("打标签失败失败，corpId不能为空");
            throw new CustomException("打标签失败");
        }
        List<CustomerTagEdit> customerTagEdits = getCustomerTagEdits(weMakeCustomerTag);
        if (CollUtil.isNotEmpty(customerTagEdits)) {
            //企微新接口
            customerTagEdits.forEach(tagEdit -> weCustomerClient.makeCustomerLabel(tagEdit, weMakeCustomerTag.getCorpId()));
        }
    }


    /**
     * 客户批量打标签
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeLabelbatch(List<WeMakeCustomerTagVO> list, String updateBy) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (WeMakeCustomerTagVO item : list) {
            this.batchMarkCustomTag(item);
            // 有操作人才需要记录信息动态
            if (StringUtils.isNotBlank(updateBy)) {
                weCustomerTrajectoryService.recordEditTagOperation(item.getCorpId(), item.getUserId(), item.getExternalUserid(), updateBy, item.getAddTag());
            }

        }

    }

    @Override
    public void makeLabelbatch(List<WeMakeCustomerTagVO> weMakeCustomerTagVOS) {
        this.makeLabelbatch(weMakeCustomerTagVOS, null);
    }

    /**
     * 打标签方法抽取
     *
     * @param weMakeCustomerTag 客户打标签实体
     * @return
     */
    @Transactional
    public List<CustomerTagEdit> getCustomerTagEdits(WeMakeCustomerTagVO weMakeCustomerTag) {
        if (StringUtils.isBlank(weMakeCustomerTag.getCorpId())) {
            log.error("打标签失败失败，corpId不能为空");
            throw new CustomException("打标签失败");
        }
        List<CustomerTagEdit> customerTagEdits = new ArrayList<>();
        //查询出当前用户对应的
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getOne(weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId());
        if (flowerCustomerRel != null) {
            List<WeTag> addTags = weMakeCustomerTag.getAddTag();
            //移除所有标签
            this.removeAllLabel(flowerCustomerRel);
            if (CollUtil.isNotEmpty(addTags)) {
                addTags.removeAll(Collections.singleton(null));
                List<WeFlowerCustomerTagRel> tagRels = new ArrayList<>();
                //官方接口参数
                CustomerTagEdit customerTagEdit = CustomerTagEdit.builder()
                        .userid(flowerCustomerRel.getUserId())
                        .external_userid(flowerCustomerRel.getExternalUserid())
                        .build();
                List<String> tags = new ArrayList<>();
                for (WeTag tag : addTags) {
                    tags.add(tag.getTagId());
                    //本地标签关系
                    tagRels.add(
                            WeFlowerCustomerTagRel.builder()
                                    .flowerCustomerRelId(flowerCustomerRel.getId())
                                    .externalUserid(flowerCustomerRel.getExternalUserid())
                                    .tagId(tag.getTagId())
                                    .createTime(new Date())
                                    .build()
                    );
                }

                customerTagEdit.setAdd_tag(ArrayUtil.toArray(tags, String.class));
                customerTagEdits.add(customerTagEdit);
                //保存或更新本地
                if (CollUtil.isNotEmpty(tagRels)) {
                    weFlowerCustomerTagRelService.saveOrUpdateBatch(tagRels);
                }
            }
            // 记录客户被打标签的信息动态
            weCustomerTrajectoryService.recordEditTagOperation(weMakeCustomerTag.getCorpId(), weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(),
                    weMakeCustomerTag.getUpdateBy(), weMakeCustomerTag.getAddTag());
        }
        return customerTagEdits;
    }

    /**
     * 增量式打标签
     *
     * @param weMakeCustomerTag
     * @return
     */
    private void batchMarkCustomTag(WeMakeCustomerTagVO weMakeCustomerTag) {
        if (StringUtils.isAnyBlank(weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId())) {
            log.error("员工id，客户id，公司id不能为空，userId：{}，externalUserId：{}，corpId：{}", weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId());
            throw new CustomException("增量式打标签错误");
        }
        //增量标签
        List<WeTag> addTags = weMakeCustomerTag.getAddTag();
        if (CollUtil.isEmpty(addTags)) {
            return;
        }
        log.info("需要打的标签列表: {}", addTags.stream().map(WeTag::getTagId).collect(Collectors.toList()));

        //查询出当前用户对应的
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getOne(weMakeCustomerTag.getUserId(), weMakeCustomerTag.getExternalUserid(), weMakeCustomerTag.getCorpId());

        if (flowerCustomerRel == null) {
            return;
        }
        //去除空参数
        addTags.removeAll(Collections.singleton(null));
        if (CollUtil.isEmpty(addTags)) {
            return;
        }

        //获取已存在的标签
        LambdaQueryWrapper<WeFlowerCustomerTagRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, flowerCustomerRel.getId());
        List<WeFlowerCustomerTagRel> weFlowerCustomerTagRelList = weFlowerCustomerTagRelService.list(queryWrapper);
        Set<String> existTagSet = weFlowerCustomerTagRelList.stream().map(WeFlowerCustomerTagRel::getTagId).collect(Collectors.toSet());

        List<WeFlowerCustomerTagRel> tagRels = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (WeTag tag : addTags) {
            //如果标签已存在则不需要重新保存
            if (existTagSet.contains(tag.getTagId())) {
                continue;
            }
            tags.add(tag.getTagId());
            //本地标签关系
            tagRels.add(
                    WeFlowerCustomerTagRel.builder()
                            .flowerCustomerRelId(flowerCustomerRel.getId())
                            .externalUserid(flowerCustomerRel.getExternalUserid())
                            .tagId(tag.getTagId())
                            .createTime(new Date())
                            .build()
            );
        }
        //保存或更新本地
        if (CollUtil.isNotEmpty(tagRels)) {
            weFlowerCustomerTagRelService.saveOrUpdateBatch(tagRels);
        }

        //官方接口参数
        log.info("可打的标签id列表: {}", tags);
        if (CollUtil.isNotEmpty(tags)) {
            CustomerTagEdit customerTagEdit = CustomerTagEdit.builder()
                    .userid(flowerCustomerRel.getUserId())
                    .external_userid(flowerCustomerRel.getExternalUserid())
                    .build();
            customerTagEdit.setAdd_tag(ArrayUtil.toArray(tags, String.class));
            //企微新接口
            weCustomerClient.makeCustomerLabel(customerTagEdit, weMakeCustomerTag.getCorpId());
        }

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
    public void removeLabel(String corpId, String externalUserid, String userid, List<String> delIdList) {
        RemoveWeCustomerTagDTO dto = new RemoveWeCustomerTagDTO();
        List<RemoveWeCustomerTagDTO.WeUserCustomer> list = new ArrayList<>();
        list.add(new RemoveWeCustomerTagDTO.WeUserCustomer(externalUserid, userid, corpId));
        dto.setCustomerList(list);
        dto.setWeTagIdList(delIdList);
        this.removeLabel(dto);
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
    @Transactional(rollbackFor = Exception.class)
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

        // 4. 数据更新：清除所有旧的标签关系,插入当前标签关系
        weFlowerCustomerTagRelService.remove(new LambdaQueryWrapper<WeFlowerCustomerTagRel>()
                .eq(WeFlowerCustomerTagRel::getFlowerCustomerRelId, localRel.getId())
        );
        if (CollectionUtils.isNotEmpty(tagRelList)) {
            BatchInsertUtil.doInsert(tagRelList, list -> weFlowerCustomerTagRelService.batchInsert(list));
        }
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
        return weCustomerMapper.selectWeCustomerListNoRel(weCustomer);
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

    @Override
    public <T> AjaxResult<T> export(WeCustomerExportDTO dto) {
        WeCustomer weCustomer = new WeCustomer();
        BeanUtils.copyProperties(dto, weCustomer);
        List<WeCustomerVO> list = this.selectWeCustomerListV2(weCustomer);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_NO_DATA_TO_EXPORT);
        }
        List<WeCustomerExportVO> exportList = list.stream().map(WeCustomerExportVO::new).collect(Collectors.toList());
        weCustomerExtendPropertyService.setKeyValueMapper(weCustomer.getCorpId(), exportList, dto.getSelectedProperties());
        ExcelUtil<WeCustomerExportVO> util = new ExcelUtil<>(WeCustomerExportVO.class);
        return util.exportExcelV2(exportList, "customer", dto.getSelectedProperties());
    }

}
