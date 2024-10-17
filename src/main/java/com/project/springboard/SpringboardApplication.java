package com.project.springboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

//@ServletComponentScan
@SpringBootApplication
@ComponentScan
public class SpringboardApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringboardApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringboardApplication.class, args);
	}

}
