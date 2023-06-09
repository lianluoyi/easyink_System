package com.easyink.wecom.domain.vo.moment;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.moment.Moment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： 获取企业全部的发表列表VO
 *
 * @author 佚名
 * @date 2022/1/6 14:18
 */
@Data
@ApiModel("获取企业全部的发表列表VO")
public class MomentListVO extends WeResultDTO {
    @ApiModelProperty("朋友圈列表")
    private List<Moment> moment_list;
}
