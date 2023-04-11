package com.easyink.wecom.service.impl.form;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.form.FormConstants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.DelFlag;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.service.QRCodeHandler;
import com.easyink.common.shorturl.model.FormShortUrlAppendInfo;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.form.*;
import com.easyink.wecom.domain.entity.form.*;
import com.easyink.wecom.domain.enums.form.DeadLineType;
import com.easyink.wecom.domain.enums.form.FormChannelEnum;
import com.easyink.wecom.domain.enums.form.PromotionalType;
import com.easyink.wecom.domain.enums.form.SubmitCntType;
import com.easyink.wecom.domain.model.form.CustomerLabelSettingModel;
import com.easyink.wecom.domain.model.form.WeFormModel;
import com.easyink.wecom.domain.query.form.FormQuery;
import com.easyink.wecom.domain.vo.autotag.TagInfoVO;
import com.easyink.wecom.domain.vo.form.*;
import com.easyink.wecom.handler.shorturl.FormShortUrlHandler;
import com.easyink.wecom.handler.shorturl.RadarShortUrlHandler;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.form.WeFormMapper;
import com.easyink.wecom.service.WeTagService;
import com.easyink.wecom.service.form.*;
import com.easyink.wecom.service.wechatopen.WechatOpenService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.easyink.common.enums.ResultTip.*;

/**
 * 表单表(WeForm)表服务实现类
 *
 * @author tigger
 * @since 2023-01-09 15:00:46
 */
@Slf4j
@Service("weFormService")
public class WeFormServiceImpl extends ServiceImpl<WeFormMapper, WeForm> implements WeFormService {

    private final WeFormAdvanceSettingService settingService;
    private final WeFormGroupService weFormGroupService;
    private final WeTagService tagService;
    private final WeFormOperRecordService weFormOperRecordService;
    private final WeFormMapper weFormMapper;
    private final FormShortUrlHandler formShortUrlHandler;
    private final WechatOpenService wechatOpenService;
    private final WeFormShortCodeRelService weFormShortCodeRelService;
    private final QRCodeHandler qrCodeHandler;

    @Resource(name = "formTaskExecutor")
    private ThreadPoolTaskExecutor formTaskExecutor;

