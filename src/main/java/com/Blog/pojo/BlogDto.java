package com.Blog.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogDto extends Blog implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String categoryname;
}
