package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.pojo.LoginDto;
import com.Blog.pojo.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface UserService extends IService<User> {
    Result<User> login(LoginDto loginDto, HttpServletResponse response);
    Result<String> logout();
    Result<Page<User>> page(int currentPage, int pageSize, String name);
    Result<String> updateStatus(Long id, int status);
    Result<String> deleteUser(Long id);
    Result<String> saveUser(User user);
    Result<String> updateUser(User user);
    void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response);
}
