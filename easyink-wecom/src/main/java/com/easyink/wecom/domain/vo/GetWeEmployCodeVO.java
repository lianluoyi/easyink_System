package com.easyink.wecom.domain.vo;

import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeTag;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 类名：GetWeEmployCodeVO
 *
 * @author Society my sister Li
 * @date 2021-10-18 16:01
 */
@Data
public class GetWeEmployCodeVO extends WeEmpleCode {

    @ApiModelProperty("主部门名称")
    private String mainDepartmentName;

    @ApiModelProperty("使用员工")
    @NotEmpty(message = "员工信息不能为空")
    private List<WeEmpleCodeUseScop> weEmpleCodeUseScops;

    @ApiModelProperty("扫码标签")
    private List<WeEmpleCodeTag> weEmpleCodeTags;

    @ApiModelProperty("素材列表")
    private List<AddWeMaterialDTO> materialList;

    private String useUserName;

    private String mobile;
}
