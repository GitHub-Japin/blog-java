package com.Blog.controller;

import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.pojo.Blog;
import com.Blog.pojo.Category;
import com.Blog.pojo.Echarts;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.Blog.service.EchartsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Autowired
    private EchartsService echartsService;

    @MyLog(name = "统计请求")
    @GetMapping("/categoryEcharts")
    public Result<List<Echarts>> getUserEcharts() {
        return echartsService.getUserEcharts();
    }
}
