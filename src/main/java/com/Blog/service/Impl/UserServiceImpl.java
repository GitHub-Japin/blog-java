package com.Blog.service.Impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.dao.UserMapper;
import com.Blog.jwt.JwtUtil;
import com.Blog.pojo.LoginDto;
import com.Blog.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

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
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result<User> login(LoginDto loginDto, HttpServletResponse response) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", loginDto.getUsername());
        User user = getOne(qw);
        if (user == null) {
            return Result.error("账号不存在");
        }
        String password = DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes());
        if (!user.getPassword().equals(password)) {
            return Result.error("密码不正确");
        }
        if(user.getStatus().equals(1)){
            return Result.error("账号已被禁用");
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);

        return Result.success(user);
    }

    @Override
    public Result<String> logout() {
        SecurityUtils.getSubject().logout();
        return Result.success("注销成功");
    }

    @Override
    @MyLog(name = "用户分页请求")
    public Result<Page<User>> page(int currentPage, int pageSize, String name) {
        Page<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(name), User::getUsername, name);
        return Result.success(page(page, lqw));
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户状态更新请求")
    public Result<String> updateStatus(Long id, int status) {
        User user = getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        updateById(user);
        return Result.success("账号更新成功");
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户删除请求")
    public Result<String> deleteUser(Long id) {
        User user = getById(id);
        if (user == null) return Result.error("用户不存在");
        removeById(id);
        return Result.success("用户删除成功");
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户注册请求")
    public Result<String> saveUser(User user) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername());/*select * from user where username= ''*/
        User users = getOne(lqw);//原本数据库对象
        if (users !=null){
            return Result.error("用户已存在");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setStatus(0);
        user.setCreated(LocalDateTime.now());
        save(user);
        return Result.success("用户添加成功");
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户更新请求")
    public Result<String> updateUser(User user) {
        //传入name、email、avatar、password、除id、created、status
        //log.error("传入用户为：{}",user);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, user.getUsername());/*select * from user where username= ''得到整行数据*/
        User initUser = getOne(lqw);//原本数据库对象
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
        if(updateById(initUser)){
            return Result.success("用户信息更新成功");
        }else {
            return Result.error("用户信息更新失败");
        }
    }

    @Override
    @MyLog(name = "用户excel下载请求")
    public void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response) {
        //查询用户数据
        try {
            List<User> userList = userMapper.selectList(new QueryWrapper<>());
            //指定导出的格式是高版本的格式
            ExportParams exportParams = new ExportParams("员工信息", "数据", ExcelType.XSSF);
            //        直接使用EasyPOI提供的方法
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, userList);
            String filename = "用户信息.xlsx";
            //            设置文件的打开方式和mime类型
            ServletOutputStream outputStream = null;

            outputStream = response.getOutputStream();

            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
