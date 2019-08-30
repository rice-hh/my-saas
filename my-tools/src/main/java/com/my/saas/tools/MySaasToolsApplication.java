package com.my.saas.tools;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.my.saas.tools", "com.my.saas.common"})
@MapperScan(basePackages = "com.my.saas.**.dao")
public class MySaasToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySaasToolsApplication.class);
    }

}
