package com.easywecom.wecom.domain.vo.wegrouptag;

import com.easywecom.wecom.domain.WeGroupTag;
import com.easywecom.wecom.domain.WeGroupTagCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 类名：WeGroupTagCategoryVO
 *
 * @author Society my sister Li
 * @date 2021-11-12 17:05
 */
@Data
@ApiModel("查询客户群标签组数据")
public class WeGroupTagCategoryVO extends WeGroupTagCategory {

    private List<WeGroupTag> tagList;
}
