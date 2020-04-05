package com.nextArt.community.controller;

import javax.servlet.http.HttpServletRequest;

import com.nextArt.community.model.Notification;
import com.nextArt.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.nextArt.community.DTO.PaginationDTO;
import com.nextArt.community.model.User;
import com.nextArt.community.service.QuestionService;

@Controller
public class profileController {
	
	@Autowired
	private QuestionService questionService;

	@Autowired
	private NotificationService notificationService;


	@GetMapping("/profile/{action}")
	public String profile(@PathVariable(name="action") String action,
			@RequestParam(name="page",defaultValue="1") Integer page,
			@RequestParam(name="size",defaultValue="5") Integer size,
			Model model,HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		
		if (user == null) {
			return "redirect:/";
		}
		if ("questions".equals(action)) {
			model.addAttribute("section","questions");
			model.addAttribute("sectionName", "我的提问");
			PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
			model.addAttribute("pagination", paginationDTO);
		}else if ("replies".equals(action)) {
			PaginationDTO paginationDTO = notificationService.list(user.getId(),page,size);
			model.addAttribute("section","replies");
			model.addAttribute("pagination", paginationDTO);
			model.addAttribute("sectionName", "最新回复");

		}

		return "profile";
	}
}
