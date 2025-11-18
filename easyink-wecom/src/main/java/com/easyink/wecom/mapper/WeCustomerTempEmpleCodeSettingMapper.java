package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.entity.WeCustomerTempEmpleCodeSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 客户临时员工活码表(WeCustomerTempEmpleCodeSetting)表数据库访问层
 *
 * @author tigger
 * @since 2025-01-13 13:52:48
 */
@Mapper
public interface WeCustomerTempEmpleCodeSettingMapper extends BaseMapper<WeCustomerTempEmpleCodeSetting> {


    /**
     * 根据state查询客户专属活码设置
     *
     * @param state  state
     * @param corpId
     * @return
     */
    WeCustomerTempEmpleCodeSetting selectByState(@Param("state") String state, @Param("corpId") String corpId);

    /**
     * 删除过期专属活码配置
     *
     * @param expireTime 过期时间
     * @param corpId 企业id
     * @return 专属客户配置列表
     */
    List<WeCustomerTempEmpleCodeSetting> selectExpireByTime(@Param("expireTime") Date expireTime, @Param("corpId") String corpId);

    /**
     * 根据专属活码主键id查询原始的员工活码id
     * @param customerEmployCodeId　专属活码id
     * @return　原活码id
     */
    Long selectOriginalEmpleCodeIdByCustomerEmployCodeIdIncludeDeleteFlag(@Param("customerEmployCodeId") Long customerEmployCodeId);


    /**
     * 根据员工活码id, 查询专属活码state, 用于查询客户身上的state
     * @param empleCodeIdList 员工活码id列表
     * @return
     */
    List<String> selectStateByCustomerEmployCodeIdList(@Param("empleCodeIdList") List<Long> empleCodeIdList);

}

