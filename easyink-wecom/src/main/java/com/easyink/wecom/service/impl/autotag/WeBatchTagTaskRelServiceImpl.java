package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskRel;
import com.easyink.wecom.mapper.autotag.WeBatchTagTaskRelMapper;
import com.easyink.wecom.service.autotag.WeBatchTagTaskRelService;
import org.springframework.stereotype.Service;

/**
 * 批量打标签-标签关联（we_batch_tag_task_rel）服务实现类
 *
 * @author lichaoyu
 * @date 2023/6/5 16:51
 */
@Service("WeBatchTagTaskRelService")
public class WeBatchTagTaskRelServiceImpl extends ServiceImpl<WeBatchTagTaskRelMapper, WeBatchTagTaskRel> implements WeBatchTagTaskRelService {


}
