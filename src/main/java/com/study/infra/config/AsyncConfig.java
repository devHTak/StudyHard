package com.study.infra.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer{@Override
	
	public Executor getAsyncExecutor() {
		// TODO Auto-generated method stub
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int processors = Runtime.getRuntime().availableProcessors();
		log.info("create pool with core {}, max {}", processors, processors * 2);
		
		executor.setCorePoolSize(processors);		// 안정적으로 생기는 스레드 양
		executor.setMaxPoolSize(processors * 2);	// 대기줄 이후에 덤으로 생길 수 있는 스레드 양
		executor.setQueueCapacity(50);				// pool size 이후에 대기 줄 용량
		executor.setKeepAliveSeconds(60); 			// max pool만큼 덤으로 생성했을 때 유지 시간
		executor.setThreadNamePrefix("AsyncExecutor-"); // Thread 이름 설정
		executor.initialize();						// initializer를 호출해야 한다.
		return executor;
	}

}
