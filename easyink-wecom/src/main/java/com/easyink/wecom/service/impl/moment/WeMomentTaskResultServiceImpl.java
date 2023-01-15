package com.easyink.wecom.service.impl.moment;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.entity.moment.WeMomentTaskResultEntity;
import com.easyink.wecom.mapper.moment.WeMomentTaskResultMapper;
import com.easyink.wecom.service.moment.WeMomentTaskResultService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WeMomentTaskResultServiceImpl extends ServiceImpl<WeMomentTaskResultMapper, WeMomentTaskResultEntity> implements WeMomentTaskResultService {


    @Override
    public void updatePublishInfo(Long momentTaskId, String userId, Integer publishStatus, Date publishTime) {
        if (momentTaskId == null || StringUtils.isBlank(userId) || publishStatus == null) {
            log.warn("[更新朋友圈任务结果发布信息] 参数错误,不执行更新");
            return;
        }
        this.baseMapper.updatePublishInfo(momentTaskId, userId, publishStatus, publishTime);
    }
}