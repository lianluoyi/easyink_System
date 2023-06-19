package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.wecom.domain.dto.BatchSaveCustomerExtendPropertyDTO;
import com.easyink.wecom.domain.dto.SaveCustomerExtendPropertyDTO;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.vo.WeCustomerExportVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 类名: 客户扩展属性业务接口
 *
 * @author : silver_chariot
 * @date : 2021/11/10 18:04
 */
public interface WeCustomerExtendPropertyService extends IService<WeCustomerExtendProperty> {

    /**
     * 判断是否存在相同的自定义属性名称
     *
     * @param weCustomerExtendProperty {@link WeCustomerExtendProperty}
     * @return true 存在 false 不存在
     */
    Boolean isUnique(WeCustomerExtendProperty weCustomerExtendProperty);

    /**
     * 保存客户扩展属性
     *
     * @param dto {@link SaveCustomerExtendPropertyDTO}
     * @return updated count
     */
    Integer add(SaveCustomerExtendPropertyDTO dto);

    /**
     * 保存客户扩展属性
     *
     * @param property {@link WeCustomerExtendProperty }
     * @return updated count
     */
    Integer add(WeCustomerExtendProperty property);

    /**
     * 对扩展属性进行校验
     *
     * @param property {@link WeCustomerExtendProperty}
     */
    void validate(WeCustomerExtendProperty property);

    /**
     * 删除客户扩展属性
     *
     * @param corpId 企业ID
     * @param ids    需要删除的属性id数组
     */
    @Transactional(rollbackFor = Exception.class)
    void del(String corpId, String[] ids);

    /**
     * 获取扩展属性列表
     *
     * @param property {@link WeCustomerExtendProperty}
     * @return 列表
     */
    List<WeCustomerExtendProperty> getList(WeCustomerExtendProperty property);

    /**
     * 获取扩展属性id和详情的映射,k:id v:详情
     *
     * @param property {@link WeCustomerExtendProperty}
     * @return 扩展属性id和详情的映射, k:id v:详情
     */
    Map<Long, WeCustomerExtendProperty> getMap(WeCustomerExtendProperty property);

    /**
     * 初始化系统默认字段 (初始化企业的时候需要调用)
     *
     * @param corpId   企业id
     * @param createBy 创建人,不传则默认admin
     * @return true or false
     */
    Boolean initSysProperty(String corpId, String createBy);

    /**
     * 编辑客户自定义属性
     *
     * @param dto {@link SaveCustomerExtendPropertyDTO}
     */
    void edit(SaveCustomerExtendPropertyDTO dto);

    /**
     * 编辑客户自定义属性
     *
     * @param property {@link WeCustomerExtendProperty}
     */
    void edit(WeCustomerExtendProperty property);

    /**
     * 批量更新
     *
     * @param dto {@link BatchSaveCustomerExtendPropertyDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    void editBatch(BatchSaveCustomerExtendPropertyDTO dto);


    /**
     * 批量更新
     *
     * @param list   {@link List<WeCustomerExtendProperty> }
     * @param corpId 企业ID
     */
    @Transactional(rollbackFor = Exception.class)
    void editBatch(List<WeCustomerExtendProperty> list, String corpId);

    /**
     * 为列表的每条数据设置扩展属性 名称和值的映射
     *
     * @param corpId             企业ID
     * @param exportCustomerList {@link List<WeCustomerExportVO>} 需要导出的客户集合
     * @param selectedProperties 选择的（需要导出）字段名
     */
    void setKeyValueMapper(String corpId, List<WeCustomerExportVO> exportCustomerList, List<String> selectedProperties);

    /**
     * 根据extendProperties 和 已有的自定义属性和多选值映射,获取客户的 自定义字段名称->所有值用,隔开 的映射
     *
     * @param extendProperties {@link List<BaseExtendPropertyRel>}
     * @param extendPropMap    自定义字段id->详情的映射
     * @param optionMap        多选值id->值的映射
     * @return 自定义字段名称->所有值用,隔开 的映射
     */
    Map<String, String> mapPropertyName2Value(List<BaseExtendPropertyRel> extendProperties, Map<Long, WeCustomerExtendProperty> extendPropMap, Map<Long, ExtendPropertyMultipleOption> optionMap);

    /**
     * 根据extendProperties 和 已有的自定义属性和多选值映射,获取客户的 自定义字段属性->所有值用,隔开 的映射
     *
     * @param extendProperties {@link List<BaseExtendPropertyRel>}
     * @param extendPropMap    自定义字段id->详情的映射
     * @param optionMap        多选值id->值的映射
     * @return 自定义字段->所有值用,隔开 的映射
     */
    Map<WeCustomerExtendProperty, String> mapProperty2Value(List<BaseExtendPropertyRel> extendProperties, Map<Long, WeCustomerExtendProperty> extendPropMap, Map<Long, ExtendPropertyMultipleOption> optionMap);


    /**
     * 根据extendProperties 和 企业id,获取客户的 自定义字段属性->所有值用,隔开 的映射
     *
     * @param extendProperties {@link List<BaseExtendPropertyRel>}
     * @param corpId           企业id
     * @return 自定义字段->所有值用,隔开 的映射
     */
    Map<WeCustomerExtendProperty, String> mapProperty2Value(List<BaseExtendPropertyRel> extendProperties, String corpId);

    /**
     * 为客户设置扩展字段和对应的值
     *
     * @param corpId 企业id
     * @param list   客户列表
     */
    void setExtendPropertyForCustomers(String corpId, List<WeCustomerVO> list);
}
