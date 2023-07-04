package com.Blog.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
//@Table(name="user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @Excel(name = "编号",orderNum = "0" ,width = 5)
    private Long id;

    @Excel(name = "姓名",orderNum = "1" ,width = 15,isImportField = "true")
    private String username;

    @Excel(name = "头像",orderNum = "2" ,width = 15,isImportField = "true")
    private String avatar;

    @Excel(name = "邮箱",orderNum = "3" ,width = 15,isImportField = "true")
    private String email;

    @Excel(name = "密码",orderNum = "4" ,width = 15,isImportField = "true")
    private String password;

    @Excel(name = "状态",orderNum = "5" ,width = 10,type = 10,isImportField = "true")
    private Integer status;

    @Excel(name = "创建日期",orderNum = "6" ,width = 15,format = "yyyy-MM-dd",isImportField = "true")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @TableLogic
    @TableField(fill= FieldFill.INSERT)
    private Integer deleted;
}
