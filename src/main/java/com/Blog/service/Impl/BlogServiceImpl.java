package com.Blog.service.Impl;

import com.Blog.dao.BlogMapper;
import com.Blog.pojo.Blog;
import com.Blog.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
