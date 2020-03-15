package com.nextArt.community.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nextArt.community.DTO.PaginationDTO;
import com.nextArt.community.service.QuestionService;

@Controller
public class indexController {
	
	@Autowired
	private QuestionService questionService;
	
	@GetMapping("/")
	public String index(Model model,
			@RequestParam(name="page",defaultValue="1") Integer page,
			@RequestParam(name="size",defaultValue="5") Integer size) {
		
		PaginationDTO pagination = questionService.list(page,size);
		model.addAttribute("pagination",pagination);
		return "index";
	}
}