package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.pojo.Echarts;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface EchartsService extends IService<Echarts> {
    Result<List<Echarts>> getUserEcharts();
}
