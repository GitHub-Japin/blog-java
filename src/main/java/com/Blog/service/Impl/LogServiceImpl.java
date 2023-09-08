package com.Blog.service.Impl;

import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.dao.LogMapper;
import com.Blog.model.pojo.Category;
import com.Blog.model.pojo.OptLog;
import com.Blog.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, OptLog> implements LogService {
    @Override
    @RequiresAuthentication
    @MyLog(name = "日志分页请求")
    public Result<Page<OptLog>> page(int currentPage, int pageSize, String title) {
        Page<OptLog> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<OptLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(title), OptLog::getAnnotation, title);
        page(page, queryWrapper);
        return Result.success(page);
    }

    @Override
    @MyLog(name = "日志删除请求")
    @RequiresAuthentication
    @Transactional
    public Result<String> deleteOptLog(Long id) {
        removeById(id);
        return Result.success("日志删除成功");
    }
}
