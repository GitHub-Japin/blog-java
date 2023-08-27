package com.Blog.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {
    /**
     * 模块名称
     */
    public String name() default "";

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default false;
}
