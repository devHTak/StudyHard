package com.study.modules.main;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.study.modules.account.Account;
import com.study.modules.account.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
	
	@ExceptionHandler
	public String handlerRuntimeException(@CurrentUser Account account, HttpServletRequest req, RuntimeException e) {
		if(account != null) {
			log.info("'{}' requested '{}'", account.getNickname(), req.getRequestURI());
		} else {
			log.info("requested '{}'", req.getRequestURI());
		}
		log.error("bad request", e);
		
		return "error";
	}

}
