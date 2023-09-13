package com.Blog.service.Impl;

import com.Blog.annotation.MyLog;
import com.Blog.common.Result;
import com.Blog.dao.BlogMapper;
import com.Blog.model.pojo.Blog;
import com.Blog.model.dto.blog.BlogDto;
import com.Blog.model.pojo.Category;
import com.Blog.model.pojo.User;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Override
    //@MyLog(name = "客户端分页请求")
    @Cacheable(value = "blogsCache",key="#currentPage+'_'+#pageSize+'_'+#title")
    public Result<Page<BlogDto>> clientPage(int currentPage, int pageSize, String title) {
        Page<Blog> pages = new Page<>(currentPage, pageSize);
        Page<BlogDto> requestPage = new Page<>();
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Blog::getStatus, 0);//0为启用
        lqw.like(StringUtils.hasLength(title), Blog::getTitle, title);
        page(pages, lqw);
        return getPageResult(pages, requestPage);
    }

    @Override
    @RequiresAuthentication
    @MyLog(name = "管理端分页请求")
    @Cacheable(value = "blogsCache",key="#currentPage+'_'+#pageSize+'_'+#title")
    public Result<Page<BlogDto>> serverPage(int currentPage, int pageSize, String title) {
        Page<Blog> pages = new Page<>(currentPage, pageSize);
        Page<BlogDto> requestPage = new Page<>();
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(title), Blog::getTitle, title);
        page(pages, lqw);
        return getPageResult(pages, requestPage);
    }

    //拷贝分页page属性
    private Result<Page<BlogDto>> getPageResult(Page<Blog> pages, Page<BlogDto> requestPage) {
        BeanUtils.copyProperties(pages, requestPage,"records");
        List<Blog> records = pages.getRecords();

        List<BlogDto> list = records.stream().map(item -> {
            BlogDto blogDto = new BlogDto();
            BeanUtils.copyProperties(item, blogDto);
            Long uid = item.getUserId();
            Long cid = item.getCategoryId();
            User user = userService.getById(uid);
            Category category = categoryService.getById(cid);
            String userName = user.getUsername();
            String categoryName = category.getCategoryname();
            blogDto.setUsername(userName);
            blogDto.setCategoryname(categoryName);
            return blogDto;
        }).collect(Collectors.toList());

        requestPage.setRecords(list);
        return Result.success(requestPage);
    }

    @Override
    //@MyLog(name = "文章详情(预览)请求")
    public Result<Blog> ViewDetails(Long id) {
        Blog blog = getById(id);
        return Result.success(blog);
    }

    @Override
    @RequiresAuthentication
    @MyLog(name = "后端文章点击查看请求")
    public Result<String> getBlogContent(Long id) {
        Blog blog = getById(id);
        if (blog == null) return Result.error("获取失败");
        return Result.success(blog.getContent());
    }

    @Override
    @Transactional
    @CacheEvict(value = "blogsCache", allEntries = true)//删除所有缓冲数据
    @RequiresAuthentication//需要在登录认证完成
    @MyLog(name = "文章编辑请求")
    public Result<String> editBlog(Blog blog) {
        Blog targetBlog;
        //当点击博客路由时 即为修改
        if (blog.getId() != null) {
            targetBlog = getById(blog.getId());
        } else {
            //当点击发表博客 即为添加
            targetBlog = new Blog();
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            targetBlog.setUserId(user.getId());
            targetBlog.setStatus(0);
            targetBlog.setCreated(new Date());
        }
        BeanUtils.copyProperties(blog, targetBlog, "id", "userId", "created", "status");
        //将博客添加/更新
        saveOrUpdate(targetBlog);
        return Result.success("编辑成功");
    }

    @Override
    @MyLog(name = "文章上下架请求")
    @RequiresAuthentication
    @Transactional
    @CacheEvict(value = "blogsCache", allEntries = true)//删除所有缓冲数据
    public Result<String> updateStatus(Long id, int status) {
        Blog blog = getById(id);
        if (blog == null) return Result.error("博客不存在");
        blog.setStatus(status);
        updateById(blog);
        return Result.success("博客状态已更新");
    }

    @Override
    @MyLog(name = "文章删除请求")
    @RequiresAuthentication
    @Transactional
    @CacheEvict(value = "blogsCache", allEntries = true)//删除所有缓冲数据
    public Result<String> deleteBlog(Long id) {
        //log.error("deleteBlog被调用了");
        Blog blog = getById(id);
        if (blog.getId() == null) return Result.error("博客不存在");
        removeById(id);
        return Result.success("博客已删除");
    }
}
