package com.dingtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 钉钉机器人启动
 *
 * @author zetu
 * @date 2022/5/22
 */
@SpringBootApplication
@EnableFeignClients
public class RobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
    }
}
