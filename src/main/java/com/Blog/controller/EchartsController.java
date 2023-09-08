package com.Blog.controller;

import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.model.dto.Echarts;
import com.Blog.model.dto.UserEcharts;
import com.Blog.service.EchartsService;
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

    @GetMapping("/categoryEcharts")
    public Result<List<Echarts>> getCategoryEcharts() {
        return echartsService.getCategoryEcharts();
    }

    @GetMapping("/userEcharts")
    public Result<List<UserEcharts>> getUserEcharts(){
        return echartsService.getUserEcharts();
    }
}
