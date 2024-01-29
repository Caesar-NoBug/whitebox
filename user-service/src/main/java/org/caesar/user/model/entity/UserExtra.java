package org.caesar.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserExtra {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户账户余额
     */
    private Integer balance;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 用户生日
     */
    private LocalDate birth;

    /**
     * 用户所在地址
     */
    private String location;

    /**
     * 用户个性签名
     */
    private String sign;

    /**
     * 用户标签
     */
    private String tag;

    /**
     * 用户关注数
     */
    private Integer followCount;

    /**
     * 用户粉丝数
     */
    private Integer fansCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 热度
     */
    private Integer readCount;

    /**
     * 用户最后一次登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 用户偏好标签(以'/'分隔)
     */
    private String preference;

    /**
     * 用户职业
     */
    private String occupation;
}
