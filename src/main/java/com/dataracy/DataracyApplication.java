package com.dataracy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataracyApplication {
	/**
	 * 애플리케이션의 진입점으로 Spring Boot 컨텍스트를 부트스트랩하고 실행합니다.
	 *
	 * <p>이 메서드는 SpringApplication.run을 호출하여 DataracyApplication을 기반으로
	 * 스프링 애플리케이션 컨텍스트를 생성하고 애플리케이션을 시작합니다.</p>
	 *
	 * @param args 명령줄 인수(애플리케이션에 전달되는 옵션 및 플래그). null 가능성이 없다고 가정합니다.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DataracyApplication.class, args);
	}
}
