package com.Blog.controller;

import cn.hutool.core.date.DateUtil;
import com.Blog.common.Result;
import com.Blog.model.pojo.Comment;
import com.Blog.model.pojo.User;
import com.Blog.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result<String> save(@RequestBody Comment comment) {
        if (comment.getId() == null) {
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            comment.setUserId(user.getId());
            comment.setCreated(DateUtil.now());
            if (comment.getPid() != null) {
//                判断如果是回复进行处理
//                找到评论的父id
                Long pid = comment.getPid();
//                        找到父评论
                Comment pComment = commentService.getById(pid);
//                如果存在当前父评论的祖宗
                if (pComment.getOid() != null) {//如果当前回复的父级有祖宗，那么就设置相同的祖宗
//                将父评论的祖宗id赋值给当前评论的祖宗id
                    comment.setOid(pComment.getOid());
                } else {
                    //否则就将父评论id设置为当前评论的祖宗id
                    comment.setOid(comment.getPid());
                }
            }
        }
        if(commentService.saveOrUpdate(comment)){
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/del/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        commentService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    @GetMapping
    public Result<List<Comment>> findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/tree/{articleId}")
    public Result<List<Comment>> findTree(@PathVariable Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getBlogId, articleId);
        List<Comment> articleComments = commentService.list(wrapper);//根据文章id查询所有的评论和回复数据
//        查询评论（不包括回复） 过滤得到祖宗id为空的评论
        List<Comment> originList = articleComments.stream().filter(comment -> comment.getOid() == null).collect(Collectors.toList());//表示回复对象
//        设置评论数据的子节点，也就是回复内容
        for (Comment origin : originList) {
//                过滤得到回复的祖宗id等于评论的id
            List<Comment> comments = articleComments.stream().filter(comment -> origin.getId().equals(comment.getOid())).collect(Collectors.toList());
            System.out.println("============================");
            for (Comment comment : comments) {
                System.out.println(comment);
            }
            System.out.println("==============================");
            comments.forEach(comment -> {
//                如果存在回复的父id，给回复设置其父评论的用户id和用户昵称，这样评论就有能@的人的用户id和昵称
//                v相当于过滤得到的父评论对象
                Optional<Comment> pComment = articleComments.stream().filter(c1 -> c1.getId().equals(comment.getPid())).findFirst();
                pComment.ifPresent((v -> {
//                                找到父级评论的用户id和用户昵称，并设置当前的回复对象
                    comment.setPUserId(v.getUserId());
                    comment.setPUsername(v.getUsername());
                }));
            });
            origin.setChildren(comments);
        }
        return Result.success(originList);
    }

    @GetMapping("/{id}")
    public Result<Comment> findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping("/page")
    public Result<Page<Comment>> findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    private User getUser() {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }
}
