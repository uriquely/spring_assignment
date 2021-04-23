package com.kh.spring.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Slf4j
 * Simple Logging Facade for java
 * Spring PSA 로깅 관련 추상화 레이어 제공
 * 
 *  - log4j
 *  - java.util.logging
 *  - org.apache.commons.loggin
 *  - Logback
 *  로깅 구현 프레임워크 추상적으로 제어하는 프레임워크
 *
 */

public class Slf4jTest {

	private static final Logger log = LoggerFactory.getLogger(Slf4jTest.class);
	
	public static void main(String[] args) {
		new Slf4jTest().logTest();
		
	}

	private void logTest() {
		
//		log.fatal("fatal"); // slf4j는 fatal level을 지원하지 않는다.
		log.error("error");
		log.warn("warn");
		log.info("info");
		log.debug("debug");
		log.trace("trace");
	}

}
