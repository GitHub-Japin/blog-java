package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.pojo.Category;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CategoryService extends IService<Category> {
    Result<Page<Category>> page(int currentPage, int pageSize, String title);
    Result<String> deleteCategory(Long id);
    Result<String> saveCategory(Category category);
    Result<String> updateCategory(Category category);
    Result<List<Category>> categoryList(Category category);

    Result<String> remove(Long id);

    void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response);
}
