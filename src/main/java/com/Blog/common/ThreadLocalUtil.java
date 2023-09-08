package com.Blog.common;


import java.util.HashMap;
import java.util.Map;

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


    //线程不安全
    private static final Map<Long,String> map=new HashMap<>();
    public static String getMap(Long userId){
        return map.get(userId);
    }
    public static void setMap(Long userId,String value){
        map.put(userId,value);
    }
    public static void clearMap(){
        map.clear();
    }
}
