package com.study.infra.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.study.modules.notification.NotificationInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	
	private final NotificationInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		List<String> staticResources = Arrays.stream(StaticResourceLocation.values())
				.flatMap(StaticResourceLocation::getPatterns)
				.collect(Collectors.toList());
		staticResources.add("/node_moduesl/**");
		
		registry.addInterceptor(interceptor)
			.excludePathPatterns(staticResources);
	}
	
	
	

}
