package com.Blog.service.Impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.Blog.annotation.MyLog;
import com.Blog.common.CustomException;
import com.Blog.common.Result;
import com.Blog.dao.CategoryMapper;
import com.Blog.model.pojo.Blog;
import com.Blog.model.pojo.Category;
import com.Blog.model.pojo.User;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogService blogService;

    @Override
    @RequiresAuthentication
    @MyLog(name = "分类分页请求")
    public Result<Page<Category>> page(int currentPage, int pageSize, String title) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.hasLength(title), Category::getCategoryname, title);
        page(page, lqw);
        return Result.success(page);
    }

    @Override
    @MyLog(name = "分类删除请求")
    @RequiresAuthentication
    @Transactional
    public Result<String> deleteCategory(Long id) {
        remove(id);
        return Result.success("分类删除成功");
    }

    @Override
    @MyLog(name = "分类添加请求")
    @RequiresAuthentication
    @Transactional
    public Result<String> saveCategory(Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getCategoryname, category.getCategoryname());
        Category c = getOne(lqw);//原本数据库对象
        if (c !=null){
            return Result.error("分类已存在");
        }
        category.setCreated(LocalDateTime.now());
        save(category);
        return Result.success("分类添加成功");
    }

    @Override
    @MyLog(name = "分类修改请求")
    @RequiresAuthentication
    @Transactional
    public Result<String> updateCategory(Category category) {
        //log.info(category.getCategoryname());
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getId, category.getId());
        Category initCategory = getOne(lqw);//原本数据库对象
        if (initCategory ==null){
            return Result.error("分类不存在");
        }
        if(initCategory.getCategoryname().equals(category.getCategoryname())){
            return Result.error("当前信息未修改");
        }
        initCategory.setCategoryname(category.getCategoryname());

        if(updateById(initCategory)){
            return Result.success("信息更新成功");
        }else {
            return Result.error("信息更新失败");
        }
    }

    @Override
    public Result<List<Category>> categoryList(Category category) {
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();//构造条件构造器
        query.orderByAsc(Category::getId).orderByDesc(Category::getCreated);//排序，若前面相同，则按后面
        List<Category> list = list(query);
        return Result.success(list);
    }

    @Override
    @RequiresAuthentication
    public void remove(Long id) {//根据id删除分类前需要判断
        //查询分类是否关联菜品、套餐
        LambdaQueryWrapper<Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(Blog::getCategoryId,id);//查询条件
        int count = blogService.count(blogWrapper);
        if (count > 0) {
            throw new CustomException("该分类已关联了博客，不能删除");
        }
        super.removeById(id);
    }

    @Override
    @MyLog(name = "分类excel下载请求")
    public void downLoadXlsxWithEayPoi(HttpServletRequest request, HttpServletResponse response) {
        //        查询用户数据
        try {
            List<Category> userList = categoryMapper.selectList(new QueryWrapper<>());
            //指定导出的格式是高版本的格式
            ExportParams exportParams = new ExportParams("员工信息", "数据", ExcelType.XSSF);
            //        直接使用EasyPOI提供的方法
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, userList);
            String filename = "用户信息.xlsx";
            //            设置文件的打开方式和mime类型
            ServletOutputStream outputStream = null;

            outputStream = response.getOutputStream();

            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
