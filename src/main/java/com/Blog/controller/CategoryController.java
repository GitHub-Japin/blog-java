package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.Category;
import com.Blog.service.CategoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = "/downLoadWithEasyPOI",name = "使用EasyPOI下载Excel")
    public void downLoadWithEasyPOI(HttpServletRequest request, HttpServletResponse response) throws Exception{
        categoryService.downLoadXlsxWithEayPoi(request,response);
    }

    @GetMapping("/page")
    public Result<Page<Category>> page(int currentPage, int pageSize, String title) {
       return categoryService.page(currentPage, pageSize, title);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
       return categoryService.deleteCategory(id);
    }

    @PostMapping("/add")
    public Result<String> saveCategory(@RequestBody Category category) {
       return categoryService.saveCategory(category);
    }

    @PutMapping("/update")
    public Result<String> updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }

    @GetMapping("/list")
    public Result<List<Category>> categoryList(Category category){//将数据放入下拉框
       return categoryService.categoryList(category);
    }
}

