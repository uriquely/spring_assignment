package com.kh.spring.member.controller;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.exception.MemberException;
import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * Model
 * 
 * - view단에서 처리할 데이터 저장소. Map객체
 * 1. Model<<interface>>
 * 		- viewName 리턴
 * 		- addAttribute(k,v)
 * 2. ModelMap
 * 		- viewName 리턴
 * 		- addAttribute(k,v)
 * 3. ModelAndView : 
 * 		- viewName(jsp위치, redirect Location) 포함, ModelAndView객체 리턴
 * 		- addObject(k,v)
 * 		- RedirectAttributes와 함께 사용하지 말 것.
 * 
 * @ModelAttribute
 * 
 * 1. 메소드 레벨
 * 		- 해당메소드의 리턴값을 model에 저장해서 모든 요청에 사용.
 * 2. 메소드 매개변수에 지정
 * 		- model에 저장된 동일한 이름의 속성이 있는 경우 getter로 사용
 * 		- 해당매개변수를 model속성으로 저장
 *		- 커맨드객체에 @ModelAttribute(속성명)으로 지정
 *      - 단순 사용자 입력값은 @RequestParam으로 처리
 * 
 * @author level
 *
 */
@Slf4j
@Controller
@RequestMapping("/member")
@SessionAttributes(value = {"loginMember", "anotherValue"})
public class MemberController {
	
	//@Slf4j에 의해 생성되는 코드
	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@ModelAttribute("common")
	public Map<String, Object> common(){
		log.info("ModelAttribute - common 실행!");
		Map<String, Object> map = new HashMap<>();
		map.put("adminEmail", "admin@spring.kh.com");
		return map;
	}

	@GetMapping("/memberEnroll.do")
	public void memberEnroll() {
		//viewTranslator에 의해서 요청 url에서 view단 jsp주소를 추론한다.
		
	}
	
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes redirectAttr) {
		log.info("member = {}", member);
		
		try {
			
			//0. 암호화처리
			String rawPassword = member.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			log.info("rawPassword = {}", rawPassword);
			log.info("encodedPassword = {}", encodedPassword);
			member.setPassword(encodedPassword);
			
			//1. 업무로직
			int result = memberService.insertMember(member);
			String msg = result > 0 ? "회원 등록 성공" : "회원 등록 실패";
			//2. 사용자피드백 준비 및 리다이렉트
			redirectAttr.addFlashAttribute("msg", msg);
			
		} catch(Exception e) {
			//1. 로깅작업
			log.error(e.getMessage(), e);
			//2. 다시 spring container에 던질 것.
			throw e;
		}
		
		return "redirect:/";
	}
	
	/**
	 * 
	 * 커맨드객체 이용시 사용자입력값(String)을 특정필드타입으로 변환할 editor객체를 설정
	 * 
	 * @param binder
	 * 
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//Member.birtyday:java.sql.Date 타입 처리
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//커스텀에디터 생성 : allowEmpty - true (빈문자열을 null로 변환처리 허용)
		PropertyEditor editor = new CustomDateEditor(sdf, true);
		binder.registerCustomEditor(java.sql.Date.class, editor);
	}
	
	@GetMapping("/login.do")
	public ModelAndView login(ModelAndView mav) {
		// /WEB-INF/views/member/login.jsp를 알아서 찾는다.

		mav.addObject("test", "hello world");
		mav.setViewName("member/login");
		return mav;
	}
	
	@PostMapping("/login.do")
	public String login(
			@RequestParam String id, 
			@RequestParam String password,
			Model model,
			RedirectAttributes redirectAttr
	) {
		try {
			log.info("id = {}, password = {}", id, password);
			//1. 업무로직 : 해당 id의 member조회
			Member member = memberService.selectOneMember(id);
			log.info("member = {}", member);
			log.info("encodedPassword = {}", bcryptPasswordEncoder.encode(password));
			
			//2. 로그인 여부처리
			if(member != null && bcryptPasswordEncoder.matches(password, member.getPassword())) {
				//로그인 성공
				//기본값으로 request scope 속성에 저장.
				//클래스레벨에 @SessionAttributes("loginMember") 지정하면, session scope에 저장
				model.addAttribute("loginMember", member);
			}
			else {
				//로그인 실패
				redirectAttr.addFlashAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
			}
			
			//3. 사용자피드백 및 리다이렉트		
			
			
		} catch (Exception e) {
			//1. logging
			log.error(e.getMessage(), e);
			//2. spring container에게 예외를 다시 던져서 error페이지로 이동시킨다.
			throw e;
		}
		
		return "redirect:/";
	}
	
	/**
	 * @SessionAttributes 를 통한 로그인은
	 * SessionStatus객체를 통해서 사용완료처리함으로 로그아웃한다.
	 * 
	 * @return
	 */
	@GetMapping("/logout.do")
	public String logout(SessionStatus sessionStatus) {
		
		if(!sessionStatus.isComplete())
				sessionStatus.setComplete();
			
		return "redirect:/";
	}
	
	@GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(@ModelAttribute("loginMember") Member loginMember, ModelAndView mav) {
		
		log.info("loginMember = {}", loginMember);
		mav.setViewName("member/memberDetail");
		return mav;
	}
	
	@PostMapping("/memberUpdate.do")
	public String memberUpdate(@ModelAttribute Member member, ModelMap model, RedirectAttributes redirectAttributes){
		try {
			log.debug("member = {} ", member);
				
			//1.비지니스로직 실행
			int result = memberService.updateMember(member);
			
			//2.처리결과에 따라 view단 분기처리
			String msg = "회원정보수정성공!";
			if(result > 0){
				//회원정보 수정 성공시 session객체 갱신
				Member updateMember = memberService.selectOneMember(member.getId());
				model.addAttribute("loginMember", updateMember);
			}
			else {
				throw new MemberException("해당회원이 존재하지 않습니다 : " + member.getId());
			}
			redirectAttributes.addFlashAttribute("msg", msg);
			
		} catch(Exception e) {
			log.error("회원 정보 수정 실패", e);
			throw e;
		}
		return "redirect:/";
	}
}
