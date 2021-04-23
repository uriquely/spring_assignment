package com.kh.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

 
@Slf4j
@Component
@Aspect
public class TimeAspect {
	
	@Pointcut("execution(* com.kh.spring.memo.controller.MemoController.insertMemo(..))")
	public void TimePointcut() {}
	
	@Around("TimePointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinpoint) throws Throwable{
        String methodName = joinpoint.getSignature().getName();
        log.debug("메소드 이름 출력 테스트 {}", methodName);
        
        StopWatch stopWatch = new StopWatch();
        Object obj = null;

		try {
			stopWatch.start();
			obj = joinpoint.proceed();
		}

		finally {
			stopWatch.stop();
			log.debug("{} 메소드 실행시간은 {}ms입니다.", methodName, stopWatch.getTotalTimeMillis());
		}

        return obj; 
    }
}