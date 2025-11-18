package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.autotag.AutoTagConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.encrypt.StrategyCryptoUtil;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDetailDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskDetailVO;
import com.easyink.wecom.mapper.autotag.WeBatchTagTaskDetailMapper;
import com.easyink.wecom.service.autotag.WeBatchTagTaskDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 批量打标签任务详情（we_batch_tag_task_detail）服务实现类
 *
 * @author lichaoyu
 * @date 2023/6/5 10:22
 */
@Service("WeBatchTagTaskDetailService")
public class WeBatchTagTaskDetailServiceImpl extends ServiceImpl<WeBatchTagTaskDetailMapper, WeBatchTagTaskDetail> implements WeBatchTagTaskDetailService {


    private final WeBatchTagTaskDetailMapper weBatchTagTaskDetailMapper;

    public WeBatchTagTaskDetailServiceImpl(WeBatchTagTaskDetailMapper weBatchTagTaskDetailMapper) {
        this.weBatchTagTaskDetailMapper = weBatchTagTaskDetailMapper;
    }

    /**
     * 查询批量打标签任务详情列表
     *
     * @param dto {@link BatchTagTaskDetailDTO}
     * @return 结果
     */
    @Override
    public List<BatchTagTaskDetailVO> selectBatchTaskDetailList(BatchTagTaskDetailDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        dto.setCustomerInfoEncrypt(StrategyCryptoUtil.encrypt(dto.getCustomerInfo()));
        return weBatchTagTaskDetailMapper.selectBatchTaskDetailList(dto);
    }

    /**
     * 导出批量打标签任务详情列表
     *
     * @param dto {@link BatchTagTaskDetailDTO}
     * @return
     */
    @Override
    public AjaxResult exportBatchTaskDetailList(BatchTagTaskDetailDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        dto.setCustomerInfoEncrypt(StrategyCryptoUtil.encrypt(dto.getCustomerInfo()));
        List<BatchTagTaskDetailVO> resultList = weBatchTagTaskDetailMapper.selectBatchTaskDetailList(dto);
        if (CollectionUtils.isEmpty(resultList)) {
            throw new CustomException(ResultTip.TIP_NO_EXPORT_DATA);
        }
        // 导出
        ExcelUtil<BatchTagTaskDetailVO> util = new ExcelUtil<>(BatchTagTaskDetailVO.class);
        return util.exportExcel(resultList, dto.getTaskName() + AutoTagConstants.EXPORT_TASK_DETAIL_NAME);
    }
}
