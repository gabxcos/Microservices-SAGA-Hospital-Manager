package com.gcostanzoweb.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(p -> p
					.path("/patient/**")
					.uri("http://patient-service:2222"))
			.route(p -> p
					.path("/booking/**")
					.uri("http://booking-service:3333"))
			.route(p -> p
					.path("/surgery/**")
					.uri("http://surgery-service:4444"))
			.route(p -> p
					.path("/triage/**")
					.uri("http://triage-service:5555"))
			.build();
	}

}
