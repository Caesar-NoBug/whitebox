package org.caesar.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName sys_user_base
 */
@TableName(value ="sys_user_base")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseUser implements Serializable {
    /**
     * 主键，用户唯一标识
     */
    @TableId
    private Long id;
    /**
     * 用户名，不多于20个字符
     */
    private String username;

    /**
     * 密码，使用MD5加密
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 验证码，用于校验邮箱登录和手机号登录
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String code;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户微信id
     */
    @JsonIgnore
    private String wxid;

    /**
     * 用户QQid
     */
    @JsonIgnore
    private String qqid;

    /**
     * 用户头像路径
     */
    private String avatarUrl;


    /**
     * 创建账号时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 账号上次更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 账号状态：0:离线 1:在线 2:封禁中
     */
    @JsonIgnore
    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}