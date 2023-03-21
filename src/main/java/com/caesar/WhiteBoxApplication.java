package com.caesar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caesar.mapper")
public class WhiteBoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhiteBoxApplication.class, args);
    }

}
