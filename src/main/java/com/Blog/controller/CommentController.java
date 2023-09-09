package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.Comment;
import com.Blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;


    @RequiresAuthentication//需要在登录认证完成
    @PostMapping
    public Result<String> save(@RequestBody Comment comment){
        return commentService.saveComment(comment);
    }

//    @RequiresAuthentication
    @GetMapping
    public Map<String,List<Comment>> list(@RequestParam Long blogId){
        return commentService.list(blogId);
    }
}
