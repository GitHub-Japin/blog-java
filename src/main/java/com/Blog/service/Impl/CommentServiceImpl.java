package com.Blog.service.Impl;

import com.Blog.common.Result;
import com.Blog.constants.ResultConstant;
import com.Blog.dao.CommentMapper;
import com.Blog.model.pojo.Comment;
import com.Blog.model.pojo.User;
import com.Blog.service.CommentService;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserService userService;

    @Override
    @RequiresAuthentication//需要在登录认证完成
    public Result<String> saveComment(Comment comment) {
        /*if (comment.getContent() == null || StringUtils.isEmpty(comment.getContent())) {
            return Result.error(ResultConstant.FailMsg);
        }*/
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        comment.setUserId(user.getId());
        comment.setUserName(user.getUsername());
        comment.setAvatar(user.getAvatar());
        comment.setCreated(new Date());
        save(comment);
        return Result.success(ResultConstant.SuccessMsg);
    }

    @Override
    @RequiresAuthentication//需要在登录认证完成
    public Result<String> removeComment(Long commentId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Comment comment = getById(commentId);
//        log.info("comment{}", comment.getUserId());
//        log.info("{}", user.getId());
        if ((!user.getId().equals(comment.getUserId())) || (user.getId() != 1)) {
            return Result.error(ResultConstant.NOTAUTHCOMMENT);
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPid, commentId).select(Comment::getPid);
        List<Comment> list = list(queryWrapper);
        if (list.size() > 1) {
            if (user.getId() != 1L) {
                return Result.error(ResultConstant.NOTAUTHCOMMENT);
            }
        }
        if (removeById(commentId) || remove(queryWrapper)) {
            return Result.success(ResultConstant.SuccessMsg);
        } else {
            return Result.error(ResultConstant.FailMsg);
        }
    }


    @Override
    public List<Comment> findAllByBlogId(Long blogId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("blog_id", blogId);//select * from comment where blog_id=?
        return commentMapper.selectList(wrapper);
    }

    @Override
    public Map<String, List<Comment>> list(Long blogId) {
        Map<String, List<Comment>> map = new HashMap<>();
        //找出本博客下所有评论
        List<Comment> comments = findAllByBlogId(blogId);
        List<Comment> resComments = new ArrayList<>();
        //设置父级头像
        for (Comment comment : comments) {
            comment.setAvatar(userService.getById(comment.getUserId()).getAvatar());
            resComments.add(comment);
        }
        //过滤pid为空的评论出来为父级评论、放置root评论的子评论集合
        List<Comment> rootComments = resComments.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        for (Comment rootComment : rootComments) {
            List<Comment> commentList = new ArrayList<>();
            setChildrenComments(rootComment.getId(), commentList);
            rootComment.setChildren(commentList);
        }
        map.put("comments", rootComments);
        return map;
    }

    public void setChildrenComments(Long parentId, List<Comment> comments) {
        List<Comment> childrenComments = getChildrenComments(parentId);
        for (Comment comment : childrenComments) {
            comment.setAvatar(userService.getById(comment.getUserId()).getAvatar());
        }
        comments.addAll(childrenComments);
        for (Comment childrenComment : childrenComments) {
            setChildrenComments(childrenComment.getId(), comments);
        }
    }

    private List<Comment> getChildrenComments(Long parentId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", parentId);
        return commentMapper.selectList(wrapper);
    }
}
