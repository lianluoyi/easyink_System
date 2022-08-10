package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.WeApplicationCenter;
import com.easywecom.wecom.domain.dto.SetApplicationUseScopeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 类名: WeApplicationDetailVO
 *
 * @author: 1*+
 * @date: 2021-10-19 17:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("应用中心应用详情实体")
public class WeApplicationDetailVO extends WeApplicationCenter {

    @ApiModelProperty("应用是否已安装")
    private Boolean installed;

    @ApiModelProperty("应用配置")
    private String config;

    @ApiModelProperty("使用范围")
    private List<SetApplicationUseScopeDTO.UseScope> scopeList;

    public static WeApplicationDetailVO copy(WeApplicationCenter weApplicationCenter) {
        WeApplicationDetailVO weApplicationDetailVO = new WeApplicationDetailVO();
        BeanUtils.copyProperties(weApplicationCenter, weApplicationDetailVO);
        return weApplicationDetailVO;
    }

}
