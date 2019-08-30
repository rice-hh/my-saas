package com.my.saas.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.my.saas.system", "com.my.saas.common"})
@MapperScan(basePackages = "com.my.saas.**.dao")
public class MySaasSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySaasSystemApplication.class);
    }

}
