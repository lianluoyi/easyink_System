package com.easyink.wecom.service.impl.form;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeFormUseRecord;
import com.easyink.wecom.domain.query.form.FormQuery;
import com.easyink.wecom.domain.vo.form.FormSimpleInfoVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.form.WeFormUseRecordMapper;
import com.easyink.wecom.service.form.WeFormService;
import com.easyink.wecom.service.form.WeFormUseRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表单使用记录表(WeFormUseRecord)表服务实现类
 *
 * @author tigger
 * @since 2023-01-13 10:12:32
 */
@Service("weFormUseRecordService")
public class WeFormUseRecordServiceImpl extends ServiceImpl<WeFormUseRecordMapper, WeFormUseRecord> implements WeFormUseRecordService {

    private final WeFormService weFormService;

    @Lazy
    public WeFormUseRecordServiceImpl(WeFormService weFormService) {
        this.weFormService = weFormService;
    }

    @Override
    public List<FormSimpleInfoVO> pageRecord(Integer sourceType, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_ERROR);
        }
        // 查询当前员工最近使用的表单id,五条
        List<Integer> formIdList = this.baseMapper.selectLimit5UseRecordFormIdList(LoginTokenService.getLoginUser().getUserId(), corpId);
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }

        // 根据条件查询表单
        FormQuery formQuery = new FormQuery();
        formQuery.setEnableFlag(Boolean.TRUE);
        formQuery.setSourceType(sourceType);
        formQuery.setFormIdList(formIdList);
        // 这里查出来的默认是根据主键id的排序规则,从小到大的顺序,需要跟上面的 formIdList 的顺序统一一下
        List<FormSimpleInfoVO> collect = weFormService.getList(formQuery, corpId)
                .stream().map(it -> {
                    FormSimpleInfoVO formSimpleInfoVO = new FormSimpleInfoVO();
                    formSimpleInfoVO.setFormId(it.getId());
                    formSimpleInfoVO.setFormName(it.getFormName());
                    formSimpleInfoVO.setDescription(it.getDescription());
                    formSimpleInfoVO.setHeadImageUrl(it.getHeadImageUrl());
                    return formSimpleInfoVO;
                }).collect(Collectors.toList());

        // 处理顺序,按照formIdList的顺序填充查询出来的数据
        List<FormSimpleInfoVO> resultList = new ArrayList<>();
        for (Integer id : formIdList) {
            collect.stream().filter(it -> it.getFormId().equals(id)).findFirst().ifPresent(resultList::add);
        }
        return resultList;
    }

    @Override
    public void saveRecord(Integer formId, String userId, String externalUserId, String corpId) {
        if (formId == null || StringUtils.isAnyBlank(userId, externalUserId, corpId)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        WeFormUseRecord weFormUseRecord = new WeFormUseRecord();
        weFormUseRecord.setFormId(formId);
        weFormUseRecord.setUserId(userId);
        weFormUseRecord.setExternalUserId(externalUserId);
        weFormUseRecord.setCorpId(corpId);
        this.baseMapper.insert(weFormUseRecord);
    }

    @Override
    public void saveRecord(Integer formId, String userId, String externalUserId) {
        this.saveRecord(formId, userId, externalUserId, LoginTokenService.getLoginUser().getCorpId());
    }
}

