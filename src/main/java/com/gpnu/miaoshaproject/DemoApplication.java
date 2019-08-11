package com.gpnu.miaoshaproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//增加注解扫描
@SpringBootApplication(scanBasePackages = {"com.gpnu.miaoshaproject"})
@MapperScan("com.gpnu.miaoshaproject.dao")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
