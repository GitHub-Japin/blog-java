package com.Blog.service.Impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.Blog.common.CustomException;
import com.Blog.dao.CategoryMapper;
import com.Blog.pojo.Blog;
import com.Blog.pojo.Category;
import com.Blog.pojo.User;
import com.Blog.service.BlogService;
import com.Blog.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogService blogService;

    @Override
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
