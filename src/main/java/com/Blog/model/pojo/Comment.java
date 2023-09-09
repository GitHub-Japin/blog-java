package com.Blog.model.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2022-05-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Comment", description = "评论实体")
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论人名")
    private String userName;

    @ApiModelProperty("评论人id")
    private Long userId;

    @ApiModelProperty("关联文章的id")
    private Long blogId;

    @ApiModelProperty("父级评论id")
    private Long pid;

    @ApiModelProperty("回复人")
    private String target;

    @ApiModelProperty("评论时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date created;

    @ApiModelProperty("头像")
    @TableField(exist = false)
    private String avatar;

    @ApiModelProperty("本评论下的子评论")
    @TableField(exist = false)
    private List<Comment> children;

}
