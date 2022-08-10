package com.easywecom.wecom.mapper.redeemcode;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.entity.redeemcode.RedeemCodeAlarmUser;
import com.easywecom.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import com.easywecom.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDTO;
import com.easywecom.wecom.domain.vo.redeemcode.RedeemCodeAlarmUserVO;
import com.easywecom.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName： WeRedeemCodeActivityMapper
 *
 * @author wx
 * @date 2022/7/5 17:20
 */
@Mapper
@Repository
public interface WeRedeemCodeActivityMapper extends BaseMapper<WeRedeemCodeActivity> {

    /**
     * 查找兑换码活动列表
     *
     * @param weRedeemCodeActivity
     * @param isSuperAdmin
     * @return
     */
    List<WeRedeemCodeActivityVO> selectWeRedeemCodeActivityList(@Param("activity") WeRedeemCodeActivityDTO weRedeemCodeActivity, @Param("isSuperAdmin") boolean isSuperAdmin);

    /**
     * 批量删除兑换码活动
     *
     * @param corpId
     * @param ids       兑换码活动id, 空格分开
     * @return
     */
    int deleteBatchByIds(@Param("corpId") String corpId, @Param("ids") Long[] ids);

    /**
     * 插入警告通知员工
     *
     * @param useUsers
     */
    void insertAlarmUser(@Param("list") List<RedeemCodeAlarmUser> useUsers);

    /**
     * 删除警告通知员工
     *
     * @param activityIds
     */
    void deleteAlarmUser(@Param("ids") Long[] activityIds);

    /**
     * 获取系统登陆人名
     *
     * @param userId
     * @return
     */
    String getSysUserName(@Param("userId") String userId);

    /**
     * 获取库存剩余的兑换码数量
     *
     * @param activityId
     * @return
     */
    Integer getRemainInventory(@Param("activityId") String activityId);

    /**
     * 获取在有效期内的库存数
     *
     * @param activityId
     * @param nowDate    当前时间
     * @return
     */
    Integer getRemainInventoryInEffectiveTime(@Param("activityId") String activityId, @Param("nowDate") String nowDate);

    /**
     * 获取某一活动中兑换码总数
     *
     * @param activityId
     * @return
     */
    Integer getSumInventory(@Param("activityId") String activityId);


    /**
     * 获取告警员工及部门
     *
     * @param corpId
     * @param id
     * @return
     */
    List<RedeemCodeAlarmUserVO> getAlarmUserList(@Param("corpId") String corpId, @Param("id") Long id);
}
