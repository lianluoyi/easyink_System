package com.easyink.wecom.service.impl.form;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.entity.form.WeFormShortCodeRel;
import com.easyink.wecom.mapper.form.WeFormShortCodeRelMapper;
import com.easyink.wecom.service.form.WeFormShortCodeRelService;
import org.springframework.stereotype.Service;



/**
 * 表单-短链关联表(WeFormShortCodeRel)表服务实现类
 *
 * @author wx
 * @since 2023-01-15 16:35:24
 */
@Service("weFormShortCodeRelService")
public class WeFormShortCodeRelServiceImpl extends ServiceImpl<WeFormShortCodeRelMapper, WeFormShortCodeRel> implements WeFormShortCodeRelService {

}
