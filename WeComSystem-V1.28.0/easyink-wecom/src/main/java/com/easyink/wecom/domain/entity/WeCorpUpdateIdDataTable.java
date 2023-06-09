package com.easyink.wecom.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName： WeCorpUpdateIdDataTable
 *
 * @author wx
 * @date 2022/8/23 16:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeCorpUpdateIdDataTable implements Serializable {

    /**
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名|条件
     * 要更新的表|修改的列名
     * 要更新的表|修改的列名|条件
     */
    @ApiModelProperty("需要更新的表名")
    private String updateTableName;

    /**
     * 存储corpIdList使用
     *
     * @param updateTableName
     */
    public WeCorpUpdateIdDataTable(String updateTableName) {
        this.updateTableName = updateTableName;
    }

    @ApiModelProperty("存储corp的关联表名")
    private String relTableName;

    @ApiModelProperty("更新表的外键")
    private String updateTableForeignKey;

    @ApiModelProperty("关联表的外键")
    private String relTableForeignKey;

    @ApiModelProperty("需要更新的列名")
    private String updateColumnName;

    @ApiModelProperty("条件")
    private String criterion;

    /**
     * 处理更新的表中存在corpId的 且需要特殊条件的表
     * 要更新的表，修改的列名，条件
     *
     * @param updateTableName
     * @param updateColumnName
     * @param criterion
     */
    public WeCorpUpdateIdDataTable(String updateTableName, String updateColumnName, String criterion) {
        this.updateTableName = updateTableName;
        this.updateColumnName = updateColumnName;
        this.criterion = criterion;
    }

    /**
     * 处理更新的表中存在corpId的表
     * 要更新的表，修改的列名
     *
     * @param updateTableName
     * @param updateColumnName
     */
    public WeCorpUpdateIdDataTable(String updateTableName, String updateColumnName){
        this.updateColumnName = updateColumnName;
        this.updateTableName = updateTableName;
    }

    /**
     * 处理corpId不在要更新的表
     * 要更新的表|带corp_id的主表|关联的外键1|关联的外键2|修改的列名
     *
     * @param updateTableName
     * @param relTableName
     * @param updateTableForeignKey
     * @param relTableForeignKey
     * @param updateColumnName
     */
    public WeCorpUpdateIdDataTable(String updateTableName, String relTableName, String updateTableForeignKey, String relTableForeignKey, String updateColumnName) {
        this.updateTableName = updateTableName;
        this.relTableName = relTableName;
        this.updateTableForeignKey = updateTableForeignKey;
        this.relTableForeignKey = relTableForeignKey;
        this.updateColumnName = updateColumnName;
    }


}
