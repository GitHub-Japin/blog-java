package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.dto.Echarts;
import com.Blog.model.dto.UserEcharts;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EchartsService extends IService<Echarts> {
    Result<List<Echarts>> getCategoryEcharts();
    Result<List<UserEcharts>> getUserEcharts();
}
