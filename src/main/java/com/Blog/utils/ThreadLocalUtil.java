package com.Blog.utils;

public class ThreadLocalUtil {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();
    public static void setData(Long userId){
        THREAD_LOCAL.set(userId);
    }
    public static Long getData(){
        return THREAD_LOCAL.get();
    }
    public static void clean(){
        THREAD_LOCAL.remove();
    }
}
