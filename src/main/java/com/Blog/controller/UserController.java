package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/downLoadWithEasyPOI", name = "使用EasyPOI下载Excel")
    public void downLoadWithEasyPOI(HttpServletRequest request, HttpServletResponse response) throws Exception {
        userService.downLoadXlsxWithEayPoi(request, response);
    }

    /*@RequiresAuthentication//等同于方法subject.isAuthenticated() 结果为true时。
    @GetMapping("/index")
    public Result<User> getUserById() {
        return Result.success(userService.getById(1L));
    }*/

    @GetMapping("/page")
    public Result<Page<User>> page(int currentPage, int pageSize, String name) {
        return userService.page(currentPage, pageSize, name);
    }

    //更新状态
    @PutMapping("/updateStatus")
    public Result<String> updateStatus(Long id, int status) {
        return userService.updateStatus(id, status);
    }

    //删除用户账号
    @DeleteMapping("/delete")
    public Result<String> deleteUser(Long id) {
        return userService.deleteUser(id);
    }

    //添加用户账号
    @PostMapping("/add")
    public Result<String> saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    //更新用户信息
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}

