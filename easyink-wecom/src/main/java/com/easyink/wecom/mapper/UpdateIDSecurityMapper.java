package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.WeCorpUpdateId;
import com.easyink.wecom.domain.entity.WeCorpUpdateIdDataTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * ClassName： UpdateIDSecurityMapper
 * 用来更新数据库中存储的corpId，external_userId，userid
 *
 * @author wx
 * @date 2022/8/19 9:43
 */
@Mapper
public interface UpdateIDSecurityMapper extends BaseMapper<WeCorpUpdateId> {

    /**
     * 更新corpId
     *
     * @param table         数据库表
     * @param openCorpId
     * @param corpId
     */
    void updateCorpId(@Param("table") String table, @Param("openCorpId") String openCorpId, @Param("corpId") String corpId);

    /**
     * 更新userId和external_userid
     * 更新员工和客户都使用该方法
     *
     * @param dataTable     更新的表参数
     * @param corpId        企业id
     * @param updateId      升级前的userId or external_userId
     * @param openUpdateId  升级后的openUserId or openExternal_userId
     */
    void updateUserIdAndExternalUserId(@Param("dataTable")WeCorpUpdateIdDataTable dataTable, @Param("corpId") String corpId, @Param("updateId") String updateId, @Param("openUpdateId") String openUpdateId);

    /**
     * 插入升级信息
     *
     * @param weCorpUpdateId
     */
    void insertOrUpdate(@Param("weCorpUpdate") WeCorpUpdateId weCorpUpdateId);

    /**
     * 删除表中存储的密文openCorpId数据
     *
     * @param updateTableName
     * @param openCorpId
     */
    void removeOpenCorpId(@Param("table") String updateTableName, @Param("openCorpId") String openCorpId);
}
