package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.pojo.Blog;
import com.Blog.pojo.BlogDto;
import com.Blog.service.BlogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@Repository
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
    public Result<Blog> ViewDetails(@PathVariable Long id) {
        return blogService.ViewDetails(id);
    }


    @GetMapping("/blog/getContent/{id}")
    public Result<String> getBlogContent(@PathVariable Long id) {
        return blogService.getBlogContent(id);
    }

    @PostMapping("/blog/edit")
    public Result<String> editBlog(@RequestBody Blog blog) {
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

    //测试接口
    @GetMapping("/blog/addtest")
    public Result<String> add() {
        for (int i = 0; i < 10; i++) {
            Blog blog = new Blog();
            blog.setId((long) i + 1000);
            blog.setCategoryId(1L);
            blog.setCreated(new Date());
            blog.setStatus(1);
            blog.setUserId(1L);
            blog.setContent("第" + i + "次内容");
            blog.setDescription("第" + i + "次");
            blog.setTitle("第" + i + "次");
            blogService.save(blog);
        }
        return Result.success("success");
    }
}
