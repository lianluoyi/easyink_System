package com.easyink.wecom.service.redeemcode;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDeleteDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCodeActivity;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDTO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;

import java.util.List;

/**
 * 兑换码活动Service
 * 类名： WeRedeemCodeActivityService
 *
 * @author wx
 * @date 2022/7/4 11:00
 */
public interface WeRedeemCodeActivityService extends IService<WeRedeemCodeActivity> {

    /**
     * 新增兑换码活动
     *
     * @param weRedeemCodeActivityDTO
     * @return
     */
    Long saveReemCodeActivity(WeRedeemCodeActivityDTO weRedeemCodeActivityDTO);

    /**
     * 分页查询兑换码活动列表
     *
     * @param weRedeemCodeActivityDTO
     * @return
     */
    List<WeRedeemCodeActivityVO> getReemCodeActivityList(WeRedeemCodeActivityDTO weRedeemCodeActivityDTO);

    /**
     * 编辑更新兑换码活动
     *
     * @param weRedeemCodeActivityDTO
     */
    void updateWeRedeemCodeActivity(WeRedeemCodeActivityDTO weRedeemCodeActivityDTO);

    /**
     * 批量逻辑删除兑换码活动
     *
     * @param corpId
     * @param deleteDTO
     * @return
     */
    int batchRemoveRedeemCodeActivity(String corpId, WeRedeemCodeActivityDeleteDTO deleteDTO);

    /**
     * 获取兑换码活动详情
     *
     * @param corpId
     * @param id        兑换码活动id
     * @return
     */
    WeRedeemCodeActivityVO getRedeemCodeActivity(String corpId, Long id);
}
