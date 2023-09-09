package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.constants.ResultConstant;
import com.Blog.model.pojo.Comment;
import com.Blog.model.pojo.User;
import com.Blog.service.CommentService;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @RequiresAuthentication
    @GetMapping
    public Map<String,List<Comment>> list(@RequestParam Long blogId){
        return commentService.list(blogId);
    }
}
