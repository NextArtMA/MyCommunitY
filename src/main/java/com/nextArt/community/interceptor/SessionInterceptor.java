package com.nextArt.community.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nextArt.community.model.UserExample;
import com.nextArt.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nextArt.community.mapper.UserMapper;
import com.nextArt.community.model.User;

import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private NotificationService notificationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		Cookie[] cookies =  request.getCookies();
		if (cookies!=null && cookies.length!=0) {
			for (Cookie cookie : cookies) {
				if("token".equals(cookie.getName())) {
					String token = cookie.getValue();
					UserExample userExample = new UserExample();
					userExample.createCriteria().andTokenEqualTo(token);
					List<User> users = userMapper.selectByExample(userExample);
					if (users.size()!=0) {
						request.getSession().setAttribute("user", users.get(0));
						long unreadCount = notificationService.unreadCount(users.get(0).getId());
						request.getSession().setAttribute("unreadCount",unreadCount);
					}
					break;
				}
			}
		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
