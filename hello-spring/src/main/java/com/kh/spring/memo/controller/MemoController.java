package com.kh.spring.memo.controller;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.memo.model.service.MemoService;
import com.kh.spring.memo.model.vo.Memo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/memo")
public class MemoController {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MemoService memoService;
	
	/**
	 * AOP의 실행 구조
	 * 
	 * MemoController.memo --------------------> MemoService.selectMemoList
	 * MemoController.memo -----proxy객체-------> Target객체(MemoService.selectMemoList)
	 * 
	 */
	
	@GetMapping("/memo.do")
	public String memoList(Model model) {
		
		//proxy확인
		log.debug("proxy = {}", memoService.getClass());
		//class com.sun.proxy.$Proxy43
		
		List<Memo> list = memoService.selectMemoList();
		log.info("list = {}", list);
		
		model.addAttribute("list", list);
		
		return "memo/memo";
	}
	
	@PostMapping("/insertMemo.do")
	public String insertMemo(Memo memo, RedirectAttributes redirectAttr) {
		
		try {
			log.debug("memo = {}", memo);
			//1. 업무로직
			int result = memoService.insertMemo(memo);
			String msg = "메모 등록 성공";
			//2. 사용자피드백 준비 및 리다이렉트
			redirectAttr.addFlashAttribute("msg", msg);
			
		} catch(Exception e) {
			//1. 로깅작업
			log.error(e.getMessage(), e);
			//2. 다시 spring container에 던질 것.
			throw e;
		}
		
		return "redirect:/memo/memo.do";
	}
}
