package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSelectTagScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户专属员工活码选择标签范围表(WeCustomerTempEmpleCodeSelectTagScope)表数据库访问层
 *
 * @author tigger
 * @since 2025-04-29 14:53:33
 */
@Mapper
public interface WeCustomerTempEmpleCodeSelectTagScopeMapper extends BaseMapper<WeCustomerTempEmpleCodeSelectTagScope> {

    /**
     * 是否存在客户专属活码的选择标签范围设置
     * @param originEmpleId 活码id
     * @param corpId 企业id
     * @return
     */
    List<WeCustomerTempEmpleCodeSelectTagScope> selectTagSelectScopeList(@Param("originEmpleId") String originEmpleId, @Param("corpId") String corpId);
}

