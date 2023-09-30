package com.Blog.service;

import com.Blog.common.Result;
import com.Blog.model.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService  extends IService<Comment> {
    Result<String> saveComment(Comment comment);

    Result<String> removeComment(Long commentId);

    List<Comment> findAllByBlogId(Long blogId);
    Map<String,List<Comment>> list(Long blogId);
}
