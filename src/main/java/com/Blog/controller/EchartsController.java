package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.pojo.Blog;
import com.Blog.pojo.Category;
import com.Blog.pojo.Echarts;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categoryEcharts")
    public Result<List<Echarts>> getUserEcharts() {
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
            Echarts echarts = new Echarts();
            for (Category category :categoryList){
                if (k.equals(category.getId())){
                    echarts.setCategoryName(category.getCategoryname());
                    echarts.setCount(map.get(k));
                }
            }
            echartsList.add(echarts);
        });
        return Result.success(echartsList);
    }
}
