package com.easyink.wecom.service.impl.form;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.wecom.ServerTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.entity.form.WeFormAdvanceSetting;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.vo.WeServerTypeVO;
import com.easyink.wecom.mapper.form.WeFormAdvanceSettingMapper;
import com.easyink.wecom.mapper.wechatopen.WeOpenConfigMapper;
import com.easyink.wecom.service.We3rdAppService;
import com.easyink.wecom.service.form.WeFormAdvanceSettingService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 表单设置表(WeFormAdvanceSetting)表服务实现类
 *
 * @author tigger
 * @since 2023-01-09 15:00:48
 */
@Service("weFormAdvanceSettingService")
public class WeFormAdvanceSettingServiceImpl extends ServiceImpl<WeFormAdvanceSettingMapper, WeFormAdvanceSetting> implements WeFormAdvanceSettingService {

    private final We3rdAppService we3rdAppService;
    private final WeOpenConfigMapper weOpenConfigMapper;

    @Lazy
    public WeFormAdvanceSettingServiceImpl(We3rdAppService we3rdAppService, WeOpenConfigMapper weOpenConfigMapper) {
        this.we3rdAppService = we3rdAppService;
        this.weOpenConfigMapper = weOpenConfigMapper;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateFormSetting(WeFormAdvanceSetting formSetting, String corpId) {
        if (formSetting == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 如果是自建应用,选默认配置的
        WeServerTypeVO serverType = we3rdAppService.getServerType();
        if(ServerTypeEnum.INTERNAL.getType().equals(serverType.getServerType())){
            // 查询企业默认配置
            WeOpenConfig weOpenConfig = weOpenConfigMapper.selectOne(new LambdaQueryWrapper<WeOpenConfig>()
                    .eq(WeOpenConfig::getCorpId, corpId)
            );
            if(weOpenConfig != null){
                formSetting.setWeChatPublicPlatform(weOpenConfig.getOfficialAccountAppId());
            }
        }
        this.saveOrUpdate(formSetting);
    }
}

