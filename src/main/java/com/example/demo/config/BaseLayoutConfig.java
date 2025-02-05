package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
public class BaseLayoutConfig {
	
	@Bean
	 LayoutDialect layoutDialect() {
	    return new LayoutDialect();
	}
}