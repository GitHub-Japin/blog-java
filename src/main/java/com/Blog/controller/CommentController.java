package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.constants.ResultConstant;
import com.Blog.model.pojo.Comment;
import com.Blog.model.pojo.User;
import com.Blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
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

    @PostMapping
    public Result<String> save(@RequestBody Comment comment){
        log.error("Comment{}",comment);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        comment.setUserId(user.getId());
        comment.setUserName(user.getUsername());
        comment.setCreated(new Date());
        commentService.save(comment);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @GetMapping
    public Map<String,Object> list(@RequestParam Long blogId){
        Map<String,Object> map = new HashMap<>();
        List<Comment> comments = commentService.findAllByBlogId(blogId);
        List<Comment> rootComments = comments.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        for (Comment rootComment : rootComments) {
            rootComment.setChildren(comments.stream().filter(comment -> rootComment.getId().equals(comment.getPid())).collect(Collectors.toList()));
        }
        map.put("comments",rootComments);
        return map;
    }
}
