package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.wecom.domain.dto.moment.*;
import com.easyink.wecom.domain.vo.MomentStrategyGetVO;
import com.easyink.wecom.domain.vo.moment.*;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名： 客户朋友圈官方接口
 *
 * @author 佚名
 * @date 2022/1/6 14:10
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}", interceptor = WeAccessTokenInterceptor.class)
public interface WeMomentClient {

    /**
     * 创建发表任务
     * <p>
     * 企业和第三方应用可通过该接口创建客户朋友圈的发表任务。
     * 可见范围说明
     * visible_range，分以下几种情况：
     * 若只指定sender_list，则可见的客户范围为该部分执行者的客户，目前执行者支持传userid与部门id列表，注意不在应用可见范围内的执行者会被忽略。
     * 若只指定external_contact_list，即指定了可见该朋友圈的目标客户，此时会将该发表任务推给这些目标客户的应用可见范围内的跟进人。
     * 若同时指定sender_list以及external_contact_list，会将该发表任务推送给sender_list指定的且在应用可见范围内的执行者，执行者发表后仅external_contact_list指定的客户可见。
     * 若未指定visible_range，则可见客户的范围为该应用可见范围内执行者的客户，执行者为应用可见范围内所有成员。
     * 注：若指定external_contact_list列表，则该条朋友圈为部分可见；否则为公开
     * 企业每分钟创建朋友圈的频率：10条/min
     *
     * @param addMomentTaskDTO 请求参数
     * @return {@link AddMomentTaskVO}
     */
    @Post(url = "/externalcontact/add_moment_task")
    AddMomentTaskVO addMomentTask(@Body AddMomentTaskDTO addMomentTaskDTO, @Header("corpid") String corpId);

    /**
     * 获取任务创建结果
     * <p>
     * 由于发表任务的创建是异步执行的，应用需要再调用该接口以获取创建的结果。
     * 只能查询已经提交过的历史任务。
     *
     * @param jobId 异步任务id，最大长度为64字节，由创建发表内容到客户朋友圈任务接口获取
     * @return
     */
    @Get(url = "/externalcontact/get_moment_task_result")
    MomentTaskResultVO getMomentTaskResult(@Query("jobid") String jobId, @Header("corpid") String corpId);

    /**
     * 获取企业全部的发表列表
     * <p>
     * 朋友圈记录的起止时间间隔不能超过30天
     * 在朋友圈发表列表中，按时间只能取到(start_time, end_time)范围内的数据
     * web管理端会展示企业成员所有已经发表的朋友圈（包括已经删除朋友圈），而API接口将不会返回已经删除的朋友圈记录
     *
     * @param momentListDTO 请求参数
     * @return {@link MomentListVO}
     */
    @Post(url = "/externalcontact/get_moment_list")
    MomentListVO getMomentList(@Body MomentListDTO momentListDTO, @Header("corpid") String corpId);

    /**
     * 获取客户朋友圈企业发表的列表
     * <p>
     * 企业和第三方应用可通过该接口获取企业发表的朋友圈成员执行情况
     *
     * @param momentTaskDTO 请求参数
     * @return {@link MomentTaskVO}
     */
    @Post(url = "/externalcontact/get_moment_task")
    MomentTaskVO getMomentTask(@Body MomentTaskDTO momentTaskDTO, @Header("corpid") String corpId);

    /**
     * 获取客户朋友圈发表时选择的可见范围
     * <p>
     * 企业和第三方应用可通过该接口获取客户朋友圈创建时，选择的客户可见范围
     *
     * @param momentCustomerDTO 请求参数
     * @return {@link MomentCustomerVO}
     */
    @Post(url = "/externalcontact/get_moment_customer_list")
    MomentCustomerVO getMomentCustomerList(@Body MomentCustomerDTO momentCustomerDTO, @Header("corpid") String corpId);

    /**
     * 获取客户朋友圈发表后的可见客户列表
     * <p>
     * 企业和第三方应用可通过该接口获取客户朋友圈发表后，可在微信朋友圈中查看的客户列表
     *
     * @param momentCustomerDTO 请求参数
     * @return {@link MomentCustomerVO}
     */
    @Post(url = "/externalcontact/get_moment_send_result")
    MomentCustomerVO getMomentSendResult(@Body MomentCustomerDTO momentCustomerDTO, @Header("corpid") String corpId);

    /**
     * 获取客户朋友圈的互动数据
     * <p>
     * 企业和第三方应用可通过此接口获取客户朋友圈的互动数据。
     *
     * @param momentCommentsDTO 请求参数
     * @return {@link MomentCommentsVO}
     */
    @Post(url = "/externalcontact/get_moment_comments")
    MomentCommentsVO getMomentComments(@Body MomentCommentsDTO momentCommentsDTO, @Header("corpid") String corpId);

    /**
     * 获取规则组列表
     * <p>
     * 企业可通过此接口获取企业配置的所有客户朋友圈规则组id列表。
     *
     * @param momentStrategyDTO 请求参数
     * @return {@link MomentStrategyListVO}
     */
    @Post(url = "/externalcontact/moment_strategy/list")
    MomentStrategyListVO momentStrategyList(@Body MomentStrategyDTO momentStrategyDTO, @Header("corpid") String corpId);


    /**
     * 获取规则组详情
     * <p>
     * 企业可以通过此接口获取某个客户朋友圈规则组的详细信息。
     *
     * @param momentStrategyGetDTO 请求参数
     * @return {@link MomentStrategyGetVO}
     */
    @Post(url = "/externalcontact/moment_strategy/get")
    MomentStrategyGetVO momentStrategyGet(@Body MomentStrategyGetDTO momentStrategyGetDTO, @Header("corpid") String corpId);


}
