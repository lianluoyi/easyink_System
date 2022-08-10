package com.easyink.web.core.config;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.enums.ResultTip;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 类名: Knife4jConfig
 *
 * @author: 1*+
 * @date: 2021-08-27 15:38
 */
@EnableKnife4j
@Configuration
public class Knife4jConfig {
    private final OpenApiExtensionResolver openApiExtensionResolver;
    private final RuoYiConfig ruoYiConfig;

    @Autowired
    public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver, RuoYiConfig ruoYiConfig) {
        this.openApiExtensionResolver = openApiExtensionResolver;
        this.ruoYiConfig = ruoYiConfig;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .host("http://localhost:8090/")
                .apiInfo(apiInfo())
                .groupName(ruoYiConfig.getVersion())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.easyink.web.controller"))
                .paths(PathSelectors.any()).build()
                .globalResponses(HttpMethod.POST, globalResponses())
                .globalResponses(HttpMethod.GET, globalResponses())
                .globalResponses(HttpMethod.PUT, globalResponses())
                .globalResponses(HttpMethod.DELETE, globalResponses())
                .extensions(openApiExtensionResolver.buildExtensions(ruoYiConfig.getVersion()));

    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("厦门联络易科技有限公司", "www.lianluoyi.cn", "");
        return new ApiInfoBuilder()
                .title("Easyink企业微信SCRM系统")
                .description("Easyink是一款开箱即用的企业微信SCRM系统，为企业提供私域流量营销系统的综合解决方案，帮助企业提高社交客户运营效率。")
                .version(ruoYiConfig.getVersion())
                .contact(contact)
                .build();
    }

    private List<Response> globalResponses() {
        ResultTip[] array = ResultTip.values();
        List<Response> responseList = new ArrayList<>(array.length);
        for (ResultTip resultTip : array) {
            //现阶段错误码超过500的都不是通用的错误码
            if (resultTip.getCode() > 500) {
                break;
            }
            Response response = new Response(String.valueOf(resultTip.getCode()), resultTip.getTipMsg(), true, new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());
            responseList.add(response);
        }
        return responseList;
    }

}
