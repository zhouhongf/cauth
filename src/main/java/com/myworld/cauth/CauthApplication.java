package com.myworld.cauth;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringCloudApplication
public class CauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CauthApplication.class, args);
    }

}
