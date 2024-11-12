package com.easyink.wecom.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskResultEntity;
import com.easyink.wecom.domain.model.customer.CustomerUserNameModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
@Repository
public interface WeMomentTaskResultMapper extends BaseMapper<WeMomentTaskResultEntity> {

    /**
     * 更新发布信息
     * @param momentTaskId 任务id
     * @param userId 用户id
     * @param publishStatus 发布状态
     * @param publishTime 发布时间
     */
    void updatePublishInfo(@Param("momentTaskId") Long momentTaskId, @Param("userId") String userId, @Param("publishStatus") Integer publishStatus, @Param("publishTime") Date publishTime);

    /**
     * 批量保存或更新结果
     *
     * @param resultEntityList {@link List<WeMomentTaskResultEntity>}
     */
    void batchSaveOrUpdateResult(@Param("list") List<WeMomentTaskResultEntity> resultEntityList);

    /**
     * 根据员工名称关键词搜索朋友圈详情id列表
     * @param momentTaskId 朋友圈id
     * @param corpId 企业id
     * @param userNameKeyword 员工昵称关键词
     * @return 朋友圈详情id列表
     */
    List<Long> selectIdListLikeUserNameKeyword(@Param("momentTaskId") Long momentTaskId, @Param("corpId") String corpId, @Param("userNameKeyword") String userNameKeyword);

    /**
     * 根据朋友圈任务的员工id, 查询关联的客户昵称
     * @param userIdList 员工id
     * @param corpId 企业id
     * @param momentTaskId 朋友圈id
     * @return 客户昵称列表
     */
    List<CustomerUserNameModel> selectCustomerInfo(@Param("userIdList") List<String> userIdList, @Param("corpId") String corpId, @Param("momentTaskId") Long momentTaskId);
}
