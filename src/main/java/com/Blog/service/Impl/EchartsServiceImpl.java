package com.Blog.service.Impl;

import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.dao.EchartsMapper;
import com.Blog.model.dto.echarts.CategoryEchartsDto;
import com.Blog.model.dto.echarts.UserEcharts;
import com.Blog.model.pojo.Blog;
import com.Blog.model.pojo.Category;
import com.Blog.model.dto.echarts.Echarts;
import com.Blog.model.pojo.User;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.Blog.service.EchartsService;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class EchartsServiceImpl extends ServiceImpl<EchartsMapper, Echarts> implements EchartsService {
    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @Override
    @RequiresAuthentication
    @MyLog(name = "分类下博客数量统计请求")
    public Result<List<Echarts>> getCategoryEcharts() {
        QueryWrapper<Blog> BlogWrapper = new QueryWrapper<>();
        BlogWrapper.select("id","category_id");//select id, category_id from blog
        List<Blog> blogList = blogService.list(BlogWrapper);//查出所有blogid,categoryid
        HashMap<Long,Integer> map=new HashMap<>();
        for(Blog blog : blogList){
            map.put(blog.getCategoryId(),map.getOrDefault(blog.getCategoryId(),0)+1);
        }
        List<Echarts> echartsList = new ArrayList<>();
        List<Category> categoryList = categoryService.list();//查出所有分类
        map.forEach((k,v)->{
            Echarts echarts = new CategoryEchartsDto();//多态
            for (Category category :categoryList){
                if (k.equals(category.getId())){
                    echarts.setName(category.getCategoryname());
                    echarts.setCount(map.get(k));
                }
            }
            echartsList.add(echarts);
        });
        return Result.success(echartsList);
    }

    @Override
//    @RequiresAuthentication
    @MyLog(name = "用户发表博客数量统计请求")
    public Result<List<Echarts>> getUserEcharts() {
        QueryWrapper<Blog> blogWrapper = new QueryWrapper<>();
        blogWrapper.select("id","user_id");
        List<Blog> blogList = blogService.list(blogWrapper);
        HashMap<Long, Integer> map=new HashMap<>();
        for(Blog blog : blogList){
            map.put(blog.getUserId(),map.getOrDefault(blog.getUserId(),0)+1);
        }
        List<Echarts> echartsList = new ArrayList<>();
        List<User> userList = userService.list();//查出所有用户
        map.forEach((k,v)->{
            Echarts echarts = new UserEcharts();
            for (User user :userList){
                if (k.equals(user.getId())){
                    echarts.setName(user.getUsername());
                    echarts.setCount(map.get(k));
                }
            }
            echartsList.add(echarts);
        });
        return Result.success(echartsList);
    }
}
