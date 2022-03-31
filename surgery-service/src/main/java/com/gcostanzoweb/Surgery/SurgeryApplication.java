package com.gcostanzoweb.Surgery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SurgeryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurgeryApplication.class, args);
	}

}
