package com.easyink.wecom.domain.dto.form;

import com.easyink.wecom.domain.enums.form.FormComponentType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 表单提交DTO
 *
 * @author wx
 * 2023/1/14 17:26
 **/
@Data
@NoArgsConstructor
public class FormCommitDTO {

    /**
     * 操作记录id
     */
    @NotNull
    private Long recordId;

    @NotBlank
    @ApiModelProperty("填写结果")
    private String formResult;

    @ApiModelProperty("普通评分")
    private List<Integer> scoreValueList;

    @ApiModelProperty("nps评分")
    private List<Integer> npsValueList;

}
