package com.Blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Blog implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private Long userId;

    private String title;

    private String description;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")//格式化时间
    private LocalDateTime created;

    private Integer status;
}
