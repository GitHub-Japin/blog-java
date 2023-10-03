package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.Blog;
import com.Blog.model.dto.blog.BlogDto;
import com.Blog.service.BlogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/blogs/page")
    public Result<Page<BlogDto>> clientPage(int currentPage, int pageSize, String title) {
        return blogService.clientPage(currentPage, pageSize, title);
    }

    @GetMapping("/blog/page")
    public Result<Page<BlogDto>> serverPage(int currentPage, int pageSize, String title) {
        return blogService.serverPage(currentPage, pageSize, title);
    }

    @GetMapping("/blog/{id}")
    public Result<Blog> viewDetails(@PathVariable Long id) {
        return blogService.viewDetails(id);
    }


    @GetMapping("/blog/getContent/{id}")
    public Result<String> getBlogContent(@PathVariable Long id) {
        return blogService.getBlogContent(id);
    }

    @PostMapping("/blog/edit")
    public Result<String> editBlog(@RequestBody @Validated Blog blog) {
        return blogService.editBlog(blog);
    }

    @PutMapping("/blog/updateStatus")
    public Result<String> updateStatus(Long id, int status) {
        return blogService.updateStatus(id,status);
    }

    @DeleteMapping("/blog/delete/{id}")
    public Result<String> deleteBlog(@PathVariable Long id) {
        return blogService.deleteBlog(id);
    }
}
