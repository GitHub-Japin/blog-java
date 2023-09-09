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
    @Resource
    private UserService userService;

    @RequiresAuthentication//需要在登录认证完成
    @PostMapping
    public Result<String> save(@RequestBody Comment comment){
        if(comment.getContent()==null|| StringUtils.isEmpty(comment.getContent())){
            return Result.fail(400,"评论不能为空","评论不能为空");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        comment.setUserId(user.getId());
        comment.setUserName(user.getUsername());
        comment.setAvatar(user.getAvatar());
        comment.setCreated(new Date());
        commentService.save(comment);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @GetMapping
    public Map<String,Object> list(@RequestParam Long blogId){
        Map<String,Object> map = new HashMap<>();
        //找出本博客下所有评论
        List<Comment> comments = commentService.findAllByBlogId(blogId);
        List<Comment> resComments = new ArrayList<>();
        log.error("=======================================>{}",comments);
        for(Comment comment : comments){
            comment.setAvatar(userService.getById(comment.getUserId()).getAvatar());
            resComments.add(comment);
        }
        log.error("=======================================>{}",resComments);
        List<Comment> rootComments = resComments.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());//过滤pid为空的评论出来为子评论
        for (Comment rootComment : rootComments) {
            rootComment.setChildren(comments.stream().filter(comment -> rootComment.getId().equals(comment.getPid())).collect(Collectors.toList()));//放置root评论的子评论集合
        }
        map.put("comments",rootComments);
        return map;
    }
}
