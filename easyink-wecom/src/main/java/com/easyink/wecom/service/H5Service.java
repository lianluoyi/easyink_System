package com.easyink.wecom.service;

import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;

import java.util.List;

/**
 * 中间页h5service
 * @author tigger
 * 2025/1/15 9:30
 **/
public interface H5Service {
    /**
     * 获取原活码标签列表
     * @param originEmpleId 原活码id
     * @return 标签列表
     */
    WeEmpleCodeVO getOriginEmpleTag(String originEmpleId, String corpId);

    /**
     * 查询标签列表
     * @param weTagGroup
     * @param corpId
     * @return
     */
    List<WeTagGroup> selectWeTagGroupList(WeTagGroup weTagGroup, String corpId);

    /**
     * 查询客户扩展属性
     *
     * @param property 查询条件
     * @param corpId
     * @return
     */
    List<WeCustomerExtendProperty> getCustomerExtendPropertyList(WeCustomerExtendProperty property, String corpId);

    /**
     * 获取客户专属活码配置的标签列表
     *
     * @param originEmpleId 生成专属活码的原活码id
     * @param weTagGroup        标签组查询条件
     * @return
     */
    List<WeTagGroup> customerLinkTagList(String originEmpleId, WeTagGroup weTagGroup);

    /**
     * 查询员工活码的专属活码页面配置
     *
     * @param empleCodeId 员工活码ID
     * @param corpId 企业ID
     * @return 配置信息
     */
    Integer getTagGroupValid(Long empleCodeId, String corpId);
}
