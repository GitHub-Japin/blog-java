package com.Blog.pojo;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2022-05-04
 */
@Getter
@Setter
@ApiModel(value = "Comment", description = "评论实体")
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("评论人id")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty("评论人名")
    private String username;

    @ApiModelProperty("关联文章的id")
    private Long blogId;

    @ApiModelProperty("顶级评论id")
    private Long oid;

    @ApiModelProperty("父级评论id")
    private Long pid;

    @TableField("评论内容")
    private String context;

    @ApiModelProperty("评论时间")
    private String created;

    @ApiModelProperty("父节点的用户id")
    @TableField(exist = false)
    private Long pUserId;
    @ApiModelProperty("父节点的用户昵称")
    @TableField(exist = false)
    private String pUsername;
    @TableField(exist = false)
    private List<Comment> children;

}
