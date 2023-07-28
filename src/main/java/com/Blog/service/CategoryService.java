package com.Blog.service;

import com.Blog.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
    void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response);
}
