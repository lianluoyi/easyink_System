package com.easywecom.wecom.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.dto.moment.CreateMomentTaskDTO;
import com.easywecom.wecom.domain.dto.moment.MomentUserCustomerDTO;
import com.easywecom.wecom.domain.dto.moment.SearchMomentContentDTO;
import com.easywecom.wecom.domain.entity.moment.WeMomentTaskEntity;
import com.easywecom.wecom.domain.vo.moment.MomentTotalVO;
import com.easywecom.wecom.domain.vo.moment.MomentUserCustomerVO;
import com.easywecom.wecom.domain.vo.moment.SearchMomentVO;

import java.util.Date;
import java.util.List;

/**
 * 类名： 朋友圈任务信息表接口
 *
 * @author 佚名
 * @date 2022-01-07 18:01:50
 */
public interface WeMomentTaskService extends IService<WeMomentTaskEntity> {
    /**
     * 创建朋友圈任务
     *
     * @param createMomentTaskDTO 实体
     * @param loginUser 登录用户
     */
    void createMomentTask(CreateMomentTaskDTO createMomentTaskDTO, LoginUser loginUser);

    /**
     * 更新企业朋友圈任务创建状态
     *
     * @param weMomentTaskEntity 实体
     */
    void updateTaskStatus(WeMomentTaskEntity weMomentTaskEntity);

    /**
     * 查询未发布的朋友圈任务
     * @return {@link List<WeMomentTaskEntity>}
     */
    List<WeMomentTaskEntity> listOfNotPublish(Date subDay,Boolean isExpire);

    /**
     *
     * @param weMomentTaskEntity
     * @param attachments
     */
    /**
     * 开始创建朋友圈任务（定时任务调用）
     *
     * @param weMomentTaskEntity    任务实体
     * @param attachments           附件（包含文本）
     */
    void startCreatMoment(WeMomentTaskEntity weMomentTaskEntity,List<WeWordsDetailEntity> attachments);

    /**s
     * 查询定时任务
     * @param now 当前时间
     * @return {@link List<WeMomentTaskEntity>}
     */
    List<WeMomentTaskEntity> listOfSettingTask(Date now);

    /**
     * 查询朋友圈发布记录
     * @param searchMomentContentDTO 查询条件
     * @param loginUser
     * @return {@link List<SearchMomentVO>}
     */
    List<SearchMomentVO> listOfMomentTask(SearchMomentContentDTO searchMomentContentDTO, LoginUser loginUser);

    /**
     * 查询朋友圈发布记录详情
     * @param momentUserCustomerDTO 查询条件
     * @return {@link List<MomentUserCustomerVO>}
     */
    List<MomentUserCustomerVO> listOfMomentPublishDetail(MomentUserCustomerDTO momentUserCustomerDTO);

    /**
     * 获得朋友圈统计
     * @param momentTaskId 任务id
     * @return {@link MomentTotalVO}
     */
    MomentTotalVO getTotal(Long momentTaskId);

    /**
     * 获取朋友圈任务基础信息
     * @param momentTaskId 任务id
     * @return {@link SearchMomentVO}
     */
    SearchMomentVO getMomentTaskBasicInfo(Long momentTaskId);

    /**
     * 删除朋友圈
     * @param momentTaskId 朋友圈id
     */
    void deleteMoment(Long momentTaskId);

    /**
     * 更新朋友圈
     * @param createMomentTaskDTO 参数
     */
    void updateMoment(CreateMomentTaskDTO createMomentTaskDTO);

    /**
     * 刷新朋友圈执行详情
     * @param momentTaskId 朋友圈任务id
     */
    void refreshMoment(Long momentTaskId);

    /**
     * 更新发布状态
     *
     * @param momentId 朋友圈id
     * @param taskId   任务id
     * @param corpId   企业id
     */
    void updatePublishStatus(String momentId, Long taskId, String corpId);

    /**
     * 更新执行个人朋友圈执行
     * @param momentTaskId 任务id
     * @param userId 员工id
     */
    void updateUserMoment(Long momentTaskId, String userId);

    /**
     * 发送应用消息通知员工执行
     * @param userIds  员工id
     * @param type 任务类型
     * @param sendTime 发送时间
     * @param momentTaskId
     */
    void sendToUser(List<String> userIds, Integer type, String sendTime, Long momentTaskId);

    /**
     * 获得h5朋友圈页面
     * @param momentTaskId 任务id
     * @param userId 员工id
     * @return
     */
    SearchMomentVO getMomentTask(Long momentTaskId, String userId);
}

