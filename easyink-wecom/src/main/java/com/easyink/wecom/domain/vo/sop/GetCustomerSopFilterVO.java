package com.easyink.wecom.domain.vo.sop;

import com.alibaba.fastjson.JSONArray;
import com.easyink.wecom.domain.WeOperationsCenterCustomerSopFilterEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名：GetCustomerSopFilterVO
 *
 * @author Society my sister Li
 * @date 2021-12-07 22:00
 */
@Data
@ApiModel("客户SOP过滤条件")
public class GetCustomerSopFilterVO extends WeOperationsCenterCustomerSopFilterEntity {

    @ApiModelProperty("过滤属性")
    private JSONArray columnList;

    @ApiModelProperty(value = "所属员工信息")
    private List<SopUserVO> userInfoList;

    @ApiModelProperty(value = "所属部门信息")
    private List<DepartmentVO> departmentInfoList;

    @ApiModelProperty(value = "已打标签列表")
    private List<BaseCustomerSopTagVO> tagList;

    @ApiModelProperty(value = "过滤标签列表")
    private List<BaseCustomerSopTagVO> filterTagList;


    public GetCustomerSopFilterVO(WeOperationsCenterCustomerSopFilterEntity sopFilterEntity){
        this.setId(sopFilterEntity.getId());
        this.setGender(sopFilterEntity.getGender());
        this.setCorpId(sopFilterEntity.getCorpId());
        this.setSopId(sopFilterEntity.getSopId());
        this.setUsers(sopFilterEntity.getUsers());
        this.setTagId(sopFilterEntity.getTagId());
        this.setIncludeTagMode(sopFilterEntity.getIncludeTagMode());
        this.setCloumnInfo(sopFilterEntity.getCloumnInfo());
        this.setFilterTagId(sopFilterEntity.getFilterTagId());
        this.setFilterTagMode(sopFilterEntity.getFilterTagMode());
        this.setStartTime(sopFilterEntity.getStartTime());
        this.setEndTime(sopFilterEntity.getEndTime());
    }
}
