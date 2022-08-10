package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeMsgTlpScope;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 模板使用人员范围Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
public interface WeMsgTlpScopeMapper extends BaseMapper<WeMsgTlpScope> {
    /**
     * 根据默认欢迎消息id查询作用范围用户名称list
     *
     * @param corpId       企业id
     * @param defaultMsgId 默认欢迎语id
     * @return
     */
    List<String> selectUserNameListByDefaultMsgId(@Param("corpId") String corpId, @Param("defaultMsgId") Long defaultMsgId);

    /**
     * 查询模板使用人员范围
     *
     * @param id 模板使用人员范围ID
     * @return 模板使用人员范围
     */
    WeMsgTlpScope selectWeMsgTlpScopeById(Long id);

    /**
     * 查询模板使用人员范围列表
     *
     * @param weMsgTlpScope 模板使用人员范围
     * @return 模板使用人员范围集合
     */
    List<WeMsgTlpScope> selectWeMsgTlpScopeList(WeMsgTlpScope weMsgTlpScope);

    /**
     * 新增模板使用人员范围
     *
     * @param weMsgTlpScope 模板使用人员范围
     * @return 结果
     */
    int insertWeMsgTlpScope(WeMsgTlpScope weMsgTlpScope);

    /**
     * 修改模板使用人员范围
     *
     * @param weMsgTlpScope 模板使用人员范围
     * @return 结果
     */
    int updateWeMsgTlpScope(WeMsgTlpScope weMsgTlpScope);

    /**
     * 删除模板使用人员范围
     *
     * @param id 模板使用人员范围ID
     * @return 结果
     */
    int deleteWeMsgTlpScopeById(Long id);

    /**
     * 批量删除模板使用人员范围
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteWeMsgTlpScopeByIds(Long[] ids);


    /**
     * 批量保存模板使用人员范围
     *
     * @param weMsgTlpScopes
     * @return
     */
    int batchInsetWeMsgTlpScope(@Param("weMsgTlpScopes") List<WeMsgTlpScope> weMsgTlpScopes);


    /**
     * 模板id,批量删除
     *
     * @param msgTlpIds
     * @return
     */
    int batchRemoveWeMsgTlpScopesByMsgTlpIds(List<Long> msgTlpIds);

    /**
     * 批量更新
     *
     * @param scopeBatchList
     */
    void batchSaveOrUpdate(@Param("list") List<WeMsgTlpScope> scopeBatchList);

    /**
     * 删除不存在集合中的的记录
     *
     * @param defaultMsgId 欢迎语id
     * @param useUserIds 员工userIds
     */
    void deleteNotInUserIds(@Param("defaultMsgId") Long defaultMsgId, @Param("list") List<String> useUserIds);

}
