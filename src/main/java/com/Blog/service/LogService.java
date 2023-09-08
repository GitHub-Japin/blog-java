package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.pojo.Category;
import com.Blog.model.pojo.OptLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface LogService extends IService<OptLog> {
    Result<Page<OptLog>> page(int currentPage, int pageSize, String title);
    Result<String> deleteOptLog(Long id);
}
