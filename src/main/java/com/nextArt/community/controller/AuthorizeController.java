package com.nextArt.community.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nextArt.community.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nextArt.community.DTO.AccessTokenDTO;
import com.nextArt.community.DTO.GitHubUserDTO;
import com.nextArt.community.mapper.UserMapper;
import com.nextArt.community.model.User;
import com.nextArt.community.provider.GitHubProvider;

@Controller
public class AuthorizeController {
	
	@Autowired
	private GitHubProvider gitHubProvider;
	
	@Autowired
	private UserService userService;

	@Value("${github.client.id}")
	private String clientId;
	
	@Value("${github.client.secret}")
	private String clientSecret;
	
	@Value("${github.redirect.uri}")
	private String redirectUri;
	
	@GetMapping("/callback")
	public String callback(@RequestParam(name="code") String code,
						   @RequestParam(name="state") String state,
						   HttpServletResponse response) {
		AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
		accessTokenDTO.setClient_id(clientId);
		accessTokenDTO.setClient_secret(clientSecret);
		accessTokenDTO.setCode(code);
		accessTokenDTO.setRedirect_uri(redirectUri);
		accessTokenDTO.setState(state);
		String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
		GitHubUserDTO gitHubUserDTO = gitHubProvider.getUserDTO(accessToken);
		if (gitHubUserDTO!=null) {
			User user = new User();
			String token = UUID.randomUUID().toString();
			user.setToken(token);
			user.setName(gitHubUserDTO.getName());
			user.setAccountId(String.valueOf(gitHubUserDTO.getId()));
			user.setAvatarUrl(gitHubUserDTO.getAvatarUrl());
			userService.createOrUpdate(user);
			response.addCookie(new Cookie("token", token));
			//登录成功，写入cookie和session
//			request.getSession().setAttribute("user", gitHubUserDTO);
			return "redirect:/";
		}else {
			//登录失败
			return "redirect:/";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response){
		request.getSession().removeAttribute("user");
		//清除cookie
		Cookie cookie  = new Cookie("token",null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "redirect:/";
	}
}
