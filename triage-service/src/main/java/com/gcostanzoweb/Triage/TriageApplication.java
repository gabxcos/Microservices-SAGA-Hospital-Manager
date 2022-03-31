package com.gcostanzoweb.Triage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class TriageApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriageApplication.class, args);
	}

}
