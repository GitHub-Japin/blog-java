package com.Blog.common;

import com.Blog.constants.ResultConstant;
import com.Blog.model.pojo.User;
import org.apache.shiro.SecurityUtils;

/**
 * @ClassName : AuthUserOpt
 * @Description : AuthUserOpt
 * @Author : Japin
 * @Date: 2023-09-30 18:01
 */
public class AuthUserOpt {
    public static Boolean authOpt() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return user.getId() == 1L;
    }
}
