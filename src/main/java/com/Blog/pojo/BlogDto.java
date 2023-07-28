package com.Blog.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "BlogDto", description = "博客数据传输体")
public class BlogDto extends Blog implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "分类名")
    private String categoryname;
}
