package com.Blog.service.Impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.Blog.annotation.MyLog;
import com.Blog.common.CustomException;
import com.Blog.utils.RandomStringSaltUtil;
import com.Blog.common.Result;
import com.Blog.constants.ResultConstant;
import com.Blog.dao.UserMapper;
import com.Blog.sercurity.jwt.JwtUtil;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JwtUtil jwtUtil;

    private Boolean IsUserExists(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);/*select * from user where id= ''得到整行数据*/
        User user = getOne(lambdaQueryWrapper);//原本数据库对象
        return user != null;
    }
    private User QueryUser(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);/*select * from user where id= ''得到整行数据*/
        return getOne(lambdaQueryWrapper);
    }

    // 把这里改成返回User，前端才能拿到数据
    public User loginWithSalt1(UserNameLoginDto loginDto, HttpServletResponse response) {
        if (StringUtils.isEmpty(loginDto.getUsername())
                || StringUtils.isEmpty(loginDto.getPassword())) {
            throw new CustomException(ResultConstant.FailMsg);
//            return Result.error(ResultConstant.FailMsg);
        }
        User user = QueryUser(loginDto.getUsername());
        if (user == null || user.getDeleted() == 1) {
            throw new CustomException(ResultConstant.FailMsg);
//            return Result.error(ResultConstant.AccountNotExist);
        }
        if (user.getStatus().equals(ResultConstant.AccountLockCode)) {
            throw new CustomException(ResultConstant.FailMsg);
//            return Result.error(ResultConstant.AccountLock);
        }
        String password = DigestUtils.md5DigestAsHex((loginDto.getPassword() + user.getSalt()).getBytes());
        if (!password.equals(user.getPassword())) {
            throw new CustomException(ResultConstant.FailMsg);
//            return Result.error(ResultConstant.PasswordNotCorrect);
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        //salt与密码不返回前端
        user.setSalt("");
        user.setPassword("");

        return user;
    }

    @Override
    public Result<User> loginWithSalt(UserNameLoginDto loginDto, HttpServletResponse response) {
        if (StringUtils.isEmpty(loginDto.getUsername())
                || StringUtils.isEmpty(loginDto.getPassword())) {
            return Result.error(ResultConstant.FailMsg);
        }
        User user = QueryUser(loginDto.getUsername());
        if (user == null || user.getDeleted() == 1) {
            return Result.error(ResultConstant.AccountNotExist);
        }
        if (user.getStatus().equals(ResultConstant.AccountLockCode)) {
            return Result.error(ResultConstant.AccountLock);
        }
        String password = DigestUtils.md5DigestAsHex((loginDto.getPassword() + user.getSalt()).getBytes());
        if (!password.equals(user.getPassword())) {
            return Result.error(ResultConstant.PasswordNotCorrect);
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        //salt与密码不返回前端
        user.setSalt("");
        user.setPassword("");

        return Result.success(user);
    }

    @Override
    public Result<String> logout() {
        SecurityUtils.getSubject().logout();
        return Result.success(ResultConstant.AccountLogout);
    }

    @Override
    @MyLog(name = "用户分页请求")
    public Result<Page<User>> page(int currentPage, int pageSize, String name) {
        Page<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            lambdaQueryWrapper.like(User::getUsername, name);
        }
        return Result.success(page(page, lambdaQueryWrapper));
    }

    @Override
    @Transactional
    @RequiresAuthentication//等同于方法subject.isAuthenticated() 结果为true时。
    @MyLog(name = "用户状态更新请求")
    public Result<String> updateStatus(Long id, int status) {
        User user = getById(id);
        if (user == null) return Result.error(ResultConstant.UserNotExitMsg);
        user.setStatus(status);
        updateById(user);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户删除请求")
    public Result<String> deleteUser(Long id) {
        User user = getById(id);
        if (user == null) return Result.error(ResultConstant.UserNotExitMsg);
        removeById(id);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @Transactional
//    @RequiresAuthentication
    @MyLog(name = "用户注册请求")
    public Result<String> saveUser(User user) {
        Boolean isUserExists = IsUserExists(user.getUsername());
        if (isUserExists){
            return Result.error(ResultConstant.UserExistMsg);
        }
        String salt = RandomStringSaltUtil.generateRandomString(5);
        user.setSalt(salt);
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword()+salt).getBytes()));
        user.setStatus(ResultConstant.AccountUnLockCode);
        user.setCreated(LocalDateTime.now());
        user.setAvatar("https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg");
        boolean IsSave = save(user);
        if (!IsSave) {
            return Result.error(ResultConstant.FailMsg);
        }
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户更新请求")
    public Result<String> updateUser(User user) {
        //log.error("传入用户为：{}",user);
        User initUser = QueryUser(user.getUsername());
        if (initUser == null) {
            return Result.error(ResultConstant.UserNotExitMsg);
        }
        String salt = initUser.getSalt();
        if (initUser.getUsername().equals(user.getUsername())
                && initUser.getAvatar().equals(user.getAvatar())
                && initUser.getEmail().equals(user.getEmail())
                && initUser.getPassword().equals(DigestUtils.md5DigestAsHex((user.getPassword()+salt).getBytes()))) {
            return Result.error(ResultConstant.FailMsg);
        }
        initUser.setUsername(user.getUsername());
        initUser.setAvatar(user.getAvatar());
        initUser.setEmail(user.getEmail());
        initUser.setPassword(DigestUtils.md5DigestAsHex((user.getPassword()+salt).getBytes()));
        if (updateById(initUser)) {
            return Result.success(ResultConstant.SuccessMsg);
        } else {
            return Result.error(ResultConstant.FailMsg);
        }
    }

    @Override
    @MyLog(name = "用户excel下载请求")
    public void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response) {
        //查询用户数据
        try {
            List<User> userList = list(new QueryWrapper<>());
            //指定导出的格式是高版本的格式
            ExportParams exportParams = new ExportParams("员工信息", "数据", ExcelType.XSSF);
            //直接使用EasyPOI提供的方法
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, userList);
            String filename = "Blog用户信息表.xlsx";
            //设置文件的打开方式和mime类型
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Autowired
    private UserLoginFactory factory;

    public LoginResp login(LoginReq loginReq){
        UserGranter granter = factory.getGranter(loginReq.getType());
        if(granter == null){
            LoginResp loginResp = new LoginResp();
            loginResp.setSuccess(false);
            return loginResp;
        }
        LoginResp loginResp = granter.login(loginReq);
        return loginResp;

        *//*if(loginReq.getType().equals("account")){
            System.out.println("用户名密码登录");

            //执行用户密码登录逻辑

            return new LoginResp();

        }else if(loginReq.getType().equals("sms")){
            System.out.println("手机号验证码登录");

            //执行手机号验证码登录逻辑

            return new LoginResp();
        }else if (loginReq.getType().equals("we_chat")){
            System.out.println("微信登录");

            //执行用户微信登录逻辑

            return new LoginResp();
        }
        LoginResp loginResp = new LoginResp();
        loginResp.setSuccess(false);
        System.out.println("登录失败");
        return loginResp;*//*
    }*/
}
