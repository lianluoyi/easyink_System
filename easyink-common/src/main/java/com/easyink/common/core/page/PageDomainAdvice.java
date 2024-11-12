package com.easyink.common.core.page;

import com.github.pagehelper.PageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动邀群repository
 *
 * @author tigger
 * 2023/4/3 17:29
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PageDomainAdvice<T> extends PageDomain implements PageInvoke {


    public PageDomainAdvice() {
        super();
    }

    @Override
    public void page() {
        PageHelper.startPage(this.getPageNum(), this.getPageSize());
    }

    @Override
    public void clear() {
        PageHelper.clearPage();
    }

}
