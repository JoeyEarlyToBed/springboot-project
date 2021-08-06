package com.midea.emall.config;

import com.midea.emall.filter.AdminFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminFilterConfig {

    /**
     * 配置过滤器
     * @return
     */
    @Bean
    public AdminFilter adminFilter() {
        return new AdminFilter();
    }

    /**
     * 将过滤器放入链路中
     * @return
     */
    @Bean(name = "adminFilterConf") //不能与类名一样，否则会冲突
    public FilterRegistrationBean adminFilterConfig() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(adminFilter());
        filterRegistrationBean.addUrlPatterns("/admin/category/*");
        filterRegistrationBean.addUrlPatterns("/admin/product/*");
        filterRegistrationBean.addUrlPatterns("/admin/order/*");
        filterRegistrationBean.setName("adminFilterConf");
        return filterRegistrationBean;
    }



}
