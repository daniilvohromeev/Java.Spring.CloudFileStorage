package com.main.authserver;

import com.main.authserver.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableDiscoveryClient
public class AuthServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

}
