package com.rabbiter.association;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.rabbiter.association.dao")
@EnableCaching
public class AssociationManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssociationManagerApplication.class, args);
    }
}
