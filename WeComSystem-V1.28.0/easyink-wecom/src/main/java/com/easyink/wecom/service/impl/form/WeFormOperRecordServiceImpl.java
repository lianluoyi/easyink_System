package com.easyink.wecom.service.impl.form;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.constant.form.FormConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.SysUser;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.service.ISysUserService;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeCustomerTrajectory;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.form.FormCommitDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.entity.form.WeFormOperRecord;
import com.easyink.wecom.domain.enums.form.FormOperEnum;
import com.easyink.wecom.domain.model.form.CustomerLabelSettingModel;
import com.easyink.wecom.domain.model.form.FormResultModel;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordExportVO;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordVO;
import com.easyink.wecom.domain.vo.form.FormOperRecordDetailVO;
import com.easyink.wecom.domain.vo.form.FormUserSendRecordVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerTrajectoryMapper;
import com.easyink.wecom.mapper.form.WeFormMapper;
import com.easyink.wecom.mapper.form.WeFormOperRecordMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.service.form.WeFormCustomerFeedbackService;
import com.easyink.wecom.service.form.WeFormOperRecordService;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static com.easyink.common.utils.SecurityUtils.isAdmin;

/**
 * 智能表单操作记录表(WeFormOperRecord)表服务实现类
 *
 * @author wx
 * @since 2023-01-13 11:49:48
 */
@Service("weFormOperRecordService")
@RequiredArgsConstructor
@Slf4j
public class WeFormOperRecordServiceImpl extends ServiceImpl<WeFormOperRecordMapper, WeFormOperRecord> implements WeFormOperRecordService {

    private final WeCustomerService weCustomerService;
    private final WeUserService weUserService;
    private final WeFormOperRecordMapper weFormOperRecordMapper;
    private final WeFormMapper weFormMapper;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeMessagePushClient weMessagePushClient;
    private final WeCorpAccountService weCorpAccountService;
    private final WeFormCustomerFeedbackService weFormCustomerFeedbackService;
    private final ISysUserService iSysUserService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WechatOpenService wechatOpenService;

    @Autowired
    private WeCustomerTrajectoryMapper weCustomerTrajectoryMapper;

    @Autowired
    private WeTagService weTagService;

    @Override
    @Async(value = "formTaskExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void syncAddClickRecord(Long recordId, Long formId, String userId, String openId, WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, Integer channelType) {
        if (recordId == null || StringUtils.isBlank(openId)) {
            log.info("[异步智能表单-点击记录处理] 参数为空, recordId:{}, formId:{}, userId:{}, openId:{}", recordId, formId, userId, openId);
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeFormOperRecord weFormOperRecord = WeFormOperRecord.builder().id(recordId)
                .formId(formId)
                .userId(userId)
                .channelType(channelType)
                .openId(openId)
                .createTime(DateUtils.getNowDate())
                .commitFlag(false)
                .build();
        // 1. 填写发送表单的员工信息
        WeUser userDetail = null;
        if (isAdmin(userId)) {
            SysUser sysUser = iSysUserService.selectUserByUserName(userId);
            weFormOperRecord.setUserName(sysUser.getUserName());
            weFormOperRecord.setUserHeadImage(sysUser.getAvatar());
        } else {
            userDetail = weUserService.getUserDetail(weForm.getCorpId(), userId);
            if (userDetail == null) {
                log.error("[异步智能表单-点击记录处理] 获取员工信息错误, formId:{}, openId:{}, userId:{}", formId, openId, userId);
                throw new CustomException(ResultTip.TIP_USER_NOT_ACTIVE);
            }
            weFormOperRecord.setUserName(userDetail.getName());
            weFormOperRecord.setUserHeadImage(userDetail.getAvatarMediaid());
        }
        // 2. 获取客户信息
        // 2.1 根据openid获取unionId
        String unionId = wechatOpenService.getUnionIdByOpenId(openId);
        // 2.2 先根据union_id去客户表查询是否有数据
        WeCustomer customer =  weCustomerService.getCustomerByUnionId(unionId, openId, weForm.getCorpId());
        if (customer == null) {
            log.info("[异步智能表单-点击记录处理] 查询不到客户信息,openid:{}", openId);
            // 查询不到客户信息则将unionId写入记录表 记录非联系人的点击记录
            weFormOperRecord.setUnionId(unionId);
            weFormOperRecordMapper.insert(weFormOperRecord);
            return;
        }
        weFormOperRecord.setExternalUserId(customer.getExternalUserid());
        weFormOperRecord.setUnionId(customer.getUnionid());
        // 3. 查询客户所属员工
        List<WeFlowerCustomerRel> weFlowerCustomerRels = weFlowerCustomerRelService.list(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getExternalUserid, customer.getExternalUserid())
                .eq(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.NORMAL.getCode())
                .eq(WeFlowerCustomerRel::getCorpId, customer.getCorpId()));
        if (CollectionUtils.isEmpty(weFlowerCustomerRels)) {
            // 若客户没有所属员工
            weFormOperRecordMapper.insert(weFormOperRecord);
            return;
        }
        // 若发送表单的人与点击客户为好友关系
        if (weFlowerCustomerRels.stream().anyMatch(it -> it.getUserId().equals(userId))) {
            weFormOperRecord.setEmployees(userId);
        } else {
            // 将最近成为好友的员工设置为所属员工
            Optional<WeFlowerCustomerRel> max = weFlowerCustomerRels.stream().max(Comparator.comparing(WeFlowerCustomerRel::getCreateTime));
            if (max.isPresent()) {
                weFormOperRecord.setEmployees(max.get().getUserId());
                userDetail = weUserService.getUserDetail(weForm.getCorpId(), weFormOperRecord.getEmployees());
            }
        }
        // 5.插入点击记录
        weFormOperRecordMapper.insert(weFormOperRecord);
        doExtraSettingOfClick(weForm, weFormAdvanceSetting, weFormOperRecord, weForm.getCorpId(), userDetail, customer);
    }

