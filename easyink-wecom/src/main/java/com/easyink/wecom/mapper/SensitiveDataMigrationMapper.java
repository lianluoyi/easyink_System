package com.easyink.wecom.mapper;

import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import com.easyink.wecom.domain.entity.WeMapConfig;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 敏感数据迁移Mapper
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Mapper
public interface SensitiveDataMigrationMapper {

    // ==================== WeUser 表迁移 ====================
    
    /**
     * 分页查询WeUser数据
     */
    List<WeUser> selectWeUserByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量更新WeUser敏感字段
     */
    int batchUpdateWeUser(@Param("list") List<WeUser> list);
    
    /**
     * 获取WeUser总记录数
     */
    int countWeUser();
    
    /**
     * 获取WeUser已迁移记录数
     */
    int countWeUserMigrated();

    // ==================== WeFlowerCustomerRel 表迁移 ====================
    
    /**
     * 分页查询WeFlowerCustomerRel数据（根据ID滚动分页）
     */
    List<WeFlowerCustomerRel> selectWeFlowerCustomerRelByPage(@Param("lastId") Long lastId, @Param("limit") int limit);
    
    /**
     * 批量更新WeFlowerCustomerRel敏感字段
     */
    int batchUpdateWeFlowerCustomerRel(@Param("list") List<WeFlowerCustomerRel> list);
    
    /**
     * 获取WeFlowerCustomerRel总记录数
     */
    int countWeFlowerCustomerRel();
    
    /**
     * 获取WeFlowerCustomerRel已迁移记录数
     */
    int countWeFlowerCustomerRelMigrated();

    // ==================== WeCustomerExtendPropertyRel 表迁移 ====================
    
    /**
     * 分页查询WeCustomerExtendPropertyRel数据（使用LIMIT OFFSET分页）
     */
    List<WeCustomerExtendPropertyRel> selectWeCustomerExtendPropertyRelByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量更新WeCustomerExtendPropertyRel敏感字段
     */
    int batchUpdateWeCustomerExtendPropertyRel(@Param("list") List<WeCustomerExtendPropertyRel> list);
    
    /**
     * 获取WeCustomerExtendPropertyRel总记录数
     */
    int countWeCustomerExtendPropertyRel();
    
    /**
     * 获取WeCustomerExtendPropertyRel已迁移记录数
     */
    int countWeCustomerExtendPropertyRelMigrated();

    // ==================== WeBatchTagTaskDetail 表迁移 ====================
    
    /**
     * 分页查询WeBatchTagTaskDetail数据（根据ID滚动分页）
     */
    List<WeBatchTagTaskDetail> selectWeBatchTagTaskDetailByPage(@Param("lastId") Long lastId, @Param("limit") int limit);
    
    /**
     * 批量更新WeBatchTagTaskDetail敏感字段
     */
    int batchUpdateWeBatchTagTaskDetail(@Param("list") List<WeBatchTagTaskDetail> list);
    
    /**
     * 获取WeBatchTagTaskDetail总记录数
     */
    int countWeBatchTagTaskDetail();
    
    /**
     * 获取WeBatchTagTaskDetail已迁移记录数
     */
    int countWeBatchTagTaskDetailMigrated();

    // ==================== WeCorpAccount 表迁移 ====================
    
    /**
     * 分页查询WeCorpAccount数据（根据ID滚动分页）
     */
    List<WeCorpAccount> selectWeCorpAccountByPage(@Param("lastId") Long lastId, @Param("limit") int limit);
    
    /**
     * 批量更新WeCorpAccount敏感字段
     */
    int batchUpdateWeCorpAccount(@Param("list") List<WeCorpAccount> list);
    
    /**
     * 获取WeCorpAccount总记录数
     */
    int countWeCorpAccount();
    
    /**
     * 获取WeCorpAccount已迁移记录数
     */
    int countWeCorpAccountMigrated();

    // ==================== WeMapConfig 表迁移 ====================
    
    /**
     * 分页查询WeMapConfig数据（根据ID滚动分页）
     */
    List<WeMapConfig> selectWeMapConfigByPage(@Param("lastId") Long lastId, @Param("limit") int limit);
    
    /**
     * 批量更新WeMapConfig敏感字段
     */
    int batchUpdateWeMapConfig(@Param("list") List<WeMapConfig> list);
    
    /**
     * 获取WeMapConfig总记录数
     */
    int countWeMapConfig();
    
    /**
     * 获取WeMapConfig已迁移记录数
     */
    int countWeMapConfigMigrated();

    // ==================== WeOpenConfig 表迁移 ====================
    
    /**
     * 分页查询WeOpenConfig数据（使用LIMIT OFFSET分页）
     */
    List<WeOpenConfig> selectWeOpenConfigByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量更新WeOpenConfig敏感字段
     */
    int batchUpdateWeOpenConfig(@Param("list") List<WeOpenConfig> list);
    
    /**
     * 获取WeOpenConfig总记录数
     */
    int countWeOpenConfig();
    
    /**
     * 获取WeOpenConfig已迁移记录数
     */
    int countWeOpenConfigMigrated();

    // ==================== 通用方法 ====================
    
    /**
     * 获取表的迁移统计信息
     */
    Map<String, Object> getTableMigrationStats(@Param("tableName") String tableName);

    /**
     * 查询we_user 表地址字段分页
     * @param offset
     * @param limit
     * @return
     */
    List<WeUser> selectWeUserAddressByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 批量更新we_user 地址字段
     * @param list 列表
     */
    void batchUpdateWeUserAddress(@Param("list") List<WeUser> list);

    /**
     *  查询we_flower_customer_rel 地址字段
     * @param lastId lastId
     * @param limit limit
     * @return 列表
     */
    List<WeFlowerCustomerRel> selectWeFlowerCustomerRelAddressByPage(@Param("lastId") Long lastId, @Param("limit") int limit);

    /**
     * 批量更新we_flower_customer_rel 地址字段
     * @param list 列表
     */
    void batchUpdateWeFlowerCustomerRelAddress(@Param("list") List<WeFlowerCustomerRel> list);

    /**
     * 查询we_customer_extend_property_rel 表地址字段分页
     * @param offset offset
     * @param limit limit
     * @return 列表
     */
    List<WeCustomerExtendPropertyRel> selectWeCustomerExtendPropertyRelAddressByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 批量更新we_customer_extend_property_rel 地址字段
     * @param list 列表
     */
    void batchUpdateWeCustomerExtendPropertyRelAddress(@Param("list") List<WeCustomerExtendPropertyRel> list);

}