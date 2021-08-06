package com.midea.emall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan(basePackages = "com.midea.emall.model.dao")
@EnableSwagger2
@EnableCaching
public class EmallApplication {

    public static void main(String[] args) {

        SpringApplication.run(EmallApplication.class, args);
    }

}
