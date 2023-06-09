package com.easyink.wecom.domain.vo.moment;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.moment.MomentStrategy;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 类名： MomentStrategyListVO
 *
 * @author 佚名
 * @date 2022/1/7 9:38
 */
@Data
@ApiModel("获取规则组列表VO")
public class MomentStrategyListVO extends WeResultDTO {
    private List<MomentStrategy> strategy;
}
