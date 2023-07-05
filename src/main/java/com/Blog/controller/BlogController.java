package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.pojo.Blog;
import com.Blog.pojo.BlogDto;
import com.Blog.pojo.Category;
import com.Blog.pojo.User;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RestController
public class BlogController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private UserService userService;

   /*//博客分页查询(主页，服务端)
    @GetMapping("/blogs/page")
    public Result<Page<Blog>> page(int currentPage, int pageSize) {
        Page<Blog> pages = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Blog::getStatus, 0);//0为启用
        blogService.page(pages, lqw);
        return Result.success(pages);
    }*/

    //博客分页查询(主页，服务端)
    @GetMapping("/blogs/page")
    @Cacheable(value = "blogsCache",key="#currentPage+'_'+#pageSize+'_'+#title")
    public Result<Page<BlogDto>> clientPage(int currentPage,int pageSize, String title) {
        Page<Blog> pages = new Page<>(currentPage, pageSize);
        Page<BlogDto> requestPage = new Page<>();
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Blog::getStatus, 0);//0为启用
        lqw.like(StringUtils.hasLength(title), Blog::getTitle, title);
        blogService.page(pages, lqw);

        return getPageResult(pages, requestPage);
    }
   /* //博客分页查询(后台)
    @GetMapping("/blog/page")
    public Result<Page<Blog>> page(int currentPage, int pageSize, String title) {
        Page<Blog> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(title), Blog::getTitle, title);
        blogService.page(page, lqw);
        return Result.success(page);
    }*/
    //博客分页查询(后台)
    @GetMapping("/blog/page")
    @Cacheable(value = "blogsCache",key="#currentPage+'_'+#pageSize+'_'+#title")
    public Result<Page<BlogDto>> serverPage(int currentPage,int pageSize, String title) {
        Page<Blog> pages = new Page<>(currentPage, pageSize);
        Page<BlogDto> requestPage = new Page<>();
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(title), Blog::getTitle, title);
        blogService.page(pages, lqw);

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

    //根据id查看博客
    @GetMapping("/blog/{id}")
    public Result<Blog> detail(@PathVariable Long id) {
        Blog blog = blogService.getById(id);
        return Result.success(blog);
    }

    //博客的编辑
    @RequiresAuthentication//需要在登录认证完成
    @PostMapping("/blog/edit")
    @CacheEvict(value = "blogsCache",allEntries = true)//删除所有缓冲数据
    public Result<String> editBlog(@RequestBody Blog blog) {
        Blog targetBlog;
        //当点击博客路由时 即为修改
        if (blog.getId() != null) {
            targetBlog = blogService.getById(blog.getId());
        } else {
            //当点击发表博客 即为添加
            targetBlog = new Blog();
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            targetBlog.setCategoryId(0L);
            targetBlog.setUserId(user.getId());
            targetBlog.setStatus(0);
            targetBlog.setCreated(LocalDateTime.now());
        }
        BeanUtils.copyProperties(blog, targetBlog, "id", "userId", "created", "status");
        //将博客添加/更新
        blogService.saveOrUpdate(targetBlog);
        return Result.success("编辑成功");
    }

    //后台查看博客内容
    @RequiresAuthentication
    @GetMapping("/blog/getContent")
    public Result<String> getBlogContent(Long id) {
        Blog blog = blogService.getById(id);
        if (blog == null) return Result.error("获取失败");
        return Result.success(blog.getContent());
    }


    @RequiresAuthentication
    @PutMapping("/blog/updateStatus")
    @CacheEvict(value = "blogsCache",allEntries = true)//删除所有缓冲数据
    public Result<String> updateStatus(Long id, int status) {
        Blog blog = blogService.getById(id);
        if (blog == null) return Result.error("博客不存在");
        blog.setStatus(status);
        blogService.updateById(blog);
        return Result.success("博客状态已更新");
    }

    @RequiresAuthentication
    @DeleteMapping("/blog/delete/{id}")
    @CacheEvict(value = "blogsCache",allEntries = true)//删除所有缓冲数据
    public Result<String> deleteBlog(@PathVariable Long id) {
        log.error("deleteBlog被调用了");
        Blog blog = blogService.getById(id);
        if (blog.getId() == null) return Result.error("博客不存在");
        blogService.removeById(id);
        return Result.success("博客已删除");
    }
}
