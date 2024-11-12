package com.easyink.wecom.domain.query.moment;

import com.easyink.wecom.domain.model.moment.MomentResultFilterModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 朋友圈详情查询上下文
 * @author tigger
 * 2023/12/26 14:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentDetailQueryContext {

    /**
     * 企业id
     */
    private String corpId;
    /**
     * 朋友圈任务id
     */
    private Long momentTaskId;
    /***
     * 朋友圈结果id列表
     */
    private MomentResultFilterModel resultFilterModel;

}
