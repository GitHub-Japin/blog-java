package com.Blog.model.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
//@Table(name="user")
@ApiModel(value = "User",description = "用户实体")
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @Excel(name = "编号", orderNum = "0", width = 5)
    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名", orderNum = "1", width = 15, isImportField = "true")
    private String username;

    @ApiModelProperty(value = "头像")
    @Excel(name = "头像", orderNum = "2", width = 15, isImportField = "true")
    private String avatar;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱", orderNum = "3", width = 15, isImportField = "true")
    @Email(message = "请输入正确的邮箱")
    private String email;

    @ApiModelProperty(value = "密码")
    @Excel(name = "密码", orderNum = "4", width = 15, isImportField = "true")
    private String password;

    @ApiModelProperty(value = "salt")
    @Excel(name = "salt", orderNum = "4", width = 15, isImportField = "true")
    private String salt;

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态", orderNum = "5", width = 10, type = 10, isImportField = "true")
    private Integer status;

    @ApiModelProperty(value = "创建日期")
    @Excel(name = "创建日期", orderNum = "6", width = 15, format = "yyyy-MM-dd", isImportField = "true")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
