package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.pojo.Blog;
import com.Blog.model.dto.BlogDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BlogService extends IService<Blog> {
    Result<Page<BlogDto>> clientPage(int currentPage, int pageSize, String title);
    Result<Page<BlogDto>> serverPage(int currentPage,int pageSize, String title);
    Result<Blog> ViewDetails(Long id);
    Result<String> getBlogContent(Long id);
    Result<String> editBlog(Blog blog);
    Result<String> updateStatus(Long id, int status);
    Result<String> deleteBlog(Long id);
}
