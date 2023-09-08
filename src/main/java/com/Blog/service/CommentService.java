package com.Blog.service;

import com.Blog.model.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService  extends IService<Comment> {
    List<Comment> findAllByBlogId(Long blogId);
}
