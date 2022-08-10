package com.easywecom.wecom.factory.impl;

import com.easywecom.common.constant.WeConstans;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeCallBackEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 类名： 异步任务完成通知
 *
 * @author 佚名
 * @date 2021/8/26 20:31
 */
@Service("batch_job_result")
@Slf4j
public class WeEventBatchJobResultImpl implements WeCallBackEventFactory {

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        String jobType = message.getBatchJob().getJobType();

        switch (jobType) {
            //增量更新成员
            case WeConstans.SYNC_USER:
                log.info("增量更新成员");
                break;
            //全量覆盖成员
            case WeConstans.REPLACE_USER:
                break;
            //邀请成员关注
            case WeConstans.INVITE_USER:
                break;
            //全量覆盖部门
            case WeConstans.REPLACE_PARTY:
                break;
            default:
                break;
        }
    }
}
