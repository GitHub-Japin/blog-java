package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/downLoadWithEasyPOI",name = "使用EasyPOI下载Excel")
    public void downLoadWithEasyPOI(HttpServletRequest request, HttpServletResponse response) throws Exception{
        userService.downLoadXlsxWithEayPoi(request,response);
    }

    @RequiresAuthentication//等同于方法subject.isAuthenticated() 结果为true时。
    @GetMapping("/index")
    public Result<User> getUserById() {
        return Result.success(userService.getById(1L));
    }

    @GetMapping("/page")
    public Result<Page<User>> page(int currentPage, int pageSize, String name) {
        Page<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(name), User::getUsername, name);
        userService.page(page, lqw);
        return Result.success(page);
    }

    //更新状态
    @RequiresAuthentication
    @PutMapping("/updateStatus")
    public Result<String> updateStatus(Long id, int status) {
        User user = userService.getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("账号更新成功");
    }

    //删除用户账号
    @RequiresAuthentication
    @DeleteMapping("/delete")
    public Result<String> deleteUser(Long id) {
        User user = userService.getById(id);
        if (user == null) return Result.error("用户不存在");
        userService.removeById(id);
        return Result.success("用户删除成功");
    }

    //添加用户账号
    @RequiresAuthentication
    @PostMapping("/add")
    public Result<String> saveUser(@RequestBody User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername());/*select * from user where username= ''*/
        User users = userService.getOne(lqw);//原本数据库对象
        if (users !=null){
            return Result.error("用户已存在");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setStatus(0);
        user.setCreated(LocalDateTime.now());
        userService.save(user);
        return Result.success("用户添加成功");
    }

    //更新用户信息
    @RequiresAuthentication
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user){//传入name、email、avatar、password、除id、created、status
//        log.error("传入用户为：{}",user);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername());/*select * from user where username= ''得到整行数据*/
        User initUser = userService.getOne(lqw);//原本数据库对象
        if (initUser ==null){
            return Result.error("用户不存在");
        }
        if(initUser.getUsername().equals(user.getUsername())
                && initUser.getAvatar().equals(user.getAvatar())
                && initUser.getEmail().equals(user.getEmail())
                && initUser.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))){
                    return Result.error("当前信息未修改");
        }
        initUser.setUsername(user.getUsername());

//        if(initUser.getUsername().equals(initUser.getUsername())){
//            return Result.error("当前用户名已存在，更新失败");
//        }
        initUser.setAvatar(user.getAvatar());
        initUser.setEmail(user.getEmail());
        initUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        if(userService.updateById(initUser)){
            return Result.success("用户信息更新成功");
        }else {
            return Result.error("用户信息更新失败");
        }
    }
}

