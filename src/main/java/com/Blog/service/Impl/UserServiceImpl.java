package com.Blog.service.Impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.lang.Validator;
import com.Blog.annotation.MyLog;
import com.Blog.common.AuthUserOpt;
import com.Blog.common.CustomException;
import com.Blog.common.Result;
import com.Blog.constants.EmailConstant;
import com.Blog.constants.ResultConstant;
import com.Blog.dao.UserMapper;
import com.Blog.model.dto.login.EmailLoginDto;
import com.Blog.model.dto.login.EmailReq;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.dto.user.UserRegisterDto;
import com.Blog.model.pojo.User;
import com.Blog.sercurity.jwt.JwtUtil;
import com.Blog.service.SendMailService;
import com.Blog.service.UserService;
import com.Blog.utils.RandomStringSaltUtil;
import com.Blog.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
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

    private Boolean isUserExists(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);/*select * from user where id= ''得到整行数据*/
        User user = getOne(lambdaQueryWrapper);//原本数据库对象
        return user != null;
    }

    private User queryUser(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);/*select * from user where id= ''得到整行数据*/
        return getOne(lambdaQueryWrapper);
    }

    private User queryUserByEmail(String email) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail, email);
        return getOne(lambdaQueryWrapper);
    }

    // 把这里改成返回User，前端才能拿到数据
    @Override
    public User loginWithSalt1(UserNameLoginDto loginDto, HttpServletResponse response) {
        /*if (StringUtils.isEmpty(loginDto.getUsername())
                || StringUtils.isEmpty(loginDto.getPassword())) {
            throw new CustomException(ResultConstant.FailMsg);
//            return Result.error(ResultConstant.FailMsg);
        }*/
        User user = queryUser(loginDto.getUsername());
        if (user == null || user.getDeleted() == 1) {
            throw new CustomException(ResultConstant.UserNotExitMsg);
//            return Result.error(ResultConstant.AccountNotExist);
        }
        if (user.getStatus().equals(ResultConstant.AccountLockCode)) {
            throw new CustomException(ResultConstant.AccountLock);
//            return Result.error(ResultConstant.AccountLock);
        }
        String password = DigestUtils.md5DigestAsHex((loginDto.getPassword() + user.getSalt()).getBytes());
        if (!password.equals(user.getPassword())) {
            throw new CustomException(ResultConstant.PasswordNotCorrect);
//            return Result.error(ResultConstant.PasswordNotCorrect);
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        //salt与密码不返回前端
        user.setSalt("");
        user.setPassword("");

        return user;
    }

    @ApiOperation("邮箱登录")
    @Override
    public User loginWithEmail(EmailLoginDto loginDto, HttpServletResponse response) {
        if (StringUtils.isEmpty(loginDto.getEmail())
                || StringUtils.isEmpty(loginDto.getEmailCode())
                || StringUtils.isEmpty(loginDto.getPCode())) {
            throw new CustomException(ResultConstant.FailMsg);
        }
        User user = queryUserByEmail(loginDto.getEmail());
        if (user == null || user.getDeleted() == 1 ||user.getStatus().equals(ResultConstant.AccountLockCode)) {
            throw new CustomException(ResultConstant.FailMsg);
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        //salt与密码不返回前端
        user.setSalt("");
        user.setPassword("");

        return user;
    }
    @Autowired
    private SendMailService sendMailService;

    @Override
    public Boolean sendEmailCode(HttpServletRequest request, EmailLoginDto emailLoginDto) {
        if (request.getSession().getAttribute("verifyCode").equals(emailLoginDto.getPCode())){
            EmailReq req = new EmailReq();
            req.setSendTo(emailLoginDto.getEmail());
            req.setSubject(EmailConstant.EMAIL_CODE);
            return sendMailService.doSendEmailCode(req);
        }else {
            return false;
        }
    }

    @Override
    @ApiOperation("生成图形验证码")
    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成Captcha验证码(定义图形验证码的长、宽、验证码字符数、干扰线宽度)
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 3);
        shearCaptcha.write(response.getOutputStream());//图形验证码写出，可以写出到文件，也可以写出到流
        // 用于后续验证用户输入验证码是否正确，用完可以移除
        request.getSession().setAttribute("verifyCode", shearCaptcha.getCode());
    }

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @ApiOperation("用户注册")
    public Boolean userRegister(UserRegisterDto userRegisterDto) {
        //TODO 转移到service中
        String password = userRegisterDto.getPassword();
        String checkPassword = userRegisterDto.getCheckPassword();
        //校验两次密码是否一致
        if (!password.equals(checkPassword)) {
            return false;
        }

        String username = userRegisterDto.getUsername();
        String mail = userRegisterDto.getMail();
        if (!Validator.isEmail(mail)) {
            return false;
        }
        //TODO 查询数据库校验用户名和mail已被注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username).or().eq(User::getEmail, mail);
        User user = getOne(queryWrapper);
        if (user != null) {
            throw new CustomException("用户名或邮箱已存在");
        }

        //TODO Redis校验验证码
        //Redis key:email_code:邮箱  value: code
        String code = userRegisterDto.getEmailCode();
        String codeValue = redisUtil.get(mail);
        if (!codeValue.equals(code)) {
            throw new CustomException("验证码错误");
        }

        //TODO 插入数据库，如果成功删除Redis中验证码
        redisUtil.delete(mail);
        User targetUser = new User();
        BeanUtils.copyProperties(targetUser, userRegisterDto);
        saveUser(targetUser);

        return true;
    }

    @Override
    public Result<User> loginWithSalt(UserNameLoginDto loginDto, HttpServletResponse response) {
        if (StringUtils.isEmpty(loginDto.getUsername())
                || StringUtils.isEmpty(loginDto.getPassword())) {
            return Result.error(ResultConstant.FailMsg);
        }
        User user = queryUser(loginDto.getUsername());
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
    @MyLog(name = "账号退出")
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
        lambdaQueryWrapper.select(User::getId, User::getUsername, User::getAvatar
                , User::getEmail, User::getStatus, User::getCreated);
        return Result.success(page(page, lambdaQueryWrapper));
    }

    @Override
    @Transactional
    @RequiresAuthentication//等同于方法subject.isAuthenticated() 结果为true时。
    @MyLog(name = "用户状态更新请求")
    public Result<String> updateStatus(Long id, int status) {
        if (!AuthUserOpt.authOpt()) {
            return Result.error(ResultConstant.NOTAUTHMsg);
        }
        User user = getById(id);
        if (user == null) {
            return Result.error(ResultConstant.UserNotExitMsg);
        }
        user.setStatus(status);
        updateById(user);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @Transactional
    @RequiresAuthentication
    @MyLog(name = "用户删除请求")
    public Result<String> deleteUser(Long id) {
        if (!AuthUserOpt.authOpt()) {
            return Result.error(ResultConstant.NOTAUTHMsg);
        }
        User user = getById(id);
        if (user == null) {
            return Result.error(ResultConstant.UserNotExitMsg);
        }
        removeById(id);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @Transactional
//    @RequiresAuthentication
    @MyLog(name = "用户注册请求")
    public Result<String> saveUser(User user) {
        Boolean isUserExists = isUserExists(user.getUsername());
        if (isUserExists) {
            return Result.error(ResultConstant.UserExistMsg);
        }
        String salt = RandomStringSaltUtil.generateRandomString(5);
        user.setSalt(salt);
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        user.setStatus(ResultConstant.AccountUnLockCode);
        user.setCreated(LocalDateTime.now());
        if (user.getAvatar() == null) {
            user.setAvatar("https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg");
        }
        boolean isSave = save(user);
        if (!isSave) {
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
        User initUser = queryUser(user.getUsername());
        if (initUser == null) {
            return Result.error(ResultConstant.UserNotExitMsg);
        }
        String salt = initUser.getSalt();
        if (initUser.getUsername().equals(user.getUsername())
                && initUser.getAvatar().equals(user.getAvatar())
                && initUser.getEmail().equals(user.getEmail())
                && initUser.getPassword().equals(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()))) {
            return Result.error(ResultConstant.FailMsg);
        }
        initUser.setUsername(user.getUsername());
        initUser.setAvatar(user.getAvatar());
        initUser.setEmail(user.getEmail());
        initUser.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        if (updateById(initUser)) {
            return Result.success(ResultConstant.SuccessMsg);
        } else {
            return Result.error(ResultConstant.FailMsg);
        }
    }

    @Override
    @MyLog(name = "用户excel下载请求")
    public void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response) {
        if (!AuthUserOpt.authOpt()) {
            return;
        }
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
}
