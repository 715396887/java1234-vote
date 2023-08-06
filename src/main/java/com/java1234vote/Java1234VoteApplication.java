package com.java1234vote;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@MapperScan("com/java1234vote/mapper")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Java1234VoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(Java1234VoteApplication.class, args);
    }
}
