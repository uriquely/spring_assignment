package com.kh.spring.common.aop;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LoginLoggerAspect {

	@Pointcut("execution(* com.kh.spring.member.controller..login(..))")
	public void pointcut() {}
	
	/**
	 * 
	 * 포인트컷은 pointcut
	 * returning이라는 속성을 주어야 한다.
	 * returning 조인포인트가 리턴하는 객체를 어디에다가 담을것이냐?
	 * 
	 */
	@AfterReturning(pointcut = "pointcut()", returning = "returnObj")
	public void advice(JoinPoint joinPoint, Object returnObj) {
		log.debug("returnObj = {}", returnObj);
		
		ModelAndView mav = (ModelAndView) returnObj;
		Map<String, Object> model = mav.getModel();
		if(model.containsKey("loginMember")) {
			Member loginMember = (Member) model.get("loginMember");
			log.info("{}[{}]님이 로그인했습니다.", loginMember.getName(), loginMember.getId());
		}
	}
	
}




