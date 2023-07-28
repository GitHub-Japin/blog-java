package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.pojo.Category;
import com.Blog.pojo.User;
import com.Blog.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        Page<Category> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(title), Category::getCategoryname, title);
        categoryService.page(page, lqw);
        return Result.success(page);
    }

    @RequiresAuthentication
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.remove(id);
        return Result.success("分类删除成功");
    }

    @RequiresAuthentication
    @PostMapping("/add")
    public Result<String> saveCategory(@RequestBody Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getCategoryname, category.getCategoryname());
        Category c = categoryService.getOne(lqw);//原本数据库对象
        if (c !=null){
            return Result.error("分类已存在");
        }
        category.setCreated(LocalDateTime.now());
        categoryService.save(category);
        return Result.success("分类添加成功");
    }

    @RequiresAuthentication
    @PutMapping("/update")
    public Result<String> updateCategory(@RequestBody Category category){
        log.info(category.getCategoryname());
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getId, category.getId());
        Category initCategory = categoryService.getOne(lqw);//原本数据库对象
        if (initCategory ==null){
            return Result.error("分类不存在");
        }
        if(initCategory.getCategoryname().equals(category.getCategoryname())){
            return Result.error("当前信息未修改");
        }
        initCategory.setCategoryname(category.getCategoryname());

        if(categoryService.updateById(initCategory)){
            return Result.success("信息更新成功");
        }else {
            return Result.error("信息更新失败");
        }
    }

    @GetMapping("/list")
    public Result<List<Category>> categoryList(Category category){//将数据放入下拉框
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();//构造条件构造器
        query.orderByAsc(Category::getId).orderByDesc(Category::getCreated);//排序，若前面相同，则按后面
        List<Category> list = categoryService.list(query);
        /*for (Category value : list) {
            System.out.println(value);
        }*/
        return Result.success(list);
    }
}

