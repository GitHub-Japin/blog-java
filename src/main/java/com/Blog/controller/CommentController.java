package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.model.pojo.Comment;
import com.Blog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping
    public Result<String> save(@RequestBody Comment comment){
        return commentService.saveComment(comment);
    }

    @GetMapping
    public Map<String,List<Comment>> list(@RequestParam Long blogId){
        return commentService.list(blogId);
    }
}
