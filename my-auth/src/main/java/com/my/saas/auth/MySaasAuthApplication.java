package com.my.saas.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.my.saas.auth", "com.my.saas.common"})
@MapperScan(basePackages = "com.my.saas.**.dao")
public class MySaasAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySaasAuthApplication.class, args);
    }

}
