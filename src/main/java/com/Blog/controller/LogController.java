package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.Category;
import com.Blog.model.pojo.OptLog;
import com.Blog.service.CategoryService;
import com.Blog.service.LogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/Log")
public class LogController {
    @Autowired
    private LogService logService;

    @GetMapping("/page")
    public Result<Page<OptLog>> page(int currentPage, int pageSize, String title) {
       return logService.page(currentPage, pageSize, title);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
       return logService.deleteOptLog(id);
    }

}