    @Lazy
    public WeFormServiceImpl(WeFormAdvanceSettingService settingService, WeFormGroupService weFormGroupService, WeTagService tagService, WeFormOperRecordService weFormOperRecordService, WeFormMapper weFormMapper, FormShortUrlHandler formShortUrlHandler, WechatOpenService wechatOpenService, WeFormShortCodeRelService weFormShortCodeRelService, QRCodeHandler qrCodeHandler) {
        this.settingService = settingService;
        this.weFormGroupService = weFormGroupService;
        this.tagService = tagService;
        this.weFormOperRecordService = weFormOperRecordService;
        this.weFormMapper = weFormMapper;
        this.formShortUrlHandler = formShortUrlHandler;
        this.wechatOpenService = wechatOpenService;
        this.weFormShortCodeRelService = weFormShortCodeRelService;
        this.qrCodeHandler = qrCodeHandler;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFormByGroupId(List<Integer> idList, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (CollectionUtils.isEmpty(idList)) {
            throw new CustomException(ResultTip.TIP_GROUP_FORM_ID_IS_NOT_NULL);
        }


        List<WeForm> deleteFormList = this.list(new LambdaQueryWrapper<WeForm>()
                .select(WeForm::getId)
                .eq(WeForm::getCorpId, corpId)
                .in(WeForm::getGroupId, idList)
        );

        // 执行逻辑删除同时更新DeleteId字段
        this.deleteBatchForm(deleteFormList.stream().map(WeForm::getId).collect(Collectors.toList()));

    }

    @Override
    public void deleteBatchForm(List<Long> deleteIdList) {
        if (CollectionUtils.isEmpty(deleteIdList)) {
            return;
        }
        this.baseMapper.deleteBatch(deleteIdList);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveForm(FormAddRequestDTO addDTO, String corpId) {
        if (addDTO == null || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 1.校验
        addDTO.valid();
        // 校验表单名称唯一性, 企业下唯一
//        validFormNameUnique(null, addDTO.getForm().getFormName(), corpId);

        // 2.插入表单
        Long formId = this.saveFormReturnId(addDTO.getForm(), corpId);

        // 2.插入表单设置
        FormSettingAddDTO formSetting = addDTO.getFormSetting();
        if (formSetting == null || formId == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        settingService.saveOrUpdateFormSetting(formSetting.toEntity(formId), corpId);
    }

    /**
     * 校验表单名称唯一性
     *
     * @param formId   表单id
     * @param formName expect表单名称
     * @param corpId   企业id
     */
    private void validFormNameUnique(Integer formId, String formName, String corpId) {
        if (StringUtils.isAnyBlank(corpId)) {
            throw new CustomException(TIP_PARAM_MISSING);
        }
        if (StringUtils.isBlank(formName)) {
            throw new CustomException(ResultTip.TIP_FORM_NAME_IS_NOT_BLANK);
        }

        Integer countNum = this.baseMapper.countSameNameNum(formName, corpId);

        // formId不等于null,表示更新,需要判断原name和更新的name是否一致,如果一致,则减少count值(不包含当前分组名称的计数)
        if (formId != null) {
            WeForm dbGroup = this.baseMapper.selectById(formId);
            // 要更新的名称与原数据相同,减掉自己 countNum--
            if (dbGroup.getFormName().equals(formName)) {
                countNum--;
            }
        }

        int uniqueNum = 0;
        if (countNum > uniqueNum) {
            throw new CustomException(ResultTip.TIP_FORM_NAME_EXIST);
        }

    }

    @Override
    public Long saveFormReturnId(FormAddDTO formDTO, String corpId) {
        if (formDTO == null || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }

        LoginUser loginUser = LoginTokenService.getLoginUser();
        WeForm form = formDTO.toEntity(corpId);
        form.setCreateBy(loginUser);
        form.setUpdateBy(loginUser);

        this.save(form);
        return form.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateForm(FormUpdateRequestDTO updateDTO, String corpId) {
        if (updateDTO == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }

        // 校验
        updateDTO.valid();
        // 校验表单名称唯一性, 企业下唯一
//        validFormNameUnique(updateDTO.getId(), updateDTO.getForm().getFormName(), corpId);

        // 修改表单
        WeForm form = updateDTO.getForm().toEntity(corpId);
        form.setId(updateDTO.getId());
        form.setCorpId(null);
        form.setNullCreateTime();
        form.setUpdateBy(LoginTokenService.getLoginUser());
        this.updateById(form);
        // 修改表单设置
        WeFormAdvanceSetting setting = updateDTO.getFormSetting().toEntity(updateDTO.getId());
        settingService.saveOrUpdateFormSetting(setting, corpId);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteForm(List<Long> idList, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        if (CollectionUtils.isEmpty(idList)) {
            throw new CustomException(ResultTip.TIP_FORM_ID_LIST_IS_NOT_NULL);
        }
        // 查询对应表单数据
        List<WeForm> deleteList = this.list(new LambdaQueryWrapper<WeForm>()
                .select(WeForm::getId)
                .in(WeForm::getId, idList)
                .eq(WeForm::getCorpId, corpId)
        );
        if (CollectionUtils.isEmpty(deleteList)) {
            throw new CustomException(TIP_FORM_IS_NOT_EXIST);
        }
        this.deleteBatchForm(deleteList.stream().map(WeForm::getId).collect(Collectors.toList()));
    }

    @Override
    public List<FormPageVO> getPage(FormQuery formQuery, String corpId) {
        return this.getPage(formQuery, corpId, Boolean.TRUE);
    }

    @Override
    public List<FormPageVO> getPage(FormQuery formQuery, String corpId, Boolean pageFlag) {
        if (formQuery == null || StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_GENERAL_ERROR);
        }
        // 校验
        formQuery.initAndValid();

        // 判断searchValue
        formQuery.fillSearchValue();

        // 查询当前分组id包含其子分组
        formQuery.setSearchGroupIdList(searchAndFillGroupIdList(formQuery.getGroupId(), formQuery.getUnNeedChildFlag(), corpId));
        if (pageFlag) {
            PageDomain pageDomain = TableSupport.buildPageRequest();
            PageHelper.startPage(pageDomain.getPageNum(), pageDomain.getPageSize());
        }

        return this.baseMapper.formPage(formQuery, corpId);

    }

    /**
     * 填充分组条件
     *
     * @param groupId         当前选中的分组id
     * @param unNeedChildFlag 是否不需要查询子分组下的数据标识
     * @param corpId          企业id
     * @return 分组id列表
     */
    private List<Integer> searchAndFillGroupIdList(Integer groupId, Boolean unNeedChildFlag, String corpId) {
        List<Integer> groupIdList = new ArrayList<>();
        if (groupId != null && StringUtils.isNotBlank(corpId)) {
            groupIdList.add(groupId);
            // 默认不需要
            if (!unNeedChildFlag) {
                groupIdList.addAll(weFormGroupService.listChildGroupIdList(groupId, corpId));
            }
        }
        return groupIdList;
    }

    @Override
    public FormDetailViewVO getDetail(Long id, String corpId) {
        try {
            // 响应超时时间
            int timeout = 5;
            return CompletableFuture
                    // 查询表单
                    .supplyAsync(() -> this.baseMapper.selectFormDetail(id, corpId)
                            // 填充字典值
                            .fillDict())
                    .thenApply(detailViewVO -> {
                        // 查询客户标签设置根据id查询名称
                        detailViewVO.setLabelSetting(parseAndBuildTagVOList(detailViewVO.getLabelSettingJson()));
                        return detailViewVO;
                    })
                    .exceptionally(e -> {
                        throw new CustomException(ExceptionUtils.getMessage(e));
                    }).get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("查询表单详情异常, formId: {}, corpId: {}, e: {}", id, corpId, ExceptionUtils.getMessage(e));
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBatchGroup(BatchUpdateGroupDTO batchDTO, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_GENERAL_ERROR);
        }
        if (batchDTO == null) {
            throw new CustomException(TIP_PARAM_MISSING);
        }

        // 校验
        batchDTO.valid();

        // 更新
        List<WeForm> updateList = new ArrayList<>();
        for (Long formId : batchDTO.getFormIdList()) {
            WeForm weForm = new WeForm();
            weForm.setId(formId);
            weForm.setGroupId(batchDTO.getGroupId());
            weForm.setNullCreateTime();
            updateList.add(weForm);
        }
        this.updateBatchById(updateList);


    }

    @Override
    public void enableForm(Long id, Boolean enableFlag, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_GENERAL_ERROR);
        }
        if (id == null || enableFlag == null) {
            throw new CustomException(TIP_PARAM_MISSING);
        }

        // 查询是否存在
        WeForm form = this.getOne(new LambdaQueryWrapper<WeForm>()
                .eq(WeForm::getId, id)
                .eq(WeForm::getCorpId, corpId)
        );
        if (form == null) {
            throw new CustomException(TIP_FORM_IS_NOT_EXIST);
        }

        // 更新
        this.update(new LambdaUpdateWrapper<WeForm>()
                .set(WeForm::getEnableFlag, enableFlag)
                .eq(WeForm::getId, id)
                .eq(WeForm::getCorpId, corpId)
        );


    }

    @Override
    public FormTotalView totalView(Long id, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_GENERAL_ERROR);
        }
        if (id == null) {
            throw new CustomException(TIP_PARAM_MISSING);
        }
        LocalDate beginTime = LocalDate.now();
        int onDayToAdd = 1;
        LocalDate endTime = beginTime.plusDays(onDayToAdd);
        return this.baseMapper.selectTotalView(id, corpId, beginTime, endTime).fillPercent();
    }

    @Override
    public WeFormEditDetailVO getEditDetail(Long id, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_GENERAL_ERROR);
        }

        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(TIP_PARAM_MISSING);
        }
        WeForm form = this.baseMapper.selectOne(new LambdaQueryWrapper<WeForm>()
                .eq(WeForm::getId, id)
                .eq(WeForm::getCorpId, corpId)
        );


        WeFormAdvanceSetting formSetting = settingService.getOne(new LambdaQueryWrapper<WeFormAdvanceSetting>()
                .eq(WeFormAdvanceSetting::getFormId, id));

        if (form == null || formSetting == null) {
            log.warn("[获取编辑表单详情异常] formId: {}, formExist: {}, formSettingExist: {}", id, form == null, formSetting == null);
            throw new CustomException(TIP_FORM_IS_NOT_EXIST);
        }
        // 查询客户标签设置根据id查询名称
        CustomerLabelSettingDetailVO labelSettingDetailVO = parseAndBuildTagVOList(formSetting.getLabelSettingJson());

        return new WeFormEditDetailVO(new WeFormVO(form), new WeFormSettingVO(formSetting, labelSettingDetailVO));

    }

    /**
     * 解析构建标签设置VO
     *
     * @param labelSettingJson
     * @return
     */
    private CustomerLabelSettingDetailVO parseAndBuildTagVOList(String labelSettingJson) {
        if (StringUtils.isBlank(labelSettingJson)) {
            return CustomerLabelSettingDetailVO.empty();
        }
        CustomerLabelSettingModel customerLabelSettingModel = JSON.parseObject(labelSettingJson, CustomerLabelSettingModel.class);
        List<TagInfoVO> clickLabelInfoList = tagService.selectTagByIds(customerLabelSettingModel.getClickLabelIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        List<TagInfoVO> submitLabelInfoList = tagService.selectTagByIds(customerLabelSettingModel.getSubmitLabelIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        return new CustomerLabelSettingDetailVO(clickLabelInfoList, submitLabelInfoList);
    }

    @Override
    public List<FormPageVO> getList(FormQuery formQuery, String corpId) {
        return this.getPage(formQuery, corpId, Boolean.FALSE);
    }

    @Override
    public String genFormUrl(Long formId, String corpId, String userId, Integer channel) {
        if (formId == null || channel == null || StringUtils.isAnyBlank(corpId, userId)) {
            log.info("[表单生成链接] 参数缺失 formId:{}, corpId:{}, userId:{}, channel:{}", formId, corpId, userId, FormChannelEnum.getByCode(channel).getDesc());
            return StringUtils.EMPTY;
        }
        // 获取中间页域名
        String middlePageDomain = wechatOpenService.getDomain(corpId);
        if (StringUtils.isBlank(middlePageDomain)) {
            log.info("[表单生成链接] 未设置中间页域名 formId:{}", formId);
            throw new CustomException(ResultTip.TIP_GET_MIDDLE_PAGE_DOMAIN_ERROR);
        }
        return FormConstants.genFormUrl(middlePageDomain, formId, userId, channel);
    }

    @Override
    @Convert2Cipher
    public String sideBarGenFormUrl(Long formId, String userId, Integer channelType) {
        return genFormUrl(formId, LoginTokenService.getLoginUser().getCorpId(), userId, channelType);
    }


    @Override
    public AttachmentParam getFormAttachment(Long formId, String corpId, String userId, Integer channelType) {
        log.info("【智能表单】 开始生成{}渠道表单附件, formId:{}, userId:{}, corpId:{}", FormChannelEnum.getByCode(channelType), formId, userId, corpId);
        if (formId == null || StringUtils.isAnyBlank(corpId, userId)
                || FormChannelEnum.UNKNOWN.equals(FormChannelEnum.getByCode(channelType))) {
            return null;
        }
        WeForm weForm = this.baseMapper.selectById(formId);
        if (weForm == null) {
            return null;
        }
        AttachmentParam.AttachmentParamBuilder builder = AttachmentParam.builder();
        final AttachmentParam build = builder.content(weForm.getFormName())
                .picUrl(WeConstans.FORM_DEFAULT_ICON_URL)
                .description(weForm.getDescription())
                .url(genFormUrl(formId, corpId, userId, channelType)).typeEnum(AttachmentTypeEnum.LINK).build();
        log.info("【智能表单】 {}渠道表单生成成功, formId:{}, userId:{}, corpId:{}", FormChannelEnum.getByCode(channelType), formId, userId, corpId);
        return build;
    }

    @Override
    public String genShortUrl(String url, Long formId, String corpId, String userId, Integer channel) {
        if (formId == null || channel == null || StringUtils.isAnyBlank(url, corpId, userId)) {
            log.info("[表单生成短链] 参数缺失 url:{}, formId:{}, corpId:{}, userId:{}, channel:{}", url, formId, corpId, userId, FormChannelEnum.getByCode(channel).getDesc());
            return null;
        }
        // 查看当前员工是否创建过该表单 创建过直接返回短链
        WeFormShortCodeRel weFormShortCodeRel = weFormShortCodeRelService.getOne(new LambdaQueryWrapper<WeFormShortCodeRel>()
                .eq(WeFormShortCodeRel::getFormId, formId)
                .eq(WeFormShortCodeRel::getUserId, userId)
                .last(GenConstants.LIMIT_1));
        // 创建短链
        WeFormAdvanceSetting setting = settingService.getOne(new LambdaQueryWrapper<WeFormAdvanceSetting>().eq(WeFormAdvanceSetting::getFormId, formId));
        if (setting == null || StringUtils.isBlank(setting.getWeChatPublicPlatform())) {
            log.info("[表单生成短链] 表单未配置公众号 formId:{}", formId);
            throw new CustomException(ResultTip.TIP_FORM_OFFICE_ACCOUNT_IS_NULL);
        }
        if (weFormShortCodeRel != null) {
            String domain = wechatOpenService.getDomain(corpId);
            return FormConstants.genShortUrl(domain, weFormShortCodeRel.getShortCode());
        }
        FormShortUrlAppendInfo appendInfo = FormShortUrlAppendInfo.builder()
                .formId(formId)
                .userId(userId)
                .channelType(channel)
                .appId(setting.getWeChatPublicPlatform())
                .corpId(corpId).build();

        return formShortUrlHandler.createShortUrl(corpId, url, userId, appendInfo);
    }

    /**
     * 判断是否限制提交(限制提交次数)
     *
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @param weFormOperRecord     {@link WeFormUseRecord}
     * @return boolean
     */
    public boolean isNotLimitCommit(WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting, WeFormOperRecord weFormOperRecord) {
        if (weForm == null || weFormAdvanceSetting == null) {
            return false;
        }
        if (SubmitCntType.NOT_LIMIT.getCode().equals(weFormAdvanceSetting.getSubmitCntType()) || weFormOperRecord == null) {
            return true;
        }
        // 限制提交一次 且 有提交
        if (Boolean.TRUE.equals(weFormOperRecord.getCommitFlag())) {
            return false;
        }
        WeFormOperRecord otherCommitedRecord = weFormOperRecordService.getOne(new LambdaQueryWrapper<WeFormOperRecord>()
                .eq(WeFormOperRecord::getFormId, weFormOperRecord.getFormId())
                .eq(WeFormOperRecord::getOpenId, weFormOperRecord.getOpenId())
                .eq(WeFormOperRecord::getCommitFlag, true)
                .last(GenConstants.LIMIT_1));
        // 是否满足可提交次数
        return otherCommitedRecord == null;
    }

    /**
     * 是否关闭提交 时间是否有效
     *
     * @param weForm               {@link WeForm}
     * @param weFormAdvanceSetting {@link WeFormAdvanceSetting}
     * @return
     */
    public boolean isNotCloseSubmit(WeForm weForm, WeFormAdvanceSetting weFormAdvanceSetting) {
        if (weForm == null || weFormAdvanceSetting == null) {
            return false;
        }
        // 未启用或已删除 返回false
        if (Boolean.FALSE.equals(weForm.getEnableFlag()) || (DelFlag.DEL.getCode().equals(weForm.getDelFlag()))) {
            return false;
        }
        // 日期是否有效
        boolean dateIsValid = true;
        if (DeadLineType.CUSTOM.getCode().equals(weFormAdvanceSetting.getDeadLineType())) {
            dateIsValid = DateUtils.isGreaterThanCurrentTime(weFormAdvanceSetting.getCustomDate());
        }
        return dateIsValid;
    }

    @Override
    public FormContentVO getContent(Long formId, String userId, String openId, Integer channelType) {
        if (StringUtils.isAnyBlank(userId, openId) || formId == null || channelType == null) {
            log.error("[智能表单-H5获取表单内容] 参数缺失, userFormId：{}, userId:{}, openId:{}", formId, userId, openId);
            throw new CustomException(TIP_PARAM_MISSING);
        }
        // 查询表单内容
        CompletableFuture<WeForm> weFormCf = CompletableFuture.supplyAsync(() -> weFormMapper.selectByIdIgnoreDelete(formId), formTaskExecutor);
        // 查询表单设置
        CompletableFuture<WeFormAdvanceSetting> weFormAdvanceSettingCf = CompletableFuture.supplyAsync(() -> settingService.getOne(new LambdaQueryWrapper<WeFormAdvanceSetting>()
                .eq(WeFormAdvanceSetting::getFormId, formId)), formTaskExecutor);
        // 查询是否有已提交的提交记录
        CompletableFuture<WeFormOperRecord> weFormOperRecordCf = CompletableFuture
                .supplyAsync(() -> weFormOperRecordService.getOne(new LambdaQueryWrapper<WeFormOperRecord>()
                        .eq(WeFormOperRecord::getFormId, formId)
                        .eq(WeFormOperRecord::getOpenId, openId)
                        .eq(WeFormOperRecord::getCommitFlag, true)
                        .last(GenConstants.LIMIT_1)), formTaskExecutor);
        // 组合判断
        try {
            // 响应超时时间
            int timeout = 3;
            CompletableFuture.allOf(weFormCf, weFormAdvanceSettingCf, weFormOperRecordCf)
                    .exceptionally(e -> {
                        log.error("[智能表单-H5获取表单内容] 查询异常, formId:{}, e: {}", formId, ExceptionUtils.getStackTrace(e));
                        throw new CustomException(ExceptionUtils.getMessage(e));
                    }).get(timeout, TimeUnit.SECONDS);
            WeForm weForm = weFormCf.get(timeout, TimeUnit.SECONDS);
            WeFormAdvanceSetting weFormAdvanceSetting = weFormAdvanceSettingCf.get(timeout, TimeUnit.SECONDS);
            WeFormOperRecord weFormOperRecord = weFormOperRecordCf.get(timeout, TimeUnit.SECONDS);
            boolean notLimitCommit = isNotLimitCommit(weForm, weFormAdvanceSetting, weFormOperRecord);
            boolean notCloseSubmit = isNotCloseSubmit(weForm, weFormAdvanceSetting);
            // 增加点击记录
            // 生成记录id
            Long recordId = SnowFlakeUtil.nextId();
            if (notLimitCommit && notCloseSubmit) {
                weFormOperRecordService.syncAddClickRecord(recordId, formId, userId, openId, weForm, weFormAdvanceSetting, channelType);
            }
            return FormContentVO.builder().recordId(recordId)
                    .weFormModel(WeFormModel.convert2WeFormModel(weForm))
                    .limitSubmitFlag(!notLimitCommit)
                    .closeSubmitFlag(!notCloseSubmit)
                    .submitActionType(weFormAdvanceSetting.getSubmitActionType())
                    .actionInfoParam(JSON.parseObject(weFormAdvanceSetting.getActionInfoParamJson(), ActionInfoParamDTO.class))
                    .build();
        } catch (InterruptedException e) {
            log.error("[智能表单-H5获取表单内容] 线程中断, formId:{}, e:{}", formId, ExceptionUtils.getMessage(e));
            Thread.currentThread().interrupt();
            throw new CustomException(ResultTip.TIP_GET_FORM_ERROR);
        } catch (Exception e) {
            log.error("[智能表单-H5获取表单内容] 查询异常, formId:{}, e{}", formId, ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_GET_FORM_ERROR);
        }
    }

    @Override
    public void commit(FormCommitDTO formCommitDTO) {
        log.info("[智能表单-H5提交表单] formCommitDTO:{}", formCommitDTO);
        if (formCommitDTO == null || formCommitDTO.getRecordId() == null || StringUtils.isAnyBlank(formCommitDTO.getFormResult())) {
            log.info("[智能表单-H5提交表单] 参数缺失");
            throw new CustomException(ResultTip.TIP_NEED_SHORT_CODE);
        }
        // 查询表单操作记录
        WeFormOperRecord weFormOperRecord = weFormOperRecordService.getOne(new LambdaQueryWrapper<WeFormOperRecord>().eq(WeFormOperRecord::getId, formCommitDTO.getRecordId()));
        if (weFormOperRecord == null) {
            log.error("[智能表单-H5提交表单] 查询表单操作记录异常, recordId:{}, formCommitResult:{}", formCommitDTO.getRecordId(), formCommitDTO.getFormResult());
            throw new CustomException(ResultTip.TIP_GET_FORM_ERROR);
        }
        // 查询表单
        WeForm weForm = weFormMapper.selectById(weFormOperRecord.getFormId());
        if (weForm == null) {
            log.error("[智能表单-H5提交表单] 查询表单操异常, recordId:{}, formId:{}", formCommitDTO.getRecordId(), weFormOperRecord.getFormId());
            throw new CustomException(ResultTip.TIP_GET_FORM_ERROR);
        }
        // 查询表单设置
        WeFormAdvanceSetting weFormAdvanceSetting = settingService.getOne(new LambdaQueryWrapper<WeFormAdvanceSetting>().eq(WeFormAdvanceSetting::getFormId, weFormOperRecord.getFormId()));
        if (weFormAdvanceSetting == null) {
            log.error("[智能表单-H5提交表单] 查询表单设置异常, recordId:{}, formCommitResult:{}", formCommitDTO.getRecordId(), formCommitDTO.getFormResult());
            throw new CustomException(ResultTip.TIP_GET_FORM_ERROR);
        }
        boolean notLimitCommit = isNotLimitCommit(weForm, weFormAdvanceSetting, weFormOperRecord);
        boolean dateIsValid = isNotCloseSubmit(weForm, weFormAdvanceSetting);
        if (notLimitCommit && dateIsValid) {
            weFormOperRecordService.addCommitRecord(formCommitDTO, weForm, weFormAdvanceSetting, weFormOperRecord);
        }
    }

    @Override
    public PromotionalVO promotion(Long formId, HttpServletResponse response) {
        if (formId == null) {
            throw new CustomException(TIP_GET_FORM_ERROR);
        }
        LoginUser loginUser = LoginTokenService.getLoginUser();
        // 长链
        String url = genFormUrl(formId, loginUser.getCorpId(), loginUser.getUserId(), FormChannelEnum.PROMOTION.getCode());
        // 短链
        String shortUrl = genShortUrl(url, formId, loginUser.getCorpId(), loginUser.getUserId(), FormChannelEnum.PROMOTION.getCode());
        String qrBase64 = qrCodeHandler.generateBase64(url);

        // 构建推广对象VO
        List<PromotionalType.BasePromotional> wayList = new ArrayList<>();

        // link
        PromotionalType.LinkPromotional linkPromotional = new PromotionalType.LinkPromotional();
        linkPromotional.setType(PromotionalType.LINK.getCode());
        linkPromotional.setLinkUrl(shortUrl);

        // qrcode
        PromotionalType.QrCodePromotional qrCodePromotional = new PromotionalType.QrCodePromotional();
        qrCodePromotional.setType(PromotionalType.QR_CODE.getCode());
        qrCodePromotional.setQrCodeUrl(qrBase64);
        wayList.add(linkPromotional);
        wayList.add(qrCodePromotional);
        return new PromotionalVO(wayList);
    }

}

