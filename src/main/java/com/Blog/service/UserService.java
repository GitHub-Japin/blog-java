package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.pojo.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface UserService extends IService<User> {
    User loginWithSalt1(UserNameLoginDto loginDto, HttpServletResponse response);
    Result<User> loginWithSalt(UserNameLoginDto loginDto, HttpServletResponse response);
    Result<String> logout();
    Result<Page<User>> page(int currentPage, int pageSize, String name);

    Result<String> saveUser(User user);
    Result<String> deleteUser(Long id);
    Result<String> updateStatus(Long id, int status);
    Result<String> updateUser(User user);
    void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response);
}
