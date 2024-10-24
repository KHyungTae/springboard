package com.project.springboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").setCachePeriod(60 * 60 * 24 * 365);
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCachePeriod(60 * 60 * 24 * 365);
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").setCachePeriod(60 * 60 * 24 * 365);
		registry.addResourceHandler("/thumbnailDir/**").addResourceLocations("file:///C:/mine/thumbnailDir/");
		registry.addResourceHandler("/fileDir/**").addResourceLocations("file:///C:/mine/fileDir/");
		registry.addResourceHandler("/smarteditorDir/**").addResourceLocations("file:///C:/mine/smarteditorDir/");
	}

	
}
