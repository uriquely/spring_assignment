package com.kh.spring.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.demo.model.service.DemoService;
import com.kh.spring.demo.model.vo.Dev;

import lombok.extern.slf4j.Slf4j;

/*
#컨트롤러클래스 메소드가 가질수 있는 파라미터

* HttpServletRequest
* HttpServletResponse
* HttpSession
* 
* java.util.Locale : 요청에 대한 Locale
* InputStream/Reader : 요청에 대한 입력스트림
* OutputStream/Writer : 응답에 대한 출력스트림. ServletOutputStream, PrintWriter

* @PathVariable: 요청url중 일부를 매개변수로 취할 수 있다.
* @RequestParam
* @RequestHeader
* @CookieValue
* @RequestBody

뷰에 전달할 모델 데이터 설정
* ModelAndView
* Model
* ModelMap 
* 
* @ModelAttribute : model속성에 대한 getter
* @SessionAttribute : session속성에 대한 getter
* 
* SessionStatus: @SessionAttribute로 등록된 속성에 대하여 사용완료(complete)처리
* Command객체 : http요청 파라미터를 커맨드객체에 저장한 VO객체
* Error, BindingResult : Command객체에 저장결과, Command객체 바로 다음위치시킬것.

기타
* MultipartFile : 업로드파일 처리 인터페이스. CommonsMultipartFile
* RedirectAttributes : DML처리후 요청주소 변경을 위한 redirect를 지원
*/

@Slf4j
@Controller
@RequestMapping("/demo")
public class DemoController {
	
	//@Slf4j에 의해 생성되는 코드
	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private DemoService demoService;
	
	/**
	 * 사용자 요청 url에 따라 호출되는 메소드
	 * 지금까지는 요청 하나당 그에 상응하는 서블릿이 필요했으나, 이제는 그에 상응하는 메소드만 준비해두면 됨
	 * @return
	 */
	@RequestMapping("/devForm.do")
	public String devForm() {
		return "demo/devForm";
	}
	
	//@RequestMapping("/demo/dev1.do") //GET POST 처리
	@RequestMapping(value = "/dev1.do", method = RequestMethod.POST)
	public String dev1(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String name = request.getParameter("name");
		int career = Integer.valueOf(request.getParameter("career"));
		String email = request.getParameter("email");
		String gender = request.getParameter("gender");
		String[] lang = request.getParameterValues("lang");
		
		Dev dev = new Dev(0, name, career, email, gender, lang);
		System.out.println(dev);
		log.info("dev = {}, {}", dev);
		
		//이전 방식(이것도 가능하긴 하다.)
		//request.setAttribute("dev", dev);
		model.addAttribute("dev", dev); //model객체를 통해 jsp에 객체, 값들을 전달
		
		return "demo/devResult";
	}
	
	@RequestMapping(value = "/dev2.do", method = RequestMethod.POST)
	public String dev2(
				@RequestParam(value = "name") String name,
				@RequestParam(value = "career") int career,
				@RequestParam(value = "email") String email,
				@RequestParam(value = "gender", defaultValue = "M") String gender,
				@RequestParam(value = "lang", required = false) String[] lang,
				Model model
			) {
		
		Dev dev = new Dev(0, name, career, email, gender, lang);
		log.info("{}", dev);
		
		model.addAttribute("dev", dev);
		return "demo/devResult";
	}
	
	/**
	 * 커맨드객체 : 사용자 입력값과 일치하는 필드에 값 대입
	 * 커맨드객체는 자동으로 model속성으로 지정된다.
	 * 
	 * [command pattern]
	 * copy, cut, paste등을 하기 위해 Ctrl+C, X, V 단축키들을 사용하는 것처럼
	 * 1:1로 짝지어지는 패턴을 의미한다.
	 * 
	 * name=hong
	 * career=3
	 * email=honggd@naver.com
	 * 이런걸
	 * Dev의 name, career, email에 매칭시키는것
	 * 
	 * @param dev
	 * @return
	 */
	//@RequestMapping(value = "/dev3.do", method = RequestMethod.POST)
	@PostMapping("/dev3.do")
	public String dev3(Dev dev) {
		log.info("{}", dev);
		return "demo/devResult";
	}
	
	@PostMapping("/insertDev.do")
	public String insertDev(Dev dev, RedirectAttributes redirectAttr) {
		log.info("{}", dev);
		
		//커맨드객체때문에 1.사용자입력값을 하던 일이 간편해졌다.
		//1. 업무로직
		int result = demoService.insertDev(dev);
		//2. 사용자피드백 및 리다이렉트(DML)
		String msg = result > 0 ? "Dev 등록 성공!" : "Dev 등록 실패!";
		log.info("처리결과 : {}", msg);
		
		//리다이렉트후 사용자피드백 전달하기
		redirectAttr.addFlashAttribute("msg", msg);
		return "redirect:/"; // /spring
	}

	
	@GetMapping("/devList.do")
	public String devList(Model model) {
		//1. 업무로직
		List<Dev> list = demoService.selectDevList();
		log.info("list = {}", list);
		
		//2. jsp위임
		model.addAttribute("list", list);
		
		return "demo/devList";
	}
	
	@GetMapping("/updateDev.do")
	public String updateDev(@RequestParam(required = true) int no, Model model) {
		//1.업무로직 : Dev 1명 조회
		Dev dev = demoService.selectOneDev(no);
		log.info("dev = {}", dev);
		
		//2. jsp위임
		model.addAttribute("dev", dev);
		
		return "demo/devUpdateForm";
	}
	
	@PostMapping("/updateDev.do")
	public String updateDev(RedirectAttributes redirectAttr, Dev dev) {
		//1.업무로직 : Dev 1명 수정
		log.info("dev = {}", dev);
		int result = demoService.updateDev(dev);
		String msg = result > 0 ? "Dev 수정 성공" : "Dev 수정 실패";
		
		//2. 리다이렉트 및 사용자 피드백
		redirectAttr.addFlashAttribute("msg", msg);
		
		return "redirect:/demo/devList.do";
	}
	
	@PostMapping("/deleteDev.do")
	public String deleteDev(RedirectAttributes redirectAttr, @RequestParam int no) {
		//1.업무로직 : Dev 1명 수정
		int result = demoService.deleteDev(no);
		String msg = result > 0 ? "Dev 삭제 성공" : "Dev 삭제 실패";
		
		//2. 리다이렉트 및 사용자 피드백
		redirectAttr.addFlashAttribute("msg", msg);
		return "redirect:/demo/devList.do";
	}
}
