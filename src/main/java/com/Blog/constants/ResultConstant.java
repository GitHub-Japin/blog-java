package com.Blog.constants;

public class ResultConstant {
    public final static Integer SuccessCode =200;
    public final static Integer FailCode =400;
    public final static String SuccessMsg ="操作成功";
    public final static String FailMsg ="操作失败";

    public final static String INTERNAL_SERVER_ERROR ="服务器内部错误";
    public final static Integer UNAUTHORIZED =401;
    public final static String UNAUTHORIZEDMsg ="未登录或登录信息过期";

    public final static String TokenTTLMsg ="token已失效,请重新登录";
    public final static String TokenMsg="未登录或登录信息过期";

    public final static String AccountNotExist = "账号不存在";
    public final static String PasswordNotCorrect = "密码不正确";
    public final static Integer AccountLockCode=1;
    public final static Integer AccountUnLockCode=0;
    public final static String AccountLock = "账号已被禁用";
    public final static String AccountLogout="账号退出成功";

    public final static String UserNotExitMsg ="用户不存在";
    public final static String UserExistMsg ="用户已存在";
    public final static String UserLockMsg ="用户处于锁定状态";



}
