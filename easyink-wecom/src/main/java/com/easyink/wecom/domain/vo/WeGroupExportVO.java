package com.easyink.wecom.domain.vo;

import cn.hutool.core.util.StrUtil;
import com.easyink.common.annotation.Excel;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.vo.wegrouptag.WeGroupTagRelDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

/**
 * ClassName： WeGroupExportVO
 *
 * @author wx
 * @date 2022/10/8 16:15
 */
@Data
@ApiModel(value = "导出客户群数据实体")
@AllArgsConstructor
@NoArgsConstructor
public class WeGroupExportVO extends WeGroup {

    @ApiModelProperty("标签")
    @Excel(name="标签",sort = 4)
    private String tags;

    /**
     * 构造导出实体
     */
    public WeGroupExportVO(WeGroup weGroup){
        BeanUtils.copyProperties(weGroup, this);
        this.setGroupLeaderName(weGroup.getGroupLeaderName() + "/" +weGroup.getMainDepartmentName());
        this.tags = CollectionUtils.isEmpty(weGroup.getTagList()) ? StringUtils.EMPTY : weGroup.getTagList().stream().map(WeGroupTagRelDetail::getName).collect(Collectors.joining(StrUtil.COMMA));
    }
}
