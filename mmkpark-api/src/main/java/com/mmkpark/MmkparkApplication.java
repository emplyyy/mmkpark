package com.mmkpark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mmkpark.mapper")
public class MmkparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmkparkApplication.class, args);
    }

}
