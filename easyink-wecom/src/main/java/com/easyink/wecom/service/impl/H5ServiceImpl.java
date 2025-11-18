package com.easyink.wecom.service.impl;

import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSelectTagScope;
import com.easyink.wecom.mapper.WeCustomerTempEmpleCodeSelectTagScopeMapper;
import com.easyink.wecom.mapper.WeEmpleCodeTagMapper;
import com.easyink.wecom.service.H5Service;
import com.easyink.wecom.service.WeCustomerExtendPropertyService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeTagGroupService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * h5service impl
 * @author tigger
 * 2025/1/15 9:31
 **/
@Service
@AllArgsConstructor
public class H5ServiceImpl implements H5Service {

    private final WeEmpleCodeTagMapper weEmpleCodeTagMapper;
    private final WeTagGroupService weTagGroupService;
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;
    private final WeEmpleCodeService weEmpleCodeService;
    private final WeCustomerTempEmpleCodeSelectTagScopeMapper selectTagScopeMapper;
    @Override
    public WeEmpleCodeVO getOriginEmpleTag(String originEmpleId, String corpId) {
        return weEmpleCodeService.selectWeEmpleCodeById(Long.valueOf(originEmpleId), corpId);
    }

    @Override
    public List<WeTagGroup> selectWeTagGroupList(WeTagGroup weTagGroup, String corpId) {

        if(StringUtils.isBlank(corpId)){
            return new ArrayList<>();
        }
        weTagGroup.setCorpId(corpId);
        return weTagGroupService.selectWeTagGroupList(weTagGroup);
    }

    @Override
    public List<WeCustomerExtendProperty> getCustomerExtendPropertyList(WeCustomerExtendProperty property, String corpId) {
        if(StringUtils.isBlank(corpId)){
            return new ArrayList<>();
        }
        property.setCorpId(corpId);

        return weCustomerExtendPropertyService.getList(property);
    }

    @Override
    public List<WeTagGroup> customerLinkTagList(String originEmpleId, WeTagGroup weTagGroup) {

        List<WeCustomerTempEmpleCodeSelectTagScope> scopeLit = selectTagScopeMapper.selectTagSelectScopeList(originEmpleId, weTagGroup.getCorpId());
        if(CollectionUtils.isNotEmpty(scopeLit)){
            return weTagGroupService.selectTagByCustomerLinkTag(scopeLit, weTagGroup, originEmpleId);
        }else{
            return weTagGroupService.selectWeTagGroupList(weTagGroup);
        }

    }

    @Override
    public Integer getTagGroupValid(Long empleCodeId, String corpId) {
        if (empleCodeId == null || StringUtils.isBlank(corpId)) {
            return null;
        }
        WeEmpleCodeVO empleCode = weEmpleCodeService.selectWeEmpleCodeById(empleCodeId, corpId);
        return empleCode != null ? empleCode.getTagGroupValid() : null;
    }
}
