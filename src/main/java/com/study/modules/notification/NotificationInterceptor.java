package com.study.modules.notification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.study.modules.account.Account;
import com.study.modules.account.UserAccount;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {
	
	private final NotificationService notificationService;
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView mav) throws Exception {
		
		// TODO Auto-generated method stub
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(mav != null && !this.isRedirectView(mav) && authentication != null && authentication.getPrincipal() instanceof UserAccount) {
			Account account = ((UserAccount)authentication.getPrincipal()).getAccount();
			long count = notificationService.countByAccountAndChecked(account, false);
			
			mav.addObject("hasNotification", count > 0);
		}
	}
	
	private boolean isRedirectView(ModelAndView mav) {
		return mav.getViewName().startsWith("redirect:") || mav.getView() instanceof RedirectView;
	}
}
