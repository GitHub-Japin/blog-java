package com.Blog.strategy;

import com.Blog.model.dto.LoginReq;
import com.Blog.model.vo.LoginResp;
import org.springframework.stereotype.Component;

/**
 *策略：账号登录
 */
//@Component
public class AccountGranter implements UserGranter{

	@Override
	public LoginResp login(LoginReq loginReq) {
		System.out.println("策略:登录方式为账号登录");
		// TODO
		// 执行业务操作

		return new LoginResp();
	}

}