    @Override
    public void addCommitRecord(FormCommitDTO formCommitDTO, WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, WeFormOperRecord weFormOperRecord) {
        // 1. 保存提交记录
        Date commitDate = DateUtils.getNowDate();
        weFormOperRecordMapper.update(null, new LambdaUpdateWrapper<WeFormOperRecord>()
                .eq(WeFormOperRecord::getId, formCommitDTO.getRecordId())
                .set(WeFormOperRecord::getFormResult, formCommitDTO.getFormResult())
                .set(WeFormOperRecord::getCommitTime, commitDate)
                .set(WeFormOperRecord::getCommitFlag, true));
        weFormOperRecord.setCommitTime(commitDate);
        // 2. 客服好评表插入
        weFormCustomerFeedbackService.batchAddFeedback(weForm.getId(), weFormOperRecord.getExternalUserId(), weFormOperRecord.getUserId(),
                formCommitDTO.getScoreValueList(), formCommitDTO.getNpsValueList(), weForm.getCorpId());
        // 3. 执行高级操作
        doExtraSettingOfCommit(weForm, weFormAdvanceSetting, weFormOperRecord);
    }

    /**
     * 执行高级操作（提交）
     *
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @param weFormOperRecord     {@link WeFormOperRecord}
     */
    private void doExtraSettingOfCommit(WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, WeFormOperRecord weFormOperRecord) {
        if (weForm == null || StringUtils.isBlank(weForm.getCorpId()) || weFormAdvanceSetting == null || weFormOperRecord == null) {
            log.error("[智能表单-高级设置处理] 执行高级设置参数缺失,weForm:{}, weFormAdvanceSetting:{}, weFormOperRecord:{}", weForm, weFormAdvanceSetting, weFormOperRecord);
            return;
        }
        String corpId = weForm.getCorpId();
        // 获取企业应用信息
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (corpAccount == null) {
            log.error("[智能表单-高级设置处理] 获取企业信息失败,corpId:{}", corpId);
            return;
        }
        // 获取客户详情
        WeCustomer customer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>()
                .eq(WeCustomer::getCorpId, corpId)
                .eq(WeCustomer::getExternalUserid, weFormOperRecord.getExternalUserId()));
        if (customer == null) {
            log.info("[智能表单-高级设置处理] 获取客户详情,查询不到客户信息,corpId:{},externalUserId:{}", corpId, weFormOperRecord.getExternalUserId());
            return;
        }
        // 是否是管理员
        if (UserConstants.INIT_ADMIN_ROLE_KEY.equals(weFormOperRecord.getUserId())) {
            getLastUser(weForm.getCorpId(), weFormOperRecord);
        }
        // 获取员工详情
        WeUser weUser = weUserService.getUserDetail(corpId, weFormOperRecord.getUserId());
        if (weUser == null) {
            log.info("[智能表单-高级设置处理] 获取员工详情,查询不到员工信息,corpId:{},userId:{}", corpId, weFormOperRecord.getUserId());
            return;
        }
        // 行为通知
        if (Boolean.TRUE.equals(weFormAdvanceSetting.getActionNoteFlag())) {
            String content = FormConstants.getCommitNoticeContent(weForm.getFormName(), customer.getName(), weFormOperRecord.getCommitTime());
            sendNotice(content, corpAccount.getCorpId(), weFormOperRecord.getUserId(), corpAccount.getAgentId());
        }
        // 轨迹记录
        if (Boolean.TRUE.equals(weFormAdvanceSetting.getTractRecordFlag())) {
            weCustomerTrajectoryService.recordFormCommitOperation(weForm, weFormOperRecord, weUser, customer);
        }
        // 打标签
        if (Boolean.TRUE.equals(weFormAdvanceSetting.getCustomerLabelFlag())) {
            CustomerLabelSettingModel customerLabelSettingModel = JSON.parseObject(weFormAdvanceSetting.getLabelSettingJson(), CustomerLabelSettingModel.class);
            if (customerLabelSettingModel == null) {
                return;
            }
            setTagForForm(customerLabelSettingModel.getSubmitLabelIdList(),
                    corpAccount.getCorpId(),
                    weFormOperRecord.getUserId(),
                    weFormOperRecord.getUserName(),
                    weFormOperRecord.getExternalUserId());
            recordFormTag(weForm,corpId,weUser,customer,CustomerTrajectoryEnums.TagType.SUBMIT_FORM.getType(),customerLabelSettingModel.getSubmitLabelIdList());
        }
    }


    /**
     * 执行高级操作（点击）
     *
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @param weFormOperRecord     {@link WeFormOperRecord}
     * @param corpId               企业id
     * @param weUser               {@link WeUser}
     * @param customer             {@link WeCustomer}
     */
    private void doExtraSettingOfClick(WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, WeFormOperRecord weFormOperRecord, String corpId, WeUser weUser, WeCustomer customer) {
        if (weForm == null || StringUtils.isBlank(weForm.getCorpId()) || weFormAdvanceSetting == null || weFormOperRecord == null) {
            log.error("[智能表单-高级设置处理] 执行高级设置参数缺失,weForm:{}, weFormAdvanceSetting:{}, weFormOperRecord:{}", weForm, weFormAdvanceSetting, weFormOperRecord);
            return;
        }
        // 是否是管理员
        if (UserConstants.INIT_ADMIN_ROLE_KEY.equals(weFormOperRecord.getUserId())) {
            getLastUser(weForm.getCorpId(), weFormOperRecord);
        }
        // 轨迹记录
        if (Boolean.TRUE.equals(weFormAdvanceSetting.getTractRecordFlag())) {
            weCustomerTrajectoryService.recordFormClickOperation(weForm, weFormOperRecord, weUser, customer);
        }
        // 打标签
        if (Boolean.TRUE.equals(weFormAdvanceSetting.getCustomerLabelFlag())) {
            CustomerLabelSettingModel customerLabelSettingModel = JSON.parseObject(weFormAdvanceSetting.getLabelSettingJson(), CustomerLabelSettingModel.class);
            if (customerLabelSettingModel == null) {
                return;
            }
            setTagForForm(customerLabelSettingModel.getClickLabelIdList(),
                    corpId,
                    weFormOperRecord.getUserId(),
                    weFormOperRecord.getUserName(),
                    weFormOperRecord.getExternalUserId());
            recordFormTag(weForm,corpId,weUser,customer,CustomerTrajectoryEnums.TagType.CLICK_FORM.getType(),customerLabelSettingModel.getClickLabelIdList());
        }
    }

    /**
     * 记录表单点击或提交打标签信息动态
     *
     * @param weForm 表单详情
     * @param corpId 公司id
     * @param weUser 员工信息
     * @param customer 客户信息
     * @param type 类型（区分点击还是提交）
     * @param tagIdList 标签id列表
     */
    public void recordFormTag(WeForm weForm,String corpId, WeUser weUser, WeCustomer customer,String type,List<String> tagIdList){
        if (Objects.isNull(weForm)||Objects.isNull(weUser)||Objects.isNull(customer)||StringUtils.isBlank(corpId)||CollectionUtils.isEmpty(tagIdList)) {
            log.info("记录表单操作信息动态时，表单，公司id，员工，客户,标签不能为空，weForm：{}，corpId：{}，weUser：{}，customer：{}，tagIdList：{}", weForm, corpId, weUser,customer,tagIdList);
            return;
        }
        TagRecordUtil tagRecordUtil=new TagRecordUtil();
        String content=tagRecordUtil.buildFormContent(weForm.getFormName(),type);
        List<String> tagName = weTagService.selectTagByIds(tagIdList).stream().map(TagInfoVO::getTagName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagName)){
            log.info("记录表单操作信息动态时,获取标签名列表异常");
            return;
        }
        String detail = String.join(",", tagName);
        //保存信息动态
        weCustomerTrajectoryService.saveCustomerTrajectory(corpId,weUser.getUserId(),customer.getExternalUserid(),content,detail);
    }

    /**
     * 查找客户最近添加的员工
     *
     * @param corpId 企业ID
     * @param weFormOperRecord {@link WeFormOperRecord}
     */
    private void getLastUser(String corpId, WeFormOperRecord weFormOperRecord) {
        if (StringUtils.isBlank(corpId) || weFormOperRecord == null) {
            log.error("[智能表单-高级设置处理] 执行高级设置参数缺失,corpId:{}, weFormOperRecord:{}", corpId, weFormOperRecord);
            return;
        }
        // 查出该客户最近添加的员工
        WeFlowerCustomerRel flowerCustomerRel = weFlowerCustomerRelService.getLastUser(weFormOperRecord.getExternalUserId(), corpId);
        if (flowerCustomerRel == null) {
            log.info("[智能表单-高级设置处理] 用户不存在添加的员工, flowerCustomerRel:{}", flowerCustomerRel);
            throw new CustomException(ResultTip.TIP_NOT_HAVE_FOLLOW_USER);
        }
        // 更改admin为添加的员工userid
        weFormOperRecord.setUserId(flowerCustomerRel.getUserId());
    }

    /**
     * 发送企业通知
     *
     * @param content 消息
     * @param corpId  企业id
     * @param userId  员工id
     * @param agentId 应用id
     */
    private void sendNotice(String content, String corpId, String userId, String agentId) {
        if (StringUtils.isAnyBlank(content, userId, corpId, agentId)) {
            log.info("[智能表单-高级设置] 发送通知,参数缺失,content:{},corpId:{}, userId:{}", content, corpId, userId);
            return;
        }
        TextMessageDTO contentInfo = TextMessageDTO.builder()
                .content(content)
                .build();
        WeMessagePushDTO request = WeMessagePushDTO.builder()
                .msgtype(MessageType.TEXT.getMessageType())
                .touser(userId)
                .text(contentInfo)
                .agentid(Integer.valueOf(agentId))
                .build();
        weMessagePushClient.sendMessageToUser(request, agentId, corpId);
    }


    @Override
    public void setTagForForm(List<String> tagIdList, String corpId, String userId, String userName, String externalUserId) {
        if (CollectionUtils.isEmpty(tagIdList)) {
            return;
        }
        weCustomerService.singleMarkLabel(corpId, userId, externalUserId, tagIdList, userName);
    }

    @Override
    public List<FormCustomerOperRecordVO> getCustomerOperRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String customerName, Integer channelType) {
        if (formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return this.baseMapper.getCustomerOperRecord(formId, isClickTime(timeType), beginTime, endTime, customerName, channelType);
    }

    @Override
    public List<FormUserSendRecordVO> getUserSendRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String userName) {
        if (formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        List<FormUserSendRecordVO> formUserSendRecords = this.baseMapper.getUserSendRecord(formId, LoginTokenService.getLoginUser().getCorpId(), isClickTime(timeType), beginTime, endTime, userName);
        for (FormUserSendRecordVO formUserSendRecord : formUserSendRecords) {
            formUserSendRecord.setSubmitPercent(calcSubmitPercent(formUserSendRecord.getSubmitCount(), formUserSendRecord.getClickCount()));
        }
        return formUserSendRecords;
    }

    @Override
    public List<FormOperRecordDetailVO> getFormResult(Long formId) {
        if (formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeForm weForm = weFormMapper.selectById(formId);
        if (weForm == null) {
            return new ArrayList<>();
        }
        List<FormOperRecordDetailVO> formResult = this.baseMapper.getFormResult(formId, LoginTokenService.getLoginUser().getCorpId());
        if (CollectionUtils.isEmpty(formResult)) {
            return new ArrayList<>();
        }
        formResult.forEach(it -> it.setFormName(weForm.getFormName()));
        return formResult;
    }

    /**
     * 获取表单名称
     *
     * @param formId    表单id
     * @return
     */
    String getFormName(Long formId) {
        WeForm weForm = weFormMapper.selectById(formId);
        if (weForm == null) {
            throw new CustomException(ResultTip.TIP_FORM_IS_NOT_EXIST);
        }
        return weForm.getFormName();
    }
    @Override
    public AjaxResult exportCustomerOperRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String customerName, Integer channelType) {
        if (formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 表单名称 + "的客户操作记录"
        String sheetName = getFormName(formId) + "的客户操作记录";
        List<FormCustomerOperRecordExportVO> list = this.baseMapper.exportCustomerOperRecord(formId, LoginTokenService.getLoginUser().getCorpId(), channelType, isClickTime(timeType), beginTime, endTime, customerName);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_NO_DATA_TO_EXPORT);
        }
        // 解析并填充表单问题和答案到拓展属性
        for (FormCustomerOperRecordExportVO item : list) {
            if (StringUtils.isBlank(item.getFormResult())) {
                continue;
            }
            List<FormResultModel> formResultModelList = JSON.parseArray(item.getFormResult(), FormResultModel.class);
            Map<String, String> qusertionAnswerMap = formResultModelList.stream().collect(Collectors.toMap(FormResultModel::getId, it -> StringUtils.defaultString(it.getAnswer()), (k1, k2) -> k1, LinkedHashMap::new));
            if (qusertionAnswerMap.isEmpty()) {
                continue;
            }
            item.setExtendPropMapper(qusertionAnswerMap);
        }
        // 获取表单问题作为列名
        int first = 0;
        List<FormResultModel> formResultModels = JSON.parseArray(list.get(first).getFormResult(), FormResultModel.class);
        List<String> properties = formResultModels.stream().map(FormResultModel::getQuestion).collect(Collectors.toList());
        // 导出
        ExcelUtil<FormCustomerOperRecordExportVO> util = new ExcelUtil<>(FormCustomerOperRecordExportVO.class);
        return util.exportExcelDefinedAndExtProp(list, sheetName, properties);
    }

    @Override
    public AjaxResult exportUserSendRecord(Long formId, Integer timeType, Date beginTime, Date endTime, String userName) {
        if (formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 表单名称 + "的员工发送记录"
        String sheetName = getFormName(formId) + "的员工发送记录";
        List<FormUserSendRecordVO> list = this.getUserSendRecord(formId, timeType, beginTime, endTime, userName);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ResultTip.TIP_NO_DATA_TO_EXPORT);
        }
        ExcelUtil<FormUserSendRecordVO> util = new ExcelUtil<>(FormUserSendRecordVO.class);
        return util.exportExcel(list, sheetName);
    }

    /**
     * 计算提交率
     *
     * @param submitCount       提交次数
     * @param clickTotalCount   点击次数
     */
    String calcSubmitPercent(Integer submitCount, Integer clickTotalCount) {
        int zeroValue = 0;
        int scale = 4;
        BigDecimal percent = new BigDecimal(100);
        if (submitCount == null || submitCount.equals(zeroValue)) {
            return BigDecimal.ZERO.toPlainString() + "%";
        } else {
            return new BigDecimal(submitCount)
                    .divide(new BigDecimal(clickTotalCount), scale, RoundingMode.HALF_UP)
                    .multiply(percent)
                    .stripTrailingZeros()
                    .toPlainString() + "%";
        }
    }



    /**
     * 判断是点击时间还是提交时间
     *
     * @param timeType  时间类型（点击时间 or 提交时间）
     * @return  点击时间返回true 提交时间返回false
     */
    private Boolean isClickTime(Integer timeType) {
        if (FormOperEnum.UNKNOWN.equals(FormOperEnum.getByCode(timeType))) {
            return null;
        }
        return FormOperEnum.CLICK.getCode().equals(timeType);
    }
}

