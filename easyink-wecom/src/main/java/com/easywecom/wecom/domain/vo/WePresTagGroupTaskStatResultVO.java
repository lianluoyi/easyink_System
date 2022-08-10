package com.easywecom.wecom.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * 老客户标签建群任务客户统计返回Vo
 *
 * @author Society my sister Li
 * @date 2021/09/14 15:00
 */
@Data
@AllArgsConstructor
public class WePresTagGroupTaskStatResultVO {

    private List<WePresTagGroupTaskStatVO> data;

    private Integer total;

}
