package com.Blog.service;

import com.Blog.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface UserService extends IService<User> {

    void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response);
}
