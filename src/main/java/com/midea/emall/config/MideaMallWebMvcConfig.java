package com.midea.emall.config;

import com.midea.emall.common.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 描述： 配置地址映射
 */
@Configuration
public class MideaMallWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + Constant.FILE_UPLOAD_DIR);
        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/admin/");

    }

}
