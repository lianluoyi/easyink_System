package com.easywecom.wecom.domain.resp;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.Data;

import java.util.List;


/**
 * 类名: 企业微信分页接口返回通用参数
 *
 * @author : silver_chariot
 * @date : 2021/11/1 11:17
 */
@Data
public abstract class WePageBaseResp<T> extends WeResultDTO {
    /**
     * 分页游标，再下次请求时填写以获取之后分页的记录，如果已经没有更多的数据则返回空
     */
    private String next_cursor;
    /**
     * 全部分页数据列表
     */
    private List<T> totalList;

    /**
     * 获取企微API分页 单次请求结果的集合
     *
     * @return 单次请求后的分页数据
     */
    public abstract List<T> getPageList();

    /**
     * 对返回的数据进行处理
     *
     * @param corpId 企业ID
     */
    public abstract void handleData(String corpId);



}
