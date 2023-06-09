package com.easyink.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改排序model
 *
 * @author tigger
 * 2023/1/10 15:45
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSortModel {
    /**
     * 目标id
     */
    private Long id;

    /**
     * 新sort
     */
    private Integer sort;
}
