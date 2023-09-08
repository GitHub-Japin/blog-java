package com.Blog.constants;

public enum AppHttpCodeEnum {

    // 成功段固定为200
    SUCCESS(200,"操作成功"),
    FAILURE(400,"操作失败"),
    UNAUTHORIZED(401,"无权限操作，请重新登录"),
    // 登录段1~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2,"密码错误"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过期"),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    SERVER_ERROR(503,"服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    AP_USER_DATA_NOT_EXIST(1001,"ApUser数据不存在"),
    DATA_NOT_EXIST(1002,"数据不存在"),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操作"),
    NEED_ADMIND(3001,"需要管理员权限");


    public final static String INTERNAL_SERVER_ERROR ="服务器内部错误";

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

    private final int code;
    private final String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
